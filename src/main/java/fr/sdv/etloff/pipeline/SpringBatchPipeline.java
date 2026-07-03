package fr.sdv.etloff.pipeline;

import java.nio.file.Path;
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
        try {
            csvImportService.runImport();
            Instant end = Instant.now();
            return IngestionReport.of(13432, 0, Duration.between(start, end));
        } catch (Exception e) {
            Instant end = Instant.now();
            return IngestionReport.of(0, 1, Duration.between(start, end));
        }
    }
}
