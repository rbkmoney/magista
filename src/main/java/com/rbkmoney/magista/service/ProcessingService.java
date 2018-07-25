package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.flow.InvoiceEventFlow;
import com.rbkmoney.magista.event.flow.PayoutEventFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by tolkonepiu on 24.08.16.
 */
@Service
public class ProcessingService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

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

    private final PlatformTransactionManager transactionManager;

    private final AtomicReference<InvoiceEventFlow> invoiceEventFlow = new AtomicReference<>();
    private final AtomicReference<PayoutEventFlow> payoutEventFlow = new AtomicReference<>();

    @Autowired
    public ProcessingService(List<Handler> handlers, PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.handlers = handlers;
    }

    public void start() {
        InvoiceEventFlow newInvoiceEventFlow = new InvoiceEventFlow(handlers, new TransactionTemplate(transactionManager), processingHandlerThreadPoolSize, processingHandlerQueueLimit, processingHandlerTimeout);
        PayoutEventFlow newPayoutEventFlow = new PayoutEventFlow(handlers, new TransactionTemplate(transactionManager), payoutHandlerThreadPoolSize, payoutHandlerQueueLimit, payoutHandlerTimeout);

        if (invoiceEventFlow.compareAndSet(null, newInvoiceEventFlow)) {
            newInvoiceEventFlow.start();
        }
        if (payoutEventFlow.compareAndSet(null, newPayoutEventFlow)) {
            newPayoutEventFlow.start();
        }
    }

    public void processInvoiceEvent(StockEvent stockEvent) {
        if (invoiceEventFlow.get() != null) {
            invoiceEventFlow.get().processEvent(stockEvent);
        } else {
            log.warn("invoice event flow is not running");
        }
    }

    public void processPayoutEvent(StockEvent stockEvent) {
        if (payoutEventFlow.get() != null) {
            payoutEventFlow.get().processEvent(stockEvent);
        } else {
            log.warn("payout event flow is not running");
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
