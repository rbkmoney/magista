package com.rbkmoney.magista.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by tolkonepiu on 31/10/2016.
 */
public class EventSaver implements Runnable {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final BlockingQueue<Future<Processor>> queue;
    private final long timeout;
    private volatile boolean isRun = true;

    public EventSaver(BlockingQueue<Future<Processor>> queue, long timeout) {
        this.queue = queue;
        this.timeout = timeout;
    }

    public void stop(Thread runningThread) {
        isRun = false;
        runningThread.interrupt();
    }

    @Override
    public void run() {
        while (isRunning()) {
            try {
                try {
                    Future<Processor> future = queue.peek();
                    if (future != null) {
                        Processor processor = future.get();
                        processor.execute();
                        queue.take();
                    } else {
                        TimeUnit.MILLISECONDS.sleep(timeout);
                    }
                } catch (Exception ex) {
                    log.error("Failed to save event after handling", ex);
                    TimeUnit.MILLISECONDS.sleep(timeout);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private boolean isRunning() {
        return isRun && !Thread.currentThread().isInterrupted();
    }
}
