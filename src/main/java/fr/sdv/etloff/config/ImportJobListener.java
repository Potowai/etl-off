package fr.sdv.etloff.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class ImportJobListener implements JobExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(ImportJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        LOG.info("Début import Open Food Facts");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long durationMs = Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime()).toMillis();
        long read = jobExecution.getStepExecutions().stream()
                .filter(step -> step.getStepName().startsWith("workerStep"))
                .mapToLong(step -> step.getReadCount())
                .sum();
        LOG.info("Import terminé : {} lignes en {} ms ({})",
                read, durationMs, jobExecution.getStatus());
    }
}
