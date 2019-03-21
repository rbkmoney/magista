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

    @Value("${bm.payout.pooling.url}")
    private Resource payoutPoolingUrl;

    @Value("${bm.payout.pooling.querySize}")
    private int payoutPoolingQuerySize;

    @Value("${bm.payout.pooling.maxPoolSize}")
    private int payoutPoolingMaxPoolSize;

    @Value("${bm.payout.pooling.delay}")
    private int payoutPoolingMaxDelay;

    @Bean
    public DefaultPollingEventPublisherBuilder payoutEventPublisherBuilder() throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(payoutPoolingUrl.getURI())
                .withMaxQuerySize(payoutPoolingQuerySize)
                .withMaxPoolSize(payoutPoolingMaxPoolSize)
                .withPollDelay(payoutPoolingMaxDelay);
    }

}
