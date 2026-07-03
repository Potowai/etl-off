package fr.sdv.etloff.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import fr.sdv.etloff.service.IReferenceDataService;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
class ReferenceDataServiceImplTest {

    @Autowired
    private IReferenceDataService service;

    @Test
    void bulkLoadEtFind() {
        service.bulkLoad(
                Set.of("Boissons", "Desserts"),
                Set.of("Coca-Cola", "Danone"),
                Set.of("sucre", "eau"),
                Set.of("lait", "gluten"),
                Set.of("E330", "E250")
        );
        assertNotNull(service.findCategorie("Boissons"));
        assertNotNull(service.findMarque("Danone"));
        assertNotNull(service.findIngredient("sucre"));
        assertNotNull(service.findAllergene("lait"));
        assertNotNull(service.findAdditif("E330"));
    }

    @Test
    void findInexistant() {
        assertNull(service.findCategorie("INEXISTANT"));
        assertNull(service.findMarque("INEXISTANT"));
    }
}
