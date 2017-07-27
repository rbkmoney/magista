package com.rbkmoney.magista.event;

import com.rbkmoney.magista.exception.AdjustmentException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by tolkonepiu on 31/10/2016.
 */
public class EventSaver implements Runnable {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final BlockingQueue<Future<Processor>> queue;
    private final long timeout;
    private AtomicBoolean started = new AtomicBoolean(false);

    public EventSaver(BlockingQueue<Future<Processor>> queue, long timeout) {
        this.queue = queue;
        this.timeout = timeout;
    }

    public void start() {
        if (!started.compareAndSet(false, true)) {
            Thread eventSaverThread = new Thread(this, "EventSaver");
            eventSaverThread.start();
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                try {
                    Future<Processor> future = queue.peek();
                    if (future != null) {
                        Processor processor = future.get();
                        processor.execute();
                        queue.take();
                        //TODO one bad event can stop the whole processing flow, need to do smth with it...
                    } else {
                        TimeUnit.MILLISECONDS.sleep(timeout);
                    }
                } catch (ExecutionException | AdjustmentException | NotFoundException ex) {
                    queue.take();
                    log.error("Failed to handle event, skipped", ex);
                } catch (StorageException ex) {
                    log.error("Failed to save event after handling", ex);
                    TimeUnit.MILLISECONDS.sleep(timeout);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
