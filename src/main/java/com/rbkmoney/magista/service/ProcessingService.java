package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.magista.event.EventSaver;
import com.rbkmoney.magista.event.HandleTask;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by tolkonepiu on 24.08.16.
 */
@Service
public class ProcessingService {

    private final List<Handler> handlers;

    private final BlockingQueue<Future<Processor>> queue;

    private final EventSaver eventSaver;

    private final ExecutorService executorService;

    @Autowired
    public ProcessingService(ExecutorService executorService, BlockingQueue<Future<Processor>> queue, EventSaver eventSaver, List<Handler> handlers) {
        this.executorService = executorService;
        this.handlers = handlers;
        this.queue = queue;
        this.eventSaver = eventSaver;
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

}
