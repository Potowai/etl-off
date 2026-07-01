package fr.sdv.etloff.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StringParseHelperTest {

    @Test
    void splitPipeLine() {
        String[] fields = CsvLineParser.splitPipe("a|b|c", 3);
        assertEquals(3, fields.length);
        assertEquals("a", fields[0]);
        assertEquals("b", fields[1]);
        assertEquals("c", fields[2]);
    }

    @Test
    void cleanWithStringBuilder_exempleSujet() {
        String cleaned = CsvLineParser.cleanText("Sucre*, farine, _Maïs_");
        assertTrue(cleaned.contains("Sucre"));
        assertTrue(cleaned.contains("farine"));
        assertTrue(cleaned.contains("Maïs"));
    }
}
