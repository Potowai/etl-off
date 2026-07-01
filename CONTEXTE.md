# Contexte du projet

## Objectif

Construire un **ETL** (Extract, Transform, Load) capable d'importer le fichier CSV **Open Food Facts** (~13 000 lignes, 30 colonnes) dans une base de données H2, le plus rapidement possible (< 3min 45s imposé par le sujet), puis d'exposer les données via une API REST.

## Contraintes

- Fichier CSV au format **pipe-delimited** (`|`) avec des données utilisateur non normalisées (parenthèses, pourcentages, caractères spéciaux)
- Les marques, catégories, ingrédients, allergènes et additifs doivent être stockés dans des **tables séparées** avec relations N-N
- L'import doit être **parallélisé** (contrainte de temps)
- L'API doit permettre de consulter les produits triés par **Nutri-Score** puis **énergie**

## Décisions techniques

### 1. Parallélisation avec Spring Batch (Partition)

Spring Batch permet de découper un fichier CSV en **N partitions** (ici 8), chacune lue et traitée par un thread virtuel indépendant.

Chaque partition reçoit :
- `startLine` / `endLine` : les indices de lignes à lire
- `startByte` : l'offset dans le fichier pour un seek direct (évite de relire depuis le début)

### 2. Préchargement des références (anticiper les conflits)

**Problème :** en parallèle, 8 threads insèrent des `Produit` liés à des `Categorie`/`Marque`/etc. Si deux threads créent la même catégorie "Boissons" simultanément → violation de clé unique.

**Solution :** un step unique (`preloadReferencesStep`) qui :
1. Scanne l'intégralité du CSV
2. Extrait toutes les valeurs uniques de catégories, marques, ingrédients, allergènes, additifs
3. Les sauvegarde en base (`saveAll`)
4. Les maintient dans un `ConcurrentHashMap` (cache mémoire)

Les workers lisent ensuite depuis ce cache — pas de concurrence BDD.

### 3. Threading avec Virtual Threads (Project Loom)

Java 21 + Spring Boot 3.4 permet d'utiliser des **virtual threads** au lieu des threads platformes.

Avantages :
- Pas de limite à ~200 threads OS : on peut lancer 8 partitions + Tomcat + tout le reste sans saturation
- Chaque requête HTTP bloquante (lecture fichier, BDD) ne bloque pas un thread OS
- Configuré via `spring.threads.virtual.enabled: true`

### 4. Record Java au lieu de classe JavaBean

`CsvProductRecord` est un `record` Java (40 lignes) plutôt qu'une classe avec 30 champs + getters + setters (304 lignes).
- Constructeur, `equals()`, `hashCode()`, `toString()` générés automatiquement
- Accesseurs en `record.nom()` au lieu de `record.getNom()`

### 5. Parsing CSV optimisé

- Découpage sur `|` avec une **boucle manuelle** plutôt que `String.split()` (regex ≈ 10× plus lent)
- Nettoyage des tokens ingrédients/allergènes/additifs : suppression des parenthèses, pourcentages, caractères spéciaux (`*`, `_`, `"`, `'`)
- Split intelligent : détection automatique du séparateur (`,`, `;`, ` - `)

### 6. Formats de lancement

Deux modes :
- **Mode API** (par défaut) : démarre Tomcat, attend les requêtes
- **Mode CLI** (`--etl.run=true`) : désactive Tomcat, importe le CSV, puis s'arrête

## Structure du code

```
src/main/java/fr/sdv/etloff/
├── EtlOffApplication.java         # Point d'entrée Spring Boot
├── config/
│   ├── BatchConfig.java           # Job Spring Batch + Virtual Threads + DataSource
│   ├── EtlProperties.java         # Propriétés personnalisées (csv-path, chunk-size)
│   └── ImportJobListener.java     # Logs début/fin d'import
├── domain/
│   ├── Categorie.java, Marque.java, Ingredient.java
│   ├── Allergene.java, Additif.java
│   └── Produit.java               # Entité principale (relations N-N)
├── dao/
│   ├── CategorieDao, MarqueDao, IngredientDao
│   ├── AllergeneDao, AdditifDao
│   └── ProduitDao                 # Requêtes SQL natives (topByBrand, etc.)
├── parser/
│   └── CsvLineParser.java         # Parsing CSV + nettoyage des tokens
├── etl/
│   ├── CsvFileAccess.java         # Indexation des offsets du fichier CSV
│   ├── CsvProductRecord.java      # Record représentant une ligne CSV
│   ├── PartitionedCsvLineReader.java  # Lecture seekée par partition
│   ├── LineRangePartitioner.java  # Découpage du fichier en N zones
│   ├── OpenFoodFactsProcessor.java   # Transformation CSV → entité JPA
│   ├── ReferencePreloadTasklet.java  # Préchargement des références
│   ├── ReferenceDataService.java     # Cache ConcurrentHashMap
│   └── EtlCommandLineRunner.java  # Lancement en mode CLI
├── service/
│   ├── ICsvImportService.java     # Interface : lancer l'import
│   ├── IProductAnalyticsService.java # Interface : requêtes API
│   ├── IReferenceDataService.java # Interface : cache références
│   └── impl/
│       ├── CsvImportServiceImpl.java
│       ├── ProductAnalyticsServiceImpl.java
│       └── ReferenceDataServiceImpl.java
└── api/
    ├── ProductAnalyticsController.java  # Endpoints GET
    ├── EtlAdminController.java          # POST /admin/etl/run
    └── dto/ (ProduitDto, ElementCountDto, EtlRunResponse)
```

## Tests

- 12 tests unitaires
- Tests des parsers (formatage CSV, nettoyage des tokens)
- Test d'intégration du contrôleur API (MockMvc + H2)
- Profil `test` avec base H2 dédiée

## Patterns utilisés

- **Service Layer** : interfaces + implémentations séparées (service/impl)
- **Repository** : Spring Data JPA avec requêtes SQL natives
- **Strategy** (implicite) : `CsvLineParser` choisit la stratégie de split selon le séparateur
- **Template Method** (implicite) : Spring Batch gère le workflow du job
- **Singleton** : `ReferenceDataService` avec cache ConcurrentHashMap

## Dépendances

| Technologie | Version | Usage |
|-------------|---------|-------|
| Spring Boot | 3.4.1 | Framework principal |
| Spring Batch | 5.x | Import CSV partitionné |
| Spring Data JPA | 3.x | ORM / Repository |
| H2 | 2.3.x | Base de données mémoire |
| Hibernate | 6.6.x | JPA provider |
| Java | 21 | Virtual Threads, Records |
| Maven | 3.9+ | Build |
