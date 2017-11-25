package com.roshan092.springbatchdemo.controller;

import com.roshan092.springbatchdemo.domain.BatchResponse;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.util.Set;

@RestController
@AllArgsConstructor
public class BatchController {
    private final JobLauncher jobLauncher;
    private final Job demoJob;
    private final Clock clock;
    private final JobExplorer jobExplorer;

    @PostMapping("/batch/run")
    public BatchResponse startBatch() throws JobExecutionException {
        JobParametersBuilder builder = new JobParametersBuilder()
                .addLong("time", clock.instant().getEpochSecond());
        Set<JobExecution> runningJobExecutions = jobExplorer.findRunningJobExecutions("demoJob");
        if (runningJobExecutions.isEmpty()) {
            return BatchResponse.builder()
                    .jobExecutionId(jobLauncher.run(this.demoJob, builder.toJobParameters()).getId())
                    .build();
        } else {
            return BatchResponse.builder()
                    .jobExecutionId(runningJobExecutions.stream().findFirst().get().getJobId())
                    .errorMessage("batch already running for demoJob")
                    .build();
        }
    }
}
