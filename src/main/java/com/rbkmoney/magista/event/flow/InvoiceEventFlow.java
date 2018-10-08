package com.rbkmoney.magista.event.flow;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.eventstock.client.poll.DefaultPollingEventPublisherBuilder;
import com.rbkmoney.magista.event.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class InvoiceEventFlow extends AbstractEventFlow {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public InvoiceEventFlow(List<Handler> handlers, DefaultPollingEventPublisherBuilder defaultPollingEventPublisherBuilder, int threadPoolSize, int queueLimit, long timeout) {
        super("invoiceEvent", handlers, defaultPollingEventPublisherBuilder, threadPoolSize, queueLimit, timeout);
    }

    public void processEvent(StockEvent stockEvent) {
        Event event = stockEvent.getSourceEvent().getProcessingEvent();
        if (event.getPayload().isSetInvoiceChanges()) {
            for (InvoiceChange invoiceChange : event.getPayload().getInvoiceChanges()) {
                Handler handler = getHandler(invoiceChange);
                if (handler != null) {
                    log.info("Start invoice event handling, id='{}', eventType='{}', handlerType='{}'",
                            stockEvent.getSourceEvent().getProcessingEvent().getId(), handler.getChangeType(), handler.getClass().getSimpleName());
                    submitAndPutInQueue(() -> handler.handle(invoiceChange, stockEvent));
                }
            }
        }
    }
}
