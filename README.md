# etl-off

ETL Open Food Facts — Spring Boot 3.4 / Spring Batch / JPA / H2

Import du CSV Open Food Facts (~13 000 produits) en base H2 avec partitionnement parallèle, puis API REST de consultation.

## Stack

- Java 21 + Virtual Threads (Project Loom)
- Spring Boot 3.4.1
- Spring Batch (partitionné en 8 threads)
- Spring Data JPA / Hibernate (batch inserts)
- H2 (base mémoire, mode PostgreSQL)
- Maven 3.9+

## Démarrage rapide

```bash
# Lancer l'application (sans import)
mvn spring-boot:run

# Lancer l'application avec import CSV
mvn spring-boot:run -DskipTests -Dspring-boot.run.arguments="--etl.run=true"

# Lancer le JAR (démarrage plus rapide)
mvn package -DskipTests
java -XX:TieredStopAtLevel=1 -jar target/etl-off-1.0.0-SNAPSHOT.jar
```

## Import CSV

```bash
# En ligne de commande
mvn spring-boot:run -DskipTests -Dspring-boot.run.arguments="--etl.run=true"

# Via l'API (une fois l'app démarrée)
curl -X POST http://localhost:8080/admin/etl/run
```

## API REST

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/products/top-by-brand?brand=X&limit=N` | Top produits d'une marque (tri Nutri-Score + énergie) |
| GET | `/products/top-by-category?category=X&limit=N` | Top produits d'une catégorie |
| GET | `/products/top-by-brand-category?brand=X&category=Y&limit=N` | Top produits par marque + catégorie |
| GET | `/ingredients/top?limit=N` | Ingrédients les plus utilisés |
| GET | `/allergens/top?limit=N` | Allergènes les plus fréquents |
| GET | `/additives/top?limit=N` | Additifs les plus fréquents |

## Architecture

```
config/   → Configuration Spring (Batch, Virtual Threads, propriétés)
domain/   → Entités JPA (Produit, Categorie, Marque, Ingredient, Allergene, Additif)
dao/      → Repository Spring Data (requêtes SQL natives)
parser/   → Parsing du CSV pipe-delimited (30 colonnes)
etl/      → Cœur ETL : partitionnement, préchargement des références, processor
service/  → Interfaces + implémentations (IProductAnalyticsService, etc.)
api/      → Contrôleurs REST + DTOs
```

## Performances

Méthode de lancement              | Temps total | Spring Boot seul
----------------------------------|-------------|-----------------
`mvn spring-boot:run`             | ~16s        | ~5s
`java -jar target/*.jar` (cold)   | ~13s        | ~13s
`java -jar` (warm)                | ~8s         | ~5s

## Tests

```bash
mvn test
```
