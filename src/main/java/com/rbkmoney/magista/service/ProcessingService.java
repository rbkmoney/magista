package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.flow.InvoiceEventFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by tolkonepiu on 24.08.16.
 */
@Service
public class ProcessingService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${bm.pooling.handler.threadPoolSize}")
    int threadPoolSize;

    @Value("${bm.pooling.handler.queue.limit}")
    int queueLimit;

    @Value("${bm.pooling.handler.timeout}")
    long timeout;

    private final List<Handler> handlers;

    private final AtomicReference<InvoiceEventFlow> invoiceEventFlow = new AtomicReference<>();

    @Autowired
    public ProcessingService(List<Handler> handlers) {
        this.handlers = handlers;
    }

    public void start() {
        InvoiceEventFlow newInvoiceEventFlow = new InvoiceEventFlow(handlers, threadPoolSize, queueLimit, timeout);
        if (invoiceEventFlow.compareAndSet(null, newInvoiceEventFlow)) {
            newInvoiceEventFlow.start();
        }
    }

    public void processInvoiceEvent(StockEvent stockEvent) {
        if (invoiceEventFlow.get() != null) {
            invoiceEventFlow.get().processEvent(stockEvent);
        } else {
            log.warn("invoice event flow is not running");
        }
    }

    @PreDestroy
    public void stop() {
        if (invoiceEventFlow.get() != null) {
            invoiceEventFlow.getAndSet(null).stop();
        }
    }

}
