package fr.sdv.etloff.service.impl;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import fr.sdv.etloff.service.ICsvImportService;

@Service
public class CsvImportServiceImpl implements ICsvImportService {

    private final JobLauncher jobLauncher;
    private final Job importOpenFoodFactsJob;

    public CsvImportServiceImpl(JobLauncher jobLauncher, Job importOpenFoodFactsJob) {
        this.jobLauncher = jobLauncher;
        this.importOpenFoodFactsJob = importOpenFoodFactsJob;
    }

    @Override
    public void runImport() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(importOpenFoodFactsJob, params);
    }
}
