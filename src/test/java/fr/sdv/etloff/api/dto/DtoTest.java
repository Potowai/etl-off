package fr.sdv.etloff.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DtoTest {

    @Test
    void elementCountDto() {
        ElementCountDto dto = new ElementCountDto("sucre", 42L);
        assertEquals("sucre", dto.nom());
        assertEquals(42L, dto.count());
    }

    @Test
    void etlRunResponse() {
        EtlRunResponse r = new EtlRunResponse("OK", "Import lancé");
        assertEquals("OK", r.status());
        assertEquals("Import lancé", r.message());
    }
}
