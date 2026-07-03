package fr.sdv.etloff.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProduitTest {

    @Test
    void createProduit() {
        Produit p = new Produit();
        p.setNom("Coca-Cola");
        p.setNutritionGradeFr("E");
        p.setEnergie100g(42.0);
        p.setPresenceHuilePalme(false);
        assertEquals("Coca-Cola", p.getNom());
        assertEquals("E", p.getNutritionGradeFr());
        assertEquals(42.0, p.getEnergie100g());
        assertFalse(p.getPresenceHuilePalme());
    }

    @Test
    void relations() {
        Categorie cat = new Categorie("Boissons");
        Marque m = new Marque("Coca-Cola");
        Produit p = new Produit();
        p.setCategorie(cat);
        p.setMarque(m);
        assertEquals("Boissons", p.getCategorie().getNom());
        assertEquals("Coca-Cola", p.getMarque().getNom());
    }

    @Test
    void ajoutIngredient() {
        Produit p = new Produit();
        Ingredient i = new Ingredient("sucre");
        p.addIngredient(i);
        assertTrue(p.getIngredients().contains(i));
    }

    @Test
    void ajoutAllergene() {
        Produit p = new Produit();
        Allergene a = new Allergene("lait");
        p.addAllergene(a);
        assertTrue(p.getAllergenes().contains(a));
    }

    @Test
    void ajoutAdditif() {
        Produit p = new Produit();
        Additif a = new Additif("E330");
        p.addAdditif(a);
        assertTrue(p.getAdditifs().contains(a));
    }
}
