# etl-off

ETL Open Food Facts — Spring Batch + API REST

Import du CSV Open Food Facts en base H2, avec API REST de consultation.

## Prérequis

- Java 21
- Maven 3.9+

## Lancer l'application

```bash
mvn spring-boot:run
```

## Import CSV

```bash
mvn spring-boot:run -DskipTests -Dspring-boot.run.arguments="--etl.run=true"
```

Ou via l'API :

```bash
curl -X POST http://localhost:8080/admin/etl/run
```

## API REST

| Méthode | Endpoint |
|---------|----------|
| GET | `/products/top-by-brand?brand=X&limit=N` |
| GET | `/products/top-by-category?category=X&limit=N` |
| GET | `/products/top-by-brand-category?brand=X&category=Y&limit=N` |
| GET | `/ingredients/top?limit=N` |
| GET | `/allergens/top?limit=N` |
| GET | `/additives/top?limit=N` |

## Tests

```bash
mvn test
```
