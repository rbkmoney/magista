package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.eventstock.client.DefaultSubscriberConfig;
import com.rbkmoney.eventstock.client.EventConstraint;
import com.rbkmoney.eventstock.client.EventPublisher;
import com.rbkmoney.eventstock.client.poll.EventFlowFilter;
import com.rbkmoney.magista.dao.InvoiceEventDao;
import com.rbkmoney.magista.event.EventSaver;
import com.rbkmoney.magista.event.HandleTask;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tolkonepiu on 24.08.16.
 */
@Service
public class EventService {

    @Autowired
    private InvoiceEventDao invoiceEventDao;

    @Autowired
    private List<Handler> handlers;

    @Autowired
    EventPublisher eventPublisher;

    private BlockingQueue<Future<Processor>> queue;

    @Value("${bm.pooling.enabled}")
    private boolean poolingEnabled;

    @Value("${bm.pooling.handler.maxPoolSize}")
    private int pollSize;

    @Value("${bm.pooling.handler.queue.limit}")
    private int queueLimit;

    @Value("${bm.pooling.handler.timeout}")
    private long timeout;

    EventSaver eventSaver;

    ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(pollSize, new ThreadFactory() {
            AtomicInteger counter = new AtomicInteger();
            ThreadGroup group = new ThreadGroup("HandleGroup");

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(group, r, "EventHandler-" + counter.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        });

        queue = new LinkedBlockingQueue(queueLimit);
        eventSaver = new EventSaver(queue, timeout);
    }

    public Long getLastEventId() {
        try {
            return invoiceEventDao.getLastEventId();
        } catch (DaoException ex) {
            throw new StorageException("Failed to get last event id", ex);
        }
    }

    public void processEvent(StockEvent stockEvent) {
        Event event = stockEvent.getSourceEvent().getProcessingEvent();
        if (event.getPayload().isSetInvoiceChanges()) {
            for (InvoiceChange invoiceChange : event.getPayload().getInvoiceChanges()) {
                Handler handler = getHandler(stockEvent);
                if (handler != null) {
                    HandleTask handleTask = new HandleTask(invoiceChange, stockEvent, handler);

                    Future<Processor> processorFuture = executorService.submit(handleTask);
                    try {
                        queue.put(processorFuture);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    private Handler getHandler(StockEvent stockEvent) {
        for (Handler handler : handlers) {
            if (handler.accept(stockEvent)) {
                return handler;
            }
        }
        return null;
    }

    public void startPooling() {
        if (poolingEnabled) {
            EventConstraint.EventIDRange eventIDRange = new EventConstraint.EventIDRange();
            Long lastEventId = getLastEventId();
            if (lastEventId != null) {
                eventIDRange.setFromExclusive(lastEventId);
            }
            EventFlowFilter eventFlowFilter = new EventFlowFilter(new EventConstraint(eventIDRange));
            eventPublisher.subscribe(new DefaultSubscriberConfig(eventFlowFilter));

            Thread eventSaverThread = new Thread(eventSaver, "EventSaver");
            eventSaverThread.start();
        }
    }
}
