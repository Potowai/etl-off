package fr.sdv.etloff.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EtlPropertiesTest {

    @Test
    void valeursParDefaut() {
        EtlProperties p = new EtlProperties();
        assertEquals("classpath:open-food-facts.csv", p.getCsvPath());
        assertEquals(500, p.getChunkSize());
        assertEquals(8, p.getGridSize());
    }

    @Test
    void setter() {
        EtlProperties p = new EtlProperties();
        p.setCsvPath("/tmp/test.csv");
        p.setChunkSize(100);
        p.setGridSize(2);
        assertEquals("/tmp/test.csv", p.getCsvPath());
        assertEquals(100, p.getChunkSize());
        assertEquals(2, p.getGridSize());
    }
}
