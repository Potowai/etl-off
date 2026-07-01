package fr.sdv.etloff.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import fr.sdv.etloff.etl.CsvProductRecord;

class CsvLineParserTest {

    @Test
    void parseLigneValide() {
        String line = "Boissons|Evian|Eau|a|Eau|0|0|0||0|0|||||||||||||||||0|||";
        CsvProductRecord record = CsvLineParser.parseLine(line);
        assertNotNull(record);
        assertEquals("Boissons", record.getCategorie());
        assertEquals("Evian", record.getMarque());
        assertEquals("Eau", record.getNom());
        assertEquals("A", record.getNutritionGradeFr());
        assertEquals(0.0, record.getEnergie100g());
    }

    @Test
    void ligneIncompleteRetourneNull() {
        assertNull(CsvLineParser.parseLine("a|b|c"));
    }
}
