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

    @Value("${payouter.pooling.url}")
    private Resource payoutPoolingUrl;

    @Value("${payouter.pooling.querySize}")
    private int payoutPoolingQuerySize;

    @Value("${payouter.pooling.maxPoolSize}")
    private int payoutPoolingMaxPoolSize;

    @Value("${payouter.pooling.delay}")
    private int payoutPoolingMaxDelay;

    @Bean
    public DefaultPollingEventPublisherBuilder payoutEventPublisherBuilder() throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(payoutPoolingUrl.getURI())
                .withPayoutServiceAdapter()
                .withMaxQuerySize(payoutPoolingQuerySize)
                .withMaxPoolSize(payoutPoolingMaxPoolSize)
                .withPollDelay(payoutPoolingMaxDelay);
    }

}
