# Décisions techniques

Ce document explique chaque choix d'architecture et pourquoi il a été fait, avec les résultats de benchmark qui les justifient.

## Sommaire

- [Spring Batch vs Virtual Threads](#spring-batch-vs-virtual-threads)
- [Préchargement des références](#préchargement-des-références)
- [Base H2 fichier vs mémoire](#base-h2-fichier-vs-mémoire)
- [Record Java vs classe JavaBean](#record-java-vs-classe-javabean)
- [Lazy initialization](#lazy-initialization)
- [Parsing CSV optimisé](#parsing-csv-optimisé)
- [Service / Impl](#service--impl)
- [Virtual Threads pour le batch](#virtual-threads-pour-le-batch)

---

## Spring Batch vs Virtual Threads

### Le constat

Le projet final du cours (Partie 9) propose 4 stratégies d'ingestion. Nous avons implémenté et benchmarké les 2 plus pertinentes pour notre stack :

| Stratégie | Code | Débit |
|---|---|---|
| **Spring Batch partitionné** (Option C) | `SpringBatchPipeline` | ~1 820 enreg./s |
| **Virtual Threads + JDBC** (Option A) | `VirtualThreadPipeline` | ~860 enreg./s |

### Pourquoi Spring Batch est plus rapide

Spring Batch apporte 3 optimisations que l'implémentation VT naïve n'a pas :

1. **Chunk transactionnels** : Spring Batch groupe les INSERT par chunk (500-1000 lignes) dans une seule transaction. Moins de commits = moins d'écriture disque.
2. **JPA batch** : Hibernate groupe les `INSERT` avec `hibernate.jdbc.batch_size`, ce qui fusionne plusieurs `INSERT` en un seul appel JDBC. Notre implémentation VT envoie chaque ligne individuellement.
3. **Orchestration intégrée** : le partitionnement, le retry, le skip sont gérés par le framework sans code supplémentaire.

### Résultat

**Spring Batch est ~2× plus rapide** que l'implémentation VT naïve sur ce jeu de données. Le gain viendrait du batch JDBC + transactionnel, pas du threading lui-même (les deux utilisent des virtual threads).

### Pour aller plus loin

Une implémentation VT optimisée avec `PreparedStatement.addBatch()` + `executeBatch()` manuel pourrait rattraper Spring Batch. Mais cela demanderait de réécrire la gestion transactionnelle et le retry — ce que Spring Batch offre déjà.

---

## Préchargement des références

### Problème

L'import parallélisé insère des `Produit` liés à des `Categorie`, `Marque`, `Ingredient`, `Allergene`, `Additif`. Si 8 threads créent la même catégorie "Boissons" en même temps → violation de contrainte UNIQUE.

### Solution

Un step unique (`preloadReferencesStep`) avant le traitement parallèle :
1. Scanne l'intégralité du CSV
2. Extrait toutes les valeurs uniques de référence
3. Les sauvegarde en base (`saveAll()`)
4. Les met en cache (`ConcurrentHashMap`)

Les workers lisent depuis le cache — aucun accès concurrent aux tables de référence.

### Alternative envisagée

Utiliser `INSERT ... ON CONFLICT DO NOTHING` (PostgreSQL) ou `MERGE INTO` (H2). Rejetée car :
- H2 supporte `MERGE` mais pas avec `saveAll()` JPA
- Le préchargement est plus simple et tout aussi rapide (~6s pour 13k lignes)

---

## Base H2 fichier vs mémoire

### Contexte

Au départ, la base était en mémoire (`jdbc:h2:mem:etloff`). Problème : les données disparaissaient à l'arrêt du processus. Impossible de dissocier l'import (mode CLI) du serveur web (mode API).

### Solution

Passage en base fichier (`jdbc:h2:file:./data/etloff`) :
1. L'import CLI écrit les données dans le fichier
2. Le serveur web lit le même fichier

### Compromis

La base fichier est ~10% plus lente que la base mémoire pour les écritures (contrainte disque). Acceptable car l'import est un batch ponctuel, pas du temps réel.

---

## Record Java vs classe JavaBean

### Avant

`CsvProductRecord` était une classe avec 30 champs, 30 getters, 30 setters = **304 lignes** de code purement technique.

### Après

Conversion en `record` Java = **~40 lignes**. Le constructeur, `equals()`, `hashCode()`, `toString()` sont générés par le compilateur.

### Pourquoi c'est mieux

- Moins de code = moins de bugs potentiels
- Plus lisible : la structure est déclarée en 30 lignes au lieu de 300
- Immuable : pas de setter, pas de modification accidentelle

---

## Lazy initialization

### Problème

Spring Boot crée tous les beans au démarrage. Avec ~40 classes + JPA + Batch, le démarrage prend ~5 secondes.

### Solution

```yaml
spring.main.lazy-initialization: true
```

Les beans sont créés à la première utilisation, pas au démarrage. Le temps de démarrage passe de ~5s à ~3s.

### Attention

`EtlCommandLineRunner` doit être marqué `@Lazy(false)` pour être exécuté même en mode lazy. Sans ça, l'import CLI ne se lance jamais.

---

## Parsing CSV optimisé

### Split sans regex

```java
// Avant : String.split("|") → regex, ~10× plus lent
// Après : boucle manuelle sur les caractères
```

`String.split()` compile une regex à chaque appel. Notre `splitPipe()` fait une simple boucle sur les caractères — 10× plus rapide sur 13k lignes.

### Détection intelligente du séparateur

Le format des ingrédients est incohérent (parfois `,`, parfois `;`, parfois ` - `). Le parser détecte automatiquement le séparateur dominant.

### Nettoyage des tokens

Les données utilisateur contiennent des parenthèses (`Farine (blé, soja)`) et des pourcentages (`Sucre 15%`). Trois passes de nettoyage :
1. Suppression du contenu entre parenthèses
2. Suppression des pourcentages et des chiffres qui les précèdent
3. Suppression des caractères spéciaux (`*`, `_`, `"`, `'`)

---

## Service / Impl

### Avant

```java
service/
├── CsvImportService.java
├── ProductAnalyticsService.java
└── ReferenceDataService.java
```

### Après

```java
service/
├── ICsvImportService.java
├── IProductAnalyticsService.java
├── IReferenceDataService.java
└── impl/
    ├── CsvImportServiceImpl.java
    ├── ProductAnalyticsServiceImpl.java
    └── ReferenceDataServiceImpl.java
```

### Pourquoi

- Les contrôleurs (`ProductAnalyticsController`, `EtlAdminController`) dépendent d'interfaces, pas d'implémentations concrètes
- Possible de changer d'implémentation sans toucher aux contrôleurs
- Convention standard Spring : l'interface définit le contrat, l'implémentation est dans `impl/`

---

## Virtual Threads pour le batch

### Config

```yaml
spring.threads.virtual.enabled: true
```

`BatchConfig` utilise `Executors.newVirtualThreadPerTaskExecutor()` pour le step partitionné.

### Impact

Avant (threads platform) : le partitionnement était limité par le nombre de threads OS disponibles (~200 max). Avec les virtual threads, chaque partition a son propre thread sans limite.

Le montage/démontage automatique (quand un thread fait une I/O, il libère son porteur) n'a pas d'impact ici car le batch est CPU-bound sur H2. Mais le code est prêt pour une migration vers PostgreSQL où l'attente réseau serait débloquée par les VT.
