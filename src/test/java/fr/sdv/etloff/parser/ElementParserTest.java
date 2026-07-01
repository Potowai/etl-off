package fr.sdv.etloff.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class ElementParserTest {

    @Test
    void retireCaracteresSpeciaux() {
        List<String> result = CsvLineParser.parseElements("Sucre*, farine, _Maïs_");
        assertEquals(List.of("Sucre", "farine", "Maïs"), result);
    }

    @Test
    void retirePourcentages() {
        List<String> result = CsvLineParser.parseElements("Sucre 15%, farine 50%, Maïs 35%");
        assertEquals(List.of("Sucre", "farine", "Maïs"), result);
    }

    @Test
    void retireParentheses() {
        List<String> result = CsvLineParser.parseElements("Sucre, banane, Pâte (Farine 50%, Sucre 20%, Œufs 30%)");
        assertEquals(List.of("Sucre", "banane", "Pâte"), result);
    }

    @Test
    void retireUnderscore() {
        List<String> result = CsvLineParser.parseElements("_lait_, eau");
        assertTrue(result.contains("lait"));
        assertTrue(result.contains("eau"));
    }

    @Test
    void additifsSeparesParVirgule() {
        List<String> result = CsvLineParser.parseElements("E500 - Carbonates de sodium,E500ii - Carbonate acide de sodium");
        assertEquals(2, result.size());
    }

    @Test
    void videRetourneListeVide() {
        assertTrue(CsvLineParser.parseElements("").isEmpty());
        assertTrue(CsvLineParser.parseElements(null).isEmpty());
    }
}
