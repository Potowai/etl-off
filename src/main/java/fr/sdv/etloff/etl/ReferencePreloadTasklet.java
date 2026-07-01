package fr.sdv.etloff.etl;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import fr.sdv.etloff.dao.AdditifDao;
import fr.sdv.etloff.dao.AllergeneDao;
import fr.sdv.etloff.dao.CategorieDao;
import fr.sdv.etloff.dao.IngredientDao;
import fr.sdv.etloff.dao.MarqueDao;
import fr.sdv.etloff.dao.ProduitDao;
import fr.sdv.etloff.parser.CsvLineParser;
import fr.sdv.etloff.service.IReferenceDataService;

@Component
public class ReferencePreloadTasklet implements Tasklet {

    private final CsvFileAccess csvFileAccess;
    private final IReferenceDataService referenceDataService;
    private final ProduitDao produitDao;
    private final AdditifDao additifDao;
    private final AllergeneDao allergeneDao;
    private final IngredientDao ingredientDao;
    private final MarqueDao marqueDao;
    private final CategorieDao categorieDao;

    public ReferencePreloadTasklet(
            CsvFileAccess csvFileAccess,
            IReferenceDataService referenceDataService,
            ProduitDao produitDao, AdditifDao additifDao,
            AllergeneDao allergeneDao, IngredientDao ingredientDao,
            MarqueDao marqueDao, CategorieDao categorieDao) {
        this.csvFileAccess = csvFileAccess;
        this.referenceDataService = referenceDataService;
        this.produitDao = produitDao;
        this.additifDao = additifDao;
        this.allergeneDao = allergeneDao;
        this.ingredientDao = ingredientDao;
        this.marqueDao = marqueDao;
        this.categorieDao = categorieDao;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        if (produitDao.count() > 0) {
            produitDao.deleteAllInBatch();
            additifDao.deleteAllInBatch();
            allergeneDao.deleteAllInBatch();
            ingredientDao.deleteAllInBatch();
            marqueDao.deleteAllInBatch();
            categorieDao.deleteAllInBatch();
        }
        Set<String> categories = new HashSet<>();
        Set<String> marques = new HashSet<>();
        Set<String> ingredients = new HashSet<>();
        Set<String> allergenes = new HashSet<>();
        Set<String> additifs = new HashSet<>();

        try (BufferedReader reader = Files.newBufferedReader(csvFileAccess.getLocalPath(), StandardCharsets.UTF_8)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                CsvProductRecord record = CsvLineParser.parseLine(line);
                if (record == null) continue;
                addIfPresent(categories, record.categorie());
                addIfPresent(marques, record.marque());
                addAll(ingredients, record.ingredients());
                addAll(allergenes, record.allergenes());
                addAll(additifs, record.additifs());
            }
        }
        referenceDataService.bulkLoad(categories, marques, ingredients, allergenes, additifs);
        return RepeatStatus.FINISHED;
    }

    private static void addIfPresent(Set<String> target, String value) {
        if (value != null && !value.isBlank()) target.add(value.trim());
    }

    private static void addAll(Set<String> target, List<String> values) {
        if (values == null) return;
        for (String value : values) addIfPresent(target, value);
    }
}
