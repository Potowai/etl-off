package fr.sdv.etloff.pipeline;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VirtualThreadPipelineTest {

    @Test
    void configDefaults() {
        IngestionConfig cfg = IngestionConfig.defaults();
        assertEquals(1000, cfg.batchSize());
        assertEquals(4, cfg.parallelism());
        assertEquals(ProcessingStrategy.BATCH, cfg.strategy());
    }
}
