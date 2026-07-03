package fr.sdv.etloff.pipeline;

import java.nio.file.Path;

public interface DataIngestionPipeline {
    IngestionReport ingest(Path dataFile, IngestionConfig config);
}
