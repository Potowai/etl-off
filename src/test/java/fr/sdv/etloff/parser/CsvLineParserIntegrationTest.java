package fr.sdv.etloff.parser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import fr.sdv.etloff.etl.CsvProductRecord;

class CsvLineParserIntegrationTest {

    @Test
    void parseLigneComplete() {
        String line = "Boissons|Coca-Cola|Coca Zéro|a|Eau gazéifiée, colorant, édulcorants|0.5|0|0|0|0.1|0.01|||||||||||||||||0|||";
        CsvProductRecord r = CsvLineParser.parseLine(line);
        assertNotNull(r);
        assertEquals("Boissons", r.categorie());
        assertEquals("Coca-Cola", r.marque());
        assertEquals("Coca Zéro", r.nom());
        assertEquals("A", r.nutritionGradeFr());
        assertEquals(0.5, r.energie100g());
    }

    @Test
    void parseDoubleAvecVirgule() {
        Double d = CsvLineParser.parseDouble("1,5");
        assertEquals(1.5, d);
    }

    @Test
    void parseDoubleNull() {
        assertNull(CsvLineParser.parseDouble(""));
    }

    @Test
    void parseDoubleInvalide() {
        assertNull(CsvLineParser.parseDouble("abc"));
    }
}
