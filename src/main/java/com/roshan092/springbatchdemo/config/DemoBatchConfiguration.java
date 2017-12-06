package com.roshan092.springbatchdemo.config;

import com.roshan092.springbatchdemo.domain.DemoBatchInput;
import com.roshan092.springbatchdemo.domain.DemoBatchOutput;
import com.roshan092.springbatchdemo.listener.DemoJobExecutionListener;
import com.roshan092.springbatchdemo.listener.DemoJobProcessorListener;
import com.roshan092.springbatchdemo.listener.DemoJobReaderListener;
import com.roshan092.springbatchdemo.listener.DemoJobWriterListener;
import com.roshan092.springbatchdemo.service.DemoItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.util.Date;

@Configuration
@EnableBatchProcessing
public class DemoBatchConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoBatchConfiguration.class);

    @Bean
    public FlatFileItemReader<DemoBatchInput> reader() {
        LOGGER.info("FlatFileItemReader created ------------------->");
        FlatFileItemReader<DemoBatchInput> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("C:\\Users\\roslobo\\spring-batch\\input\\sample-data-100.csv"));
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<DemoBatchInput>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"id"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<DemoBatchInput>() {{
                setTargetType(DemoBatchInput.class);
            }});
        }});
        return reader;
    }

    @Bean
    public FlatFileItemWriter<DemoBatchOutput> writer() {
        LOGGER.info("FlatFileItemWriter created ------------------->");
        long timeStamp = new Date().getTime();
        FlatFileItemWriter<DemoBatchOutput> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("C:\\Users\\roslobo\\spring-batch\\output\\sample-data-100" + timeStamp + ".csv"));
        writer.setHeaderCallback(
                headerWriter -> headerWriter.write("id, value"));

        DelimitedLineAggregator<DemoBatchOutput> delLineAgg
                = new DelimitedLineAggregator<>();
        delLineAgg.setDelimiter(",");
        BeanWrapperFieldExtractor<DemoBatchOutput> fieldExtractor
                = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "value"});
        delLineAgg.setFieldExtractor(fieldExtractor);
        writer.setLineAggregator(delLineAgg);
        return writer;
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, DemoItemProcessor demoItemProcessor,
                     DemoJobReaderListener DemoJobReaderListener, DemoJobWriterListener demoJobWriterListener,
                     DemoJobProcessorListener demoJobProcessorListener) {
        LOGGER.info("step created ------------------->");
        return stepBuilderFactory.get("step")
                .<DemoBatchInput, DemoBatchOutput>chunk(1)
                .reader(reader())
                .listener(DemoJobReaderListener)
                .processor(demoItemProcessor)
                .listener(demoJobProcessorListener)
                .writer(writer())
                .listener(demoJobWriterListener)
                .build();
    }

    @Bean(name = "demoJob")
    public Job job(JobBuilderFactory jobBuilderFactory, Step step, DemoJobExecutionListener demoJobExecutionListener) {
        LOGGER.info("demoJob created ------------------->");
        return jobBuilderFactory.get("demoJob")
                .incrementer(new RunIdIncrementer())
                .flow(step)
                .end()
                .listener(demoJobExecutionListener)
                .build();
    }

    @Bean
    public TaskExecutor jobTaskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(1);
        return taskExecutor;
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository, TaskExecutor jobTaskExecutor) {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(jobTaskExecutor);
        return jobLauncher;
    }

}
