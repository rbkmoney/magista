package com.rbkmoney.magista.config;

import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.PollingEventPublisherBuilder;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.impl.poller.EventStockHandler;
import com.rbkmoney.magista.service.ProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Configuration
public class EventStockPollerConfig {

    @Value("${bm.pooling.url}")
    Resource bmUri;

    @Value("${bm.pooling.delay}")
    int pollDelay;

    @Value("${bm.pooling.maxPoolSize}")
    int maxPoolSize;

    @Autowired
    ProcessingService processingService;

    @Bean
    public EventPublisher eventPublisher() throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(bmUri.getURI())
                .withEventHandler(new EventStockHandler(processingService))
                .withMaxPoolSize(maxPoolSize)
                .withPollDelay(pollDelay)
                .build();
    }

}
