package com.rbkmoney.magista.event.flow;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.event.EventSaver;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class AbstractEventFlow {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final BlockingQueue queue;
    private final List<Handler> handlers;
    private final ThreadGroup threadGroup;
    private final ExecutorService executorService;
    private final EventSaver eventSaver;
    private final Thread eventSaverThread;
    private final long timeout;

    private AtomicBoolean isRun = new AtomicBoolean(false);

    public AbstractEventFlow(String name, List<Handler> handlers, int threadPoolSize, int queueLimit, long timeout) {
        this.threadGroup = new ThreadGroup(name + "Flow");
        this.threadGroup.setDaemon(true);
        this.handlers = handlers;
        this.queue = new LinkedBlockingQueue<>(queueLimit);
        this.executorService = Executors.newFixedThreadPool(threadPoolSize, new ThreadFactory() {
            AtomicInteger counter = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(threadGroup, r, name + "Handler-" + counter.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        });
        this.eventSaver = new EventSaver(queue, timeout);
        this.eventSaverThread = new Thread(threadGroup, eventSaver, name + "Saver");
        this.timeout = timeout;
    }

    public void start() {
        if (isRun.compareAndSet(false, true)) {
            eventSaverThread.start();
            log.info("Event saver thread started");
        }
    }

    public abstract void processEvent(StockEvent stockEvent);

    protected <C> List<Handler> getHandlers(C change) {
        return handlers.stream()
                .filter(handler -> handler.accept(change))
                .collect(Collectors.toList());
    }

    protected void submitAndPutInQueue(Callable<Processor> task) {
        Future<Processor> processorFuture = executorService.submit(task);
        try {
            queue.put(processorFuture);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        if (isRun.compareAndSet(true, false)) {
            eventSaver.stop(eventSaverThread);
            executorService.shutdownNow();

            try {
                long startTime = System.currentTimeMillis();
                if (!executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
                    log.warn("Failed to stop enrichment tasks");
                } else {
                    log.info("Enrichment tasks stopped");
                }
                long newTimeout = timeout - (System.currentTimeMillis() - startTime);

                if (newTimeout > 0) {
                    eventSaverThread.join(newTimeout);
                }

                if (eventSaverThread.isAlive()) {
                    log.warn("Failed to stop event saver thread");
                } else {
                    log.info("Event saver stopped");
                }

            } catch (InterruptedException e) {
                log.warn("Waiting for tasks shutdown is interrupted");
            }

            if (!threadGroup.isDestroyed()) {
                log.warn("Failed to stop event flow");
            } else {
                log.info("Event flow stopped");
            }
        }
    }

}
