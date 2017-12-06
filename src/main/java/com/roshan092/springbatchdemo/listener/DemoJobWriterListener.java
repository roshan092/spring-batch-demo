package com.roshan092.springbatchdemo.listener;

import com.roshan092.springbatchdemo.domain.DemoBatchOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemoJobWriterListener implements ItemWriteListener<DemoBatchOutput> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoJobWriterListener.class);

    @Override
    public void beforeWrite(List<? extends DemoBatchOutput> items) {
//        items.stream().forEach(item -> LOGGER.info("Before writing items = " + item));
    }

    @Override
    public void afterWrite(List<? extends DemoBatchOutput> items) {
        items.stream().forEach(item -> LOGGER.info("After writing items = " + item));
    }

    @Override
    public void onWriteError(Exception exception, List<? extends DemoBatchOutput> items) {
    }
}
