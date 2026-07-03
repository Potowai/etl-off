package fr.sdv.etloff.pipeline.impl;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import fr.sdv.etloff.domain.Additif;
import fr.sdv.etloff.domain.Allergene;
import fr.sdv.etloff.domain.Categorie;
import fr.sdv.etloff.domain.Ingredient;
import fr.sdv.etloff.domain.Marque;
import fr.sdv.etloff.etl.CsvProductRecord;
import fr.sdv.etloff.parser.CsvLineParser;
import fr.sdv.etloff.pipeline.DataIngestionPipeline;
import fr.sdv.etloff.pipeline.IngestionConfig;
import fr.sdv.etloff.pipeline.IngestionReport;
import fr.sdv.etloff.service.IReferenceDataService;

@Component("vtPipeline")
public class VirtualThreadPipeline implements DataIngestionPipeline {

    private final IReferenceDataService referenceDataService;
    private final JdbcTemplate jdbc;

    public VirtualThreadPipeline(IReferenceDataService referenceDataService, DataSource ds) {
        this.referenceDataService = referenceDataService;
        this.jdbc = new JdbcTemplate(ds);
    }

    @Override
    public IngestionReport ingest(Path dataFile, IngestionConfig config) {
        Instant start = Instant.now();
        try {
            Path localPath = dataFile != null ? dataFile : extractClasspathCsv();
            preloadReferences(localPath);
            List<String> lines = readAllLines(localPath);
            int chunkSize = config.batchSize();
            int parallelism = config.parallelism();
            AtomicInteger failed = new AtomicInteger(0);

            try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < lines.size(); i += chunkSize) {
                    int end = Math.min(i + chunkSize, lines.size());
                    List<String> chunk = lines.subList(i, end);
                    exec.submit(() -> {
                        try {
                            insertChunk(chunk);
                        } catch (Exception e) {
                            failed.addAndGet(chunk.size());
                        }
                    });
                }
            }
            long processed = lines.size() - failed.get();
            return IngestionReport.of(processed, failed.get(), Duration.between(start, Instant.now()));
        } catch (Exception e) {
            return IngestionReport.of(0, 1, Duration.between(start, Instant.now()));
        }
    }

    private void preloadReferences(Path localPath) throws Exception {
        Set<String> categories = new HashSet<>();
        Set<String> marques = new HashSet<>();
        Set<String> ingredients = new HashSet<>();
        Set<String> allergenes = new HashSet<>();
        Set<String> additifs = new HashSet<>();

        try (BufferedReader reader = Files.newBufferedReader(localPath, StandardCharsets.UTF_8)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                var record = CsvLineParser.parseLine(line);
                if (record == null) continue;
                addIfPresent(categories, record.categorie());
                addIfPresent(marques, record.marque());
                addAll(ingredients, record.ingredients());
                addAll(allergenes, record.allergenes());
                addAll(additifs, record.additifs());
            }
        }
        referenceDataService.bulkLoad(categories, marques, ingredients, allergenes, additifs);
    }

    private List<String> readAllLines(Path localPath) throws Exception {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(localPath, StandardCharsets.UTF_8)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) lines.add(line);
        }
        return lines;
    }

    private void insertChunk(List<String> lines) {
        for (String line : lines) {
            CsvProductRecord record = CsvLineParser.parseLine(line);
            if (record == null) continue;
            Long catId = findCategorieId(record.categorie());
            Long marqueId = findMarqueId(record.marque());
            Long produitId = insertProduit(record, catId, marqueId);
            if (produitId == null) continue;
            linkAllergenes(produitId, record.allergenes());
            linkAdditifs(produitId, record.additifs());
            linkIngredients(produitId, record.ingredients());
        }
    }

    private Long insertProduit(CsvProductRecord record, Long catId, Long marqueId) {
        try {
            jdbc.update("INSERT INTO produit (nom, nutrition_grade_fr, energie100g, graisse100g, sucres100g, fibres100g, proteines100g, sel100g, presence_huile_palme, categorie_id, marque_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                    record.nom(), record.nutritionGradeFr(), record.energie100g(),
                    record.graisse100g(), record.sucres100g(), record.fibres100g(),
                    record.proteines100g(), record.sel100g(), record.presenceHuilePalme(),
                    catId, marqueId);
            return jdbc.queryForObject("CALL IDENTITY()", Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    private Long findCategorieId(String nom) {
        if (nom == null || nom.isBlank()) return null;
        Categorie c = referenceDataService.findCategorie(nom);
        return c != null ? c.getId() : null;
    }

    private Long findMarqueId(String nom) {
        if (nom == null || nom.isBlank()) return null;
        Marque m = referenceDataService.findMarque(nom);
        return m != null ? m.getId() : null;
    }

    private void linkAllergenes(Long produitId, List<String> names) {
        if (names == null) return;
        for (String name : names) {
            Allergene a = referenceDataService.findAllergene(name);
            if (a != null) jdbc.update("INSERT INTO produit_allergene (produit_id, allergene_id) VALUES (?,?)", produitId, a.getId());
        }
    }

    private void linkAdditifs(Long produitId, List<String> names) {
        if (names == null) return;
        for (String name : names) {
            Additif a = referenceDataService.findAdditif(name);
            if (a != null) jdbc.update("INSERT INTO produit_additif (produit_id, additif_id) VALUES (?,?)", produitId, a.getId());
        }
    }

    private void linkIngredients(Long produitId, List<String> names) {
        if (names == null) return;
        for (String name : names) {
            Ingredient i = referenceDataService.findIngredient(name);
            if (i != null) jdbc.update("INSERT INTO produit_ingredient (produit_id, ingredient_id) VALUES (?,?)", produitId, i.getId());
        }
    }

    private Path extractClasspathCsv() throws Exception {
        Path temp = Files.createTempFile("off-", ".csv");
        try (java.io.InputStream in = getClass().getResourceAsStream("/open-food-facts.csv")) {
            Files.copy(in, temp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        return temp;
    }

    private static void addIfPresent(Set<String> target, String value) {
        if (value != null && !value.isBlank()) target.add(value.trim());
    }

    private static void addAll(Set<String> target, List<String> values) {
        if (values == null) return;
        for (String value : values) addIfPresent(target, value);
    }
}
