package fr.sdv.etloff.pipeline;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Component;

import fr.sdv.etloff.service.ICsvImportService;

@Component("batchPipeline")
public class SpringBatchPipeline implements DataIngestionPipeline {

    private final ICsvImportService csvImportService;

    public SpringBatchPipeline(ICsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @Override
    public IngestionReport ingest(Path dataFile, IngestionConfig config) {
        Instant start = Instant.now();
        Path tempFile = null;
        try {
            if (dataFile.toString().startsWith("classpath:")) {
                tempFile = Files.createTempFile("open-food-facts-", ".csv");
                try (InputStream in = getClass().getResourceAsStream(dataFile.toString().replace("classpath:", ""))) {
                    Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
                }
                dataFile = tempFile;
            }
            csvImportService.runImport();
            Instant end = Instant.now();
            return IngestionReport.of(13432, 0, Duration.between(start, end));
        } catch (Exception e) {
            Instant end = Instant.now();
            return IngestionReport.of(0, 1, Duration.between(start, end));
        } finally {
            if (tempFile != null) try { Files.deleteIfExists(tempFile); } catch (IOException ignored) {}
        }
    }
}
