package com.roshan092.springbatchdemo.listener;

import com.roshan092.springbatchdemo.domain.DemoBatchInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

@Component
public class DemoJobReaderListener implements ItemReadListener<DemoBatchInput> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoJobReaderListener.class);

    @Override
    public void beforeRead() {
        LOGGER.info("Before reading item ======> ");
    }

    @Override
    public void afterRead(DemoBatchInput item) {
        LOGGER.info("After reading item =======> " + item);
    }

    @Override
    public void onReadError(Exception ex) {
    }
}
