package fr.sdv.etloff.etl;

import java.util.List;

public record CsvProductRecord(
        String categorie,
        String marque,
        String nom,
        String nutritionGradeFr,
        String ingredientsRaw,
        List<String> ingredients,
        Double energie100g,
        Double graisse100g,
        Double sucres100g,
        Double fibres100g,
        Double proteines100g,
        Double sel100g,
        Double vitA100g,
        Double vitD100g,
        Double vitE100g,
        Double vitK100g,
        Double vitC100g,
        Double vitB1100g,
        Double vitB2100g,
        Double vitPP100g,
        Double vitB6100g,
        Double vitB9100g,
        Double vitB12100g,
        Double calcium100g,
        Double magnesium100g,
        Double iron100g,
        Double fer100g,
        Double betaCarotene100g,
        Boolean presenceHuilePalme,
        String allergenesRaw,
        List<String> allergenes,
        String additifsRaw,
        List<String> additifs
) {
}
