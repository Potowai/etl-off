package fr.sdv.etloff.api.dto;

import fr.sdv.etloff.domain.Produit;

public record ProduitDto(
        Long id,
        String nom,
        String nutritionGradeFr,
        String categorie,
        String marque,
        Double energie100g) {

    public static ProduitDto from(Produit produit) {
        return new ProduitDto(
                produit.getId(),
                produit.getNom(),
                produit.getNutritionGradeFr(),
                produit.getCategorie() != null ? produit.getCategorie().getNom() : null,
                produit.getMarque() != null ? produit.getMarque().getNom() : null,
                produit.getEnergie100g());
    }
}
