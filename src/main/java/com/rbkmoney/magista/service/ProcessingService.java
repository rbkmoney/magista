package com.rbkmoney.magista.service;

import com.rbkmoney.eventstock.client.poll.DefaultPollingEventPublisherBuilder;
import com.rbkmoney.magista.dao.EventDao;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.flow.InvoiceEventFlow;
import com.rbkmoney.magista.event.flow.PayoutEventFlow;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by tolkonepiu on 24.08.16.
 */
@Service
public class ProcessingService {

    @Value("${bm.processing.handler.queue.limit}")
    private int processingHandlerQueueLimit;

    @Value("${bm.processing.handler.threadPoolSize}")
    private int processingHandlerThreadPoolSize;

    @Value("${bm.processing.handler.timeout}")
    private int processingHandlerTimeout;

    @Value("${bm.payout.handler.queue.limit}")
    private int payoutHandlerQueueLimit;

    @Value("${bm.payout.handler.threadPoolSize}")
    private int payoutHandlerThreadPoolSize;

    @Value("${bm.payout.handler.timeout}")
    private int payoutHandlerTimeout;

    private final List<Handler> handlers;

    private final EventDao eventDao;

    private final DefaultPollingEventPublisherBuilder processingEventPublisherBuilder;

    private final AtomicReference<InvoiceEventFlow> invoiceEventFlow = new AtomicReference<>();
    private final AtomicReference<PayoutEventFlow> payoutEventFlow = new AtomicReference<>();

    @Autowired
    public ProcessingService(
            List<Handler> handlers,
            EventDao eventDao,
            @Qualifier("processingEventPublisherBuilder") DefaultPollingEventPublisherBuilder processingEventPublisherBuilder
    ) {
        this.handlers = handlers;
        this.eventDao = eventDao;
        this.processingEventPublisherBuilder = processingEventPublisherBuilder;
    }

    public void start() {
        InvoiceEventFlow newInvoiceEventFlow = new InvoiceEventFlow(handlers, processingEventPublisherBuilder, processingHandlerThreadPoolSize, processingHandlerQueueLimit, processingHandlerTimeout);
        if (invoiceEventFlow.compareAndSet(null, newInvoiceEventFlow)) {
            Optional<Long> lastEventId = getLastInvoiceEventId();
            newInvoiceEventFlow.start(lastEventId);
        }
    }

    public Optional<Long> getLastInvoiceEventId() throws StorageException {
        try {
            return eventDao.getLastInvoiceEventId();
        } catch (DaoException ex) {
            throw new StorageException("Failed to get last invoice event id", ex);
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
        if (invoiceEventFlow.get() != null) {
            invoiceEventFlow.getAndSet(null).stop();
        }

        if (payoutEventFlow.get() != null) {
            payoutEventFlow.getAndSet(null).stop();
        }
    }

}
