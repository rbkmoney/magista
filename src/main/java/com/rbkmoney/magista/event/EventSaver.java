package com.rbkmoney.magista.event;

import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by tolkonepiu on 31/10/2016.
 */
public class EventSaver implements Runnable {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private BlockingQueue<Future<Processor>> queue;

    public EventSaver(BlockingQueue<Future<Processor>> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Future<Processor> future = queue.take();
                Processor processor = future.get();

                processor.execute();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException ex) {
                log.error("Failed to handle event", ex);
            } catch (NotFoundException | StorageException ex) {
                log.error("Failed to save event after handling", ex);
            }
        }
    }
}
