package com.rbkmoney.magista.event.flow;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class InvoiceEventFlow extends AbstractEventFlow {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public InvoiceEventFlow(List<Handler> handlers, TransactionTemplate transactionTemplate, int threadPoolSize, int queueLimit, long timeout) {
        super("invoiceEvent", handlers, transactionTemplate, threadPoolSize, queueLimit, timeout);
    }

    public void processEvent(StockEvent stockEvent) {
        Event event = stockEvent.getSourceEvent().getProcessingEvent();
        if (event.getPayload().isSetInvoiceChanges()) {
            for (InvoiceChange invoiceChange : event.getPayload().getInvoiceChanges()) {
                List<Handler> handlers = getHandlers(invoiceChange);
                submitAndPutInQueue(() -> {
                    List<Processor> processors = handlers.stream()
                            .map(handler -> {
                                log.info("Start invoice event handling, id='{}', eventType='{}', handlerType='{}'",
                                        stockEvent.getSourceEvent().getProcessingEvent().getId(), handler.getChangeType(), handler.getClass().getSimpleName());
                                return handler.handle(invoiceChange, stockEvent);
                            }).collect(Collectors.toList());
                    return () -> processors.forEach(processor -> processor.execute());
                });
            }
        }
    }
}
