package com.roshan092.springbatchdemo.listener;

import com.roshan092.springbatchdemo.domain.DemoBatchInput;
import com.roshan092.springbatchdemo.domain.DemoBatchOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

@Component
public class DemoJobProcessorListener implements ItemProcessListener<DemoBatchInput, DemoBatchOutput> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoJobProcessorListener.class);

    @Override
    public void beforeProcess(DemoBatchInput item) {
        LOGGER.info("Before item process = " + item);
    }

    @Override
    public void afterProcess(DemoBatchInput item, DemoBatchOutput result) {
        LOGGER.info("After item process = " + item + ", result = " + result);
    }

    @Override
    public void onProcessError(DemoBatchInput item, Exception e) {
    }
}
