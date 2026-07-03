package fr.sdv.etloff.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AllergeneAdditifIngredientTest {

    @Test
    void ingredient() {
        Ingredient i = new Ingredient("sucre");
        assertEquals("sucre", i.getNom());
    }

    @Test
    void allergene() {
        Allergene a = new Allergene("lait");
        assertEquals("lait", a.getNom());
    }

    @Test
    void additif() {
        Additif a = new Additif("E330");
        assertEquals("E330", a.getNom());
    }

    @Test
    void marque() {
        Marque m = new Marque("Evian");
        assertEquals("Evian", m.getNom());
    }
}
