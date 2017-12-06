package com.roshan092.springbatchdemo.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BatchRequest {
    private String fileName;
    private Long noOfThreads;
}
