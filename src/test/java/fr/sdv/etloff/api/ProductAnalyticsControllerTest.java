package fr.sdv.etloff.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import fr.sdv.etloff.dao.CategorieDao;
import fr.sdv.etloff.dao.MarqueDao;
import fr.sdv.etloff.dao.ProduitDao;
import fr.sdv.etloff.domain.Categorie;
import fr.sdv.etloff.domain.Marque;
import fr.sdv.etloff.domain.Produit;

@SpringBootTest(properties = "spring.batch.job.enabled=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProduitDao produitDao;

    @Autowired
    private CategorieDao categorieDao;

    @Autowired
    private MarqueDao marqueDao;

    @BeforeEach
    void seed() {
        produitDao.deleteAll();
        marqueDao.deleteAll();
        categorieDao.deleteAll();

        Categorie cat = categorieDao.save(new Categorie("Boissons"));
        Marque marque = marqueDao.save(new Marque("Evian"));

        Produit p1 = new Produit();
        p1.setNom("Eau A");
        p1.setNutritionGradeFr("A");
        p1.setEnergie100g(0.0);
        p1.setCategorie(cat);
        p1.setMarque(marque);
        produitDao.save(p1);

        Produit p2 = new Produit();
        p2.setNom("Eau C");
        p2.setNutritionGradeFr("C");
        p2.setEnergie100g(100.0);
        p2.setCategorie(cat);
        p2.setMarque(marque);
        produitDao.save(p2);
    }

    @Test
    void topByBrand() throws Exception {
        mockMvc.perform(get("/products/top-by-brand").param("brand", "Evian").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nutritionGradeFr").value("A"));
    }

    @Test
    void topByBrandNotFound() throws Exception {
        mockMvc.perform(get("/products/top-by-brand").param("brand", "Inconnue").param("limit", "5"))
                .andExpect(status().isNotFound());
    }
}
