package fr.sdv.etloff.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CategorieTest {

    @Test
    void constructeur() {
        Categorie c = new Categorie("Boissons");
        assertEquals("Boissons", c.getNom());
    }

    @Test
    void egaliteNom() {
        Categorie c1 = new Categorie("Boissons");
        Categorie c2 = new Categorie("Boissons");
        assertEquals(c1.getNom(), c2.getNom());
    }
}
