package fr.sdv.etloff.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

@Service
public class CsvImportService {

    private final JobLauncher jobLauncher;
    private final Job importOpenFoodFactsJob;

    public CsvImportService(JobLauncher jobLauncher, Job importOpenFoodFactsJob) {
        this.jobLauncher = jobLauncher;
        this.importOpenFoodFactsJob = importOpenFoodFactsJob;
    }

    public void runImport() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(importOpenFoodFactsJob, params);
    }
}
