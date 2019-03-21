package com.rbkmoney.magista.listener;

import com.rbkmoney.magista.service.ProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OnStart implements ApplicationListener<ApplicationReadyEvent> {

    private final ProcessingService processingService;

    @Value("${bm.pooling.enabled}")
    private boolean poolingEnabled;

    @Autowired
    public OnStart(ProcessingService processingService) {
        this.processingService = processingService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (poolingEnabled) {
            processingService.start();
        }
    }

}