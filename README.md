# etl-off

ETL Open Food Facts — Spring Boot 3.4 / Spring Batch / JPA / H2

Import du CSV Open Food Facts (~13 000 produits) en base H2 avec partitionnement parallèle, puis API REST de consultation.

👉 [Toutes les décisions techniques sont détaillées ici](DECISIONS.md)

## Stack

- Java 21 + Virtual Threads (Project Loom)
- Spring Boot 3.4.1
- Spring Batch (partitionné en 8 threads)
- Spring Data JPA / Hibernate (batch inserts)
- H2 (base fichier persistée)
- Maven 3.9+

## Démarrage rapide

```bash
# Lancer l'application (sans import)
mvn spring-boot:run

# Lancer l'application avec import CSV
mvn package -DskipTests
java -Detl.run=true -jar target/etl-off-1.0.0-SNAPSHOT.jar

# Puis lancer le serveur web
java -jar target/etl-off-1.0.0-SNAPSHOT.jar
```

## Import CSV

```bash
# En ligne de commande (import seul, s'arrête après)
mvn spring-boot:run "-Dspring-boot.run.arguments=--etl.run=true"

# Via l'API (une fois l'app démarrée)
curl -X POST http://localhost:8080/admin/etl/run
```

## API REST

### Produits

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/products/top-by-brand?brand=X&limit=N` | Top produits d'une marque (tri Nutri-Score + énergie) |
| GET | `/products/top-by-category?category=X&limit=N` | Top produits d'une catégorie |
| GET | `/products/top-by-brand-category?brand=X&category=Y&limit=N` | Top produits par marque + catégorie |
| GET | `/ingredients/top?limit=N` | Ingrédients les plus utilisés |
| GET | `/allergens/top?limit=N` | Allergènes les plus fréquents |
| GET | `/additives/top?limit=N` | Additifs les plus fréquents |

### Benchmark

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/benchmark/run?strategy=BATCH&batchSize=1000&parallelism=4` | Lance l'import avec la stratégie choisie |

Stratégies disponibles : `BATCH` (Spring Batch), `VT` (Virtual Threads + JDBC).

## Architecture

```
config/   → Configuration Spring (Batch, Virtual Threads, propriétés)
domain/   → Entités JPA (Produit, Categorie, Marque, Ingredient, Allergene, Additif)
dao/      → Repository Spring Data (requêtes SQL natives)
parser/   → Parsing du CSV pipe-delimited (30 colonnes)
etl/      → Cœur ETL : partitionnement, préchargement des références, processor
service/  → Interfaces + implémentations (IProductAnalyticsService, etc.)
pipeline/ → Contrat DataIngestionPipeline + implémentations VT et Spring Batch
api/      → Contrôleurs REST + DTOs
```

## Performances

| Métrique | Valeur |
|----------|--------|
| Import 13 432 lignes | **~6s** (Spring Batch) |
| Débit Spring Batch | **~1 820 enreg./s** |
| Débit Virtual Threads + JDBC | **~860 enreg./s** |
| Spring Boot startup | **~4s** |

## Tests

```bash
mvn test
```
