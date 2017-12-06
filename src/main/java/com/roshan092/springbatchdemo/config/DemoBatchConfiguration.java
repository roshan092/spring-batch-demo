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
import org.springframework.batch.core.configuration.annotation.StepScope;
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
import org.springframework.beans.factory.annotation.Value;
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
    @StepScope
    public FlatFileItemReader<DemoBatchInput> reader(@Value("#{jobParameters[inputFileName]}") String inputFileName) {
        String inputFilePath = String.format("C:\\Users\\roslobo\\spring-batch\\input\\%s", inputFileName);
        LOGGER.info("FlatFileItemReader created with file name ------------------->" + inputFilePath);
        FlatFileItemReader<DemoBatchInput> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(inputFilePath));
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
    @StepScope
    public FlatFileItemWriter<DemoBatchOutput> writer(@Value("#{jobParameters[outputFileName]}") String outputFileName) {
        long timeStamp = new Date().getTime();
        String outputFilePath = String.format("C:\\Users\\roslobo\\spring-batch\\output\\%s-%d.csv", outputFileName, timeStamp);
        LOGGER.info("FlatFileItemWriter created ------------------->" + outputFilePath);
        FlatFileItemWriter<DemoBatchOutput> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(outputFilePath));
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
    @StepScope
    public TaskExecutor stepTaskExecutor(@Value("#{jobParameters[noOfThreads]}") Integer noOfThreads) {
        System.out.println("StepTaskExecutor created with noOfThreads = " + noOfThreads);
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(noOfThreads);
        return taskExecutor;
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory,
                     FlatFileItemReader<DemoBatchInput> flatFileItemReader,
                     DemoItemProcessor demoItemProcessor,
                     FlatFileItemWriter<DemoBatchOutput> flatFileItemWriter,
                     DemoJobReaderListener DemoJobReaderListener, DemoJobWriterListener demoJobWriterListener,
                     DemoJobProcessorListener demoJobProcessorListener, TaskExecutor stepTaskExecutor) {
        LOGGER.info("step created ------------------->");
        return stepBuilderFactory.get("step")
                .<DemoBatchInput, DemoBatchOutput>chunk(10)
                .reader(flatFileItemReader)
                .listener(DemoJobReaderListener)
                .processor(demoItemProcessor)
                .listener(demoJobProcessorListener)
                .writer(flatFileItemWriter)
                .listener(demoJobWriterListener)
                .taskExecutor(stepTaskExecutor)
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
