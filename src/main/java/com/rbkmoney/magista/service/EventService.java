package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.eventstock.client.poll.Pair;
import com.rbkmoney.magista.dao.EventDao;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.handler.EventSaver;
import com.rbkmoney.magista.handler.HandleTask;
import com.rbkmoney.magista.handler.Handler;
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

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventDao eventDao;

    @Autowired
    private List<Handler> handlers;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PaymentService paymentService;

    private BlockingQueue<Future<Pair>> queue;

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
        Future<Pair> future = executorService.submit(new HandleTask(stockEvent, handlers));
        try {
            queue.put(future);
        } catch (InterruptedException e) {
            //ALARM! if you see it, don't approve this pull request
        }
    }

    public void start() {
        queue = new LinkedBlockingQueue(queueLimit);
        eventSaver = new EventSaver(queue, paymentService, invoiceService);
        Thread newThread = new Thread(eventSaver, "EventSaver");
        newThread.start();
    }
}
