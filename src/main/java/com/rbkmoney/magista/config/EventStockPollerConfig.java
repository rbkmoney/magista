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

    @Value("${bm.processing.pooling.maxPoolSize}")
    private int processingPoolingMaxPoolSize;

    @Value("${bm.processing.pooling.delay}")
    private int processingPoolingMaxDelay;

    @Value("${bm.payout.pooling.url}")
    private Resource payoutPoolingUrl;

    @Value("${bm.payout.pooling.maxPoolSize}")
    private int payoutPoolingMaxPoolSize;

    @Value("${bm.payout.pooling.delay}")
    private int payoutPoolingMaxDelay;

    @Bean
    public DefaultPollingEventPublisherBuilder processingEventPublisherBuilder() throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(processingPoolingUrl.getURI())
                .withMaxPoolSize(processingPoolingMaxPoolSize)
                .withPollDelay(processingPoolingMaxDelay);
    }

    @Bean
    public DefaultPollingEventPublisherBuilder payoutEventPublisherBuilder() throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(payoutPoolingUrl.getURI())
                .withMaxPoolSize(payoutPoolingMaxPoolSize)
                .withPollDelay(payoutPoolingMaxDelay);
    }

}
