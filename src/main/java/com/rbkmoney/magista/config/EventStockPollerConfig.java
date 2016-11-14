package com.rbkmoney.magista.config;

import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.PollingEventPublisherBuilder;
import com.rbkmoney.magista.event.impl.poller.EventStockErrorHandler;
import com.rbkmoney.magista.event.impl.poller.EventStockHandler;
import com.rbkmoney.magista.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Configuration
public class EventStockPollerConfig {

    @Value("${bm.pooling.url}")
    Resource bmUri;

    @Value("${bm.pooling.delay:5000}")
    int pollDelay;

    @Value("${bm.pooling.maxPoolSize:1}")
    int maxPoolSize;

    @Autowired
    EventService eventService;

    @Bean
    public EventPublisher eventPublisher() throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(bmUri.getURI())
                .withEventHandler(new EventStockHandler(eventService))
                .withErrorHandler(new EventStockErrorHandler())
                .withMaxPoolSize(maxPoolSize)
                .withPollDelay(pollDelay)
                .build();
    }

}
