package com.roshan092.springbatchdemo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class DemoJobExecutionListener implements JobExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoJobExecutionListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Before demoJob Execution ");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long milliSecondsTaken = jobExecution.getEndTime().getTime() - jobExecution.getCreateTime().getTime();
        LOGGER.info("demoJob Execution completed in seconds: " + (milliSecondsTaken / 1000));
    }
}
