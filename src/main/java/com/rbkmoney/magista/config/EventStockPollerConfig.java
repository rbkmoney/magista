package com.rbkmoney.magista.config;

import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.PollingEventPublisherBuilder;
import com.rbkmoney.magista.event.impl.poller.InvoiceEventStockHandler;
import com.rbkmoney.magista.event.impl.poller.PayoutEventStockHandler;
import com.rbkmoney.magista.service.ProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

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
    private UrlResource payoutPoolingUrl;

    @Value("${bm.payout.pooling.maxPoolSize}")
    private int payoutPoolingMaxPoolSize;

    @Value("${bm.payout.pooling.delay}")
    private int payoutPoolingMaxDelay;

    @Autowired
    ProcessingService processingService;

    @Bean
    public EventPublisher processingEventPublisher() throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(processingPoolingUrl.getURI())
                .withEventHandler(new InvoiceEventStockHandler(processingService))
                .withMaxPoolSize(processingPoolingMaxPoolSize)
                .withPollDelay(processingPoolingMaxDelay)
                .build();
    }

    @Bean
    public EventPublisher payoutEventPublisher() throws IOException {
        return new PollingEventPublisherBuilder()
                .withURI(payoutPoolingUrl.getURI())
                .withEventHandler(new PayoutEventStockHandler(processingService))
                .withMaxPoolSize(payoutPoolingMaxPoolSize)
                .withPollDelay(payoutPoolingMaxDelay)
                .build();
    }

}
