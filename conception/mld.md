# Modèle physique de données (MLD)

## Tables

| Table | Clé | Attributs principaux |
|-------|-----|----------------------|
| `categorie` | `id` PK | `nom` UNIQUE |
| `marque` | `id` PK | `nom` UNIQUE |
| `ingredient` | `id` PK | `nom` UNIQUE |
| `allergene` | `id` PK | `nom` UNIQUE |
| `additif` | `id` PK | `nom` UNIQUE |
| `produit` | `id` PK | `nom`, `nutrition_grade_fr`, valeurs nutrition (index CSV 5-27), `presence_huile_palme`, FK `categorie_id`, FK `marque_id` |
| `produit_ingredient` | (`produit_id`, `ingredient_id`) | association N-N |
| `produit_allergene` | (`produit_id`, `allergene_id`) | association N-N |
| `produit_additif` | (`produit_id`, `additif_id`) | association N-N |

## Mapping CSV → colonnes

| Index CSV | Colonne `produit` / table |
|-----------|---------------------------|
| 0 | `categorie.nom` |
| 1 | `marque.nom` |
| 2 | `produit.nom` |
| 3 | `produit.nutrition_grade_fr` |
| 4 | `produit.ingredients_text` + tokens → `ingredient` |
| 5-27 | champs nutritionnels `*_100g` |
| 28 | tokens → `allergene` |
| 29 | tokens → `additif` |
