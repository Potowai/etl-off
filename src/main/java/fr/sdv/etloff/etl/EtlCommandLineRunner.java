package fr.sdv.etloff.etl;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import fr.sdv.etloff.service.CsvImportService;

@Component
@ConditionalOnProperty(name = "etl.run", havingValue = "true")
@Order(Integer.MAX_VALUE)
public class EtlCommandLineRunner implements ApplicationRunner {

    private final CsvImportService csvImportService;

    public EtlCommandLineRunner(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        csvImportService.runImport();
    }
}
