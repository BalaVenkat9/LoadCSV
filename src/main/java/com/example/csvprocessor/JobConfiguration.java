package com.example.csvprocessor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import org.springframework.retry.backoff.FixedBackOffPolicy;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job processCsvJob(Step processCsvStep) {
        return jobBuilderFactory.get("processCsvJob")
                .incrementer(new RunIdIncrementer())
                .flow(processCsvStep)
                .end()
                .build();
    }

    @Bean
    public Step processCsvStep(CsvItemReader reader, CsvItemProcessor processor, CsvItemWriter writer,
                               @Value("${retry.limit}") int retryLimit,
                               @Value("${retry.delay}") long retryDelay) {
        return stepBuilderFactory.get("processCsvStep")
                .<String, String>chunk(100) // Read 100 lines at a time
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retryLimit(retryLimit)
                .retry(Exception.class)
                .backOffPolicy(new FixedBackOffPolicy() {{
                    setBackOffPeriod(retryDelay);
                }})
                .build();
    }

    @Bean
    public CsvItemReader reader(@Value("${source.directory.path}") String sourceDirectoryPath) throws IOException {
        CsvItemReader reader = new CsvItemReader();
        reader.setResources(new PathMatchingResourcePatternResolver().getResources("file:" + sourceDirectoryPath + "/*.csv"));
        return reader;
    }

    @Bean
    public CsvItemProcessor processor() {
        return new CsvItemProcessor();
    }

    @Bean
    public CsvItemWriter writer(DataSource dataSource,
                                @Value("${table.name}") String tableName,
                                @Value("${table.fields}") String[] tableFields) {
        return new CsvItemWriter(dataSource, tableName, tableFields);
    }
}
