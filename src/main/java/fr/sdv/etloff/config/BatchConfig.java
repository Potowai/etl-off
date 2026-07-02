package fr.sdv.etloff.config;

import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import fr.sdv.etloff.domain.Produit;
import fr.sdv.etloff.etl.CsvFileAccess;
import fr.sdv.etloff.etl.LineRangePartitioner;
import fr.sdv.etloff.etl.OpenFoodFactsProcessor;
import fr.sdv.etloff.etl.PartitionedCsvLineReader;
import fr.sdv.etloff.etl.ReferencePreloadTasklet;
import jakarta.persistence.EntityManagerFactory;

/**
 * Job Spring Batch en 2 steps :
 * 1. preloadReferencesStep -> scanne le CSV, précharge les références en cache+base
 * 2. masterStep -> partitionne le CSV (grid-size) et lance les workers en parallèle
 *    via virtual threads. Chaque worker lit sa portion, transforme et persist en batch.
 */
@Configuration
@EnableBatchProcessing
@EnableConfigurationProperties(EtlProperties.class)
public class BatchConfig {

    @Bean
    JpaItemWriter<Produit> produitWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Produit>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true)
                .build();
    }

    @Bean
    @StepScope
    PartitionedCsvLineReader partitionedCsvLineReader(
            CsvFileAccess csvFileAccess,
            @Value("#{stepExecutionContext['startLine']}") long startLine,
            @Value("#{stepExecutionContext['endLine']}") long endLine,
            @Value("#{stepExecutionContext['startByte']}") long startByte) {
        return new PartitionedCsvLineReader(
                csvFileAccess.getLocalPath(), startLine, endLine, startByte);
    }

    @Bean
    Step preloadReferencesStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ReferencePreloadTasklet tasklet) {
        return new StepBuilder("preloadReferencesStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    Step workerStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            PartitionedCsvLineReader reader,
            OpenFoodFactsProcessor processor,
            JpaItemWriter<Produit> writer,
            EtlProperties properties) {
        return new StepBuilder("workerStep", jobRepository)
                .<String, Produit>chunk(properties.getChunkSize(), transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    LineRangePartitioner lineRangePartitioner(CsvFileAccess csvFileAccess, EtlProperties properties) {
        return new LineRangePartitioner(csvFileAccess, properties.getGridSize());
    }

    @Bean
    Step masterStep(
            JobRepository jobRepository,
            Step workerStep,
            LineRangePartitioner partitioner,
            EtlProperties properties,
            @Qualifier("batchPartitionTaskExecutor") AsyncTaskExecutor taskExecutor) {
        return new StepBuilder("masterStep", jobRepository)
                .partitioner(workerStep.getName(), partitioner)
                .step(workerStep)
                .gridSize(properties.getGridSize())
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    Job importOpenFoodFactsJob(
            JobRepository jobRepository,
            Step preloadReferencesStep,
            Step masterStep,
            ImportJobListener importJobListener) {
        return new JobBuilder("importOpenFoodFactsJob", jobRepository)
                .listener(importJobListener)
                .start(preloadReferencesStep)
                .next(masterStep)
                .build();
    }

    @Bean(name = "batchPartitionTaskExecutor")
    AsyncTaskExecutor batchPartitionTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean
    BatchDataSourceScriptDatabaseInitializer batchDataSourceInitializer(DataSource dataSource) {
        BatchProperties.Jdbc jdbc = new BatchProperties.Jdbc();
        jdbc.setInitializeSchema(DatabaseInitializationMode.ALWAYS);
        return new BatchDataSourceScriptDatabaseInitializer(dataSource, jdbc);
    }
}
