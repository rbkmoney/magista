package com.rbkmoney.magista.event.flow;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.magista.event.EventSaver;
import com.rbkmoney.magista.event.HandleTask;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class InvoiceEventFlow {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final BlockingQueue queue;
    private final List<Handler> handlers;
    private final ThreadGroup threadGroup;
    private final ExecutorService executorService;
    private final EventSaver eventSaver;
    private final Thread eventSaverThread;
    private final long timeout;

    private AtomicBoolean isRun = new AtomicBoolean(false);

    public InvoiceEventFlow(List<Handler> handlers, int threadPoolSize, int queueLimit, long timeout) {
        this.threadGroup = new ThreadGroup("InvoiceEventFlow");
        this.handlers = handlers;
        this.queue = new LinkedBlockingQueue<>(queueLimit);
        this.executorService = Executors.newFixedThreadPool(threadPoolSize, new ThreadFactory() {
            AtomicInteger counter = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(threadGroup, r, "InvoiceEventHandler-" + counter.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        });
        this.eventSaver = new EventSaver(queue, timeout);
        this.eventSaverThread = new Thread(threadGroup, eventSaver, "InvoiceEventSaver");
        this.timeout = timeout;
    }

    public void start() {
        if (isRun.compareAndSet(false, true)) {
            eventSaverThread.start();
            log.info("Event saver thread started");
        }
    }

    public void processEvent(StockEvent stockEvent) {
        Event event = stockEvent.getSourceEvent().getProcessingEvent();
        if (event.getPayload().isSetInvoiceChanges()) {
            for (InvoiceChange invoiceChange : event.getPayload().getInvoiceChanges()) {
                Handler handler = getHandler(invoiceChange);
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

    private Handler getHandler(InvoiceChange invoiceChange) {
        for (Handler handler : handlers) {
            if (handler.accept(invoiceChange)) {
                return handler;
            }
        }
        return null;
    }

    public void stop() {
        if (isRun.compareAndSet(true, false)) {
            eventSaver.stop(eventSaverThread);
            try {
                eventSaverThread.join(timeout);
            } catch (InterruptedException e) {
                log.warn("Waiting for event saver shutdown is interrupted");
            }

            if (eventSaverThread.isAlive()) {
                log.warn("Failed to stop event saver thread");
            } else {
                log.info("Event saver stopped");
            }

            executorService.shutdownNow();
            try {
                if (!executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
                    log.warn("Failed to stop enrichment tasks");
                } else {
                    log.info("Enrichment tasks stopped");
                }
            } catch (InterruptedException e) {
                log.warn("Waiting for enrichment tasks shutdown is interrupted");
            }

            if (!threadGroup.isDestroyed()) {
                log.warn("Failed to stop invoice event flow");
            } else {
                log.info("Invoice event flow stopped");
            }
        }
    }
}
