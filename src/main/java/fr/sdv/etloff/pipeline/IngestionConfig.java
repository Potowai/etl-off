package fr.sdv.etloff.pipeline;

public record IngestionConfig(
        int batchSize,
        int parallelism,
        ProcessingStrategy strategy
) {
    public static IngestionConfig defaults() {
        return new IngestionConfig(1000, 4, ProcessingStrategy.BATCH);
    }
}
