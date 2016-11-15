package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.dao.EventDao;
import com.rbkmoney.magista.event.EventContext;
import com.rbkmoney.magista.event.EventSaver;
import com.rbkmoney.magista.event.HandleTask;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tolkonepiu on 24.08.16.
 */
@Service
public class EventService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventDao eventDao;

    @Autowired
    private List<Handler> handlers;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PaymentService paymentService;

    private BlockingQueue<Future<EventContext>> queue;

    @Value("${bm.queue.limit:100}")
    private int queueLimit;

    EventSaver eventSaver;

    ExecutorService executorService = Executors.newFixedThreadPool(5, new ThreadFactory() {
        AtomicInteger counter = new AtomicInteger();
        ThreadGroup group = new ThreadGroup("HandleGroup");

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(group, r, "Handler-" + counter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    });

    public Long getLastEventId() {
        Long lastEventId;
        try {
            log.trace("Get last event id");
            lastEventId = eventDao.getLastEventId();
        } catch (DaoException ex) {
            throw new StorageException("Failed to get last event id", ex);
        }
        return lastEventId;
    }

    public void processEvent(StockEvent stockEvent) {
        Handler handler = getHandler(stockEvent);
        if (handler != null) {
            HandleTask handleTask = new HandleTask(stockEvent, handler);

            Future<EventContext> eventContextFuture = executorService.submit(handleTask);

            try {
                queue.put(eventContextFuture);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
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

    public void start() {
        queue = new LinkedBlockingQueue(queueLimit);
        eventSaver = new EventSaver(queue, paymentService, invoiceService);
        Thread newThread = new Thread(eventSaver, "EventSaver");
        newThread.start();
    }
}
