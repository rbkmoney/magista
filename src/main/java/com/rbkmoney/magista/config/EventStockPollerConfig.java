package com.rbkmoney.magista.config;

import com.rbkmoney.eventstock.client.poll.DefaultPollingEventPublisherBuilder;
import com.rbkmoney.eventstock.client.poll.PollingEventPublisherBuilder;
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

    @Value("${bm.processing.pooling.url}")
    private Resource processingPoolingUrl;

    @Value("${bm.processing.pooling.querySize}")
    private int processingPoolingQuerySize;

    @Value("${bm.processing.pooling.maxPoolSize}")
    private int processingPoolingMaxPoolSize;

    @Value("${bm.processing.pooling.delay}")
    private int processingPoolingMaxDelay;

    @Bean
    public DefaultPollingEventPublisherBuilder processingEventPublisherBuilder() throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(processingPoolingUrl.getURI())
                .withMaxQuerySize(processingPoolingQuerySize)
                .withMaxPoolSize(processingPoolingMaxPoolSize)
                .withPollDelay(processingPoolingMaxDelay);
    }

}
