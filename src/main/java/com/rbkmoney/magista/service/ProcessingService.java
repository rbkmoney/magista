package com.rbkmoney.magista.service;

import com.rbkmoney.eventstock.client.poll.DefaultPollingEventPublisherBuilder;
import com.rbkmoney.magista.dao.EventDao;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.flow.PayoutEventFlow;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.StorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by tolkonepiu on 24.08.16.
 */
@Service
@RequiredArgsConstructor
public class ProcessingService {

    @Value("${bm.payout.handler.queue.limit}")
    private int payoutHandlerQueueLimit;

    @Value("${bm.payout.handler.threadPoolSize}")
    private int payoutHandlerThreadPoolSize;

    @Value("${bm.payout.handler.timeout}")
    private int payoutHandlerTimeout;

    private final List<Handler> handlers;

    private final EventDao eventDao;

    private final DefaultPollingEventPublisherBuilder payoutEventPublisherBuilder;

    private final AtomicReference<PayoutEventFlow> payoutEventFlow = new AtomicReference<>();

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        PayoutEventFlow newPayoutEventFlow = new PayoutEventFlow(handlers, payoutEventPublisherBuilder, payoutHandlerThreadPoolSize, payoutHandlerQueueLimit, payoutHandlerTimeout);
        if (payoutEventFlow.compareAndSet(null, newPayoutEventFlow)) {
            Optional<Long> lastEventId = getLastPayoutEventId();
            newPayoutEventFlow.start(lastEventId);
        }
    }

    public Optional<Long> getLastPayoutEventId() throws StorageException {
        try {
            return eventDao.getLastPayoutEventId();
        } catch (DaoException ex) {
            throw new StorageException("Failed to get last payout event id", ex);
        }
    }

    @PreDestroy
    public void stop() {
        if (payoutEventFlow.get() != null) {
            payoutEventFlow.getAndSet(null).stop();
        }
        kafkaListenerEndpointRegistry.stop();
    }

}
