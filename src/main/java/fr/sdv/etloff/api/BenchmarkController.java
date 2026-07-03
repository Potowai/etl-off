package fr.sdv.etloff.api;

import java.nio.file.Path;

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

    private final DataIngestionPipeline pipeline;

    public BenchmarkController(DataIngestionPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @PostMapping("/run")
    public IngestionReport run(
            @RequestParam(defaultValue = "classpath:open-food-facts.csv") String file,
            @RequestParam(defaultValue = "1000") int batchSize,
            @RequestParam(defaultValue = "4") int parallelism,
            @RequestParam(defaultValue = "BATCH") ProcessingStrategy strategy) {
        return pipeline.ingest(Path.of(file), new IngestionConfig(batchSize, parallelism, strategy));
    }
}
