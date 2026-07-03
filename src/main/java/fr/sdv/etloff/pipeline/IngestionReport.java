package fr.sdv.etloff.pipeline;

import java.time.Duration;

public record IngestionReport(
        long recordsProcessed,
        long recordsFailed,
        Duration duration,
        double throughputPerSecond
) {
    public static IngestionReport of(long processed, long failed, Duration d) {
        double tps = d.toMillis() > 0 ? (double) processed / d.toMillis() * 1000 : 0;
        return new IngestionReport(processed, failed, d, Math.round(tps * 100) / 100.0);
    }
}
