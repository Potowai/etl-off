package fr.sdv.etloff.pipeline;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class IngestionReportTest {

    @Test
    void calculDebit() {
        IngestionReport r = IngestionReport.of(1000, 5, Duration.ofSeconds(10));
        assertEquals(1000, r.recordsProcessed());
        assertEquals(5, r.recordsFailed());
        assertEquals(100.0, r.throughputPerSecond(), 0.01);
    }

    @Test
    void zeroLigne() {
        IngestionReport r = IngestionReport.of(0, 0, Duration.ZERO);
        assertEquals(0, r.throughputPerSecond(), 0.01);
    }
}
