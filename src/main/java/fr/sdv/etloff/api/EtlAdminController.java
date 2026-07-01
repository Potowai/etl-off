package fr.sdv.etloff.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.sdv.etloff.api.dto.EtlRunResponse;
import fr.sdv.etloff.service.CsvImportService;

@RestController
@RequestMapping("/admin/etl")
public class EtlAdminController {

    private final CsvImportService csvImportService;

    public EtlAdminController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @PostMapping("/run")
    public EtlRunResponse run() throws Exception {
        csvImportService.runImport();
        return new EtlRunResponse("COMPLETED", "OK");
    }
}
