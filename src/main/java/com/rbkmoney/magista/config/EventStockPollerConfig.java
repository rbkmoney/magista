package com.rbkmoney.magista.config;

import com.rbkmoney.eventstock.client.*;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.eventstock.client.poll.PollingEventPublisherBuilder;
import com.rbkmoney.magista.handler.Handler;
import com.rbkmoney.magista.handler.poller.EventStockErrorHandler;
import com.rbkmoney.magista.handler.poller.EventStockHandler;
import com.rbkmoney.magista.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

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
    List<Handler> handlers;

    @Bean
    public EventPublisher eventPublisher() throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(bmUri.getURI())
                .withEventHandler(new EventStockHandler(handlers))
                .withErrorHandler(new EventStockErrorHandler())
                .withMaxPoolSize(maxPoolSize)
                .withPollDelay(pollDelay)
                .build();
    }

}
