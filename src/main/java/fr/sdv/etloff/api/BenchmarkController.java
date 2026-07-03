package fr.sdv.etloff.api;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.sdv.etloff.pipeline.DataIngestionPipeline;
import fr.sdv.etloff.pipeline.IngestionConfig;
import fr.sdv.etloff.pipeline.IngestionReport;
import fr.sdv.etloff.pipeline.ProcessingStrategy;

@RestController
@RequestMapping("/benchmark")
public class BenchmarkController {

    private final Map<String, DataIngestionPipeline> pipelines;

    public BenchmarkController(Map<String, DataIngestionPipeline> pipelines) {
        this.pipelines = pipelines;
    }

    @PostMapping("/run")
    public IngestionReport run(
            @RequestParam(defaultValue = "1000") int batchSize,
            @RequestParam(defaultValue = "4") int parallelism,
            @RequestParam(defaultValue = "BATCH") ProcessingStrategy strategy) {
        DataIngestionPipeline p = pipelines.get(strategy.name().toLowerCase() + "Pipeline");
        if (p == null) throw new IllegalArgumentException("Strategy not implemented: " + strategy);
        return p.ingest(null, new IngestionConfig(batchSize, parallelism, strategy));
    }
}
