package com.roshan092.springbatchdemo.service;

import com.roshan092.springbatchdemo.domain.CalculationResult;
import com.roshan092.springbatchdemo.domain.DemoBatchInput;
import com.roshan092.springbatchdemo.domain.DemoBatchOutput;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class DemoItemProcessor implements ItemProcessor<DemoBatchInput, DemoBatchOutput> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoItemProcessor.class);
    private final RestTemplate restTemplate;

    @Override
    public DemoBatchOutput process(DemoBatchInput demoBatchInput) throws Exception {
        String url
                = String.format("http://localhost:9090/calculate?id=%d",
                demoBatchInput.getId());
        LOGGER.info("Calling=======================> " + url);
        ResponseEntity<CalculationResult> responseEntity
                = restTemplate.getForEntity(url, CalculationResult.class);
        return DemoBatchOutput.builder()
                .id(responseEntity.getBody().getId())
                .value(responseEntity.getBody().getValue())
                .build();
    }
}
