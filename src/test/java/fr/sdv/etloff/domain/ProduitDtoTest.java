package fr.sdv.etloff.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import fr.sdv.etloff.api.dto.ProduitDto;

class ProduitDtoTest {

    @Test
    void fromProduit() {
        Produit p = new Produit();
        p.setNom("Test");
        p.setNutritionGradeFr("A");
        p.setEnergie100g(100.0);
        p.setCategorie(new Categorie("Cat"));
        p.setMarque(new Marque("Marque"));

        ProduitDto dto = ProduitDto.from(p);
        assertEquals("Test", dto.nom());
        assertEquals("A", dto.nutritionGradeFr());
        assertEquals("Cat", dto.categorie());
        assertEquals("Marque", dto.marque());
        assertEquals(100.0, dto.energie100g());
    }
}
