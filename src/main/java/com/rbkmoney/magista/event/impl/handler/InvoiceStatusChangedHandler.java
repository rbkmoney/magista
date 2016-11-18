package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.event.EventType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.event.impl.mapper.InvoiceStatusMapper;
import com.rbkmoney.magista.event.impl.processor.InvoiceStatusChangeProcessor;
import com.rbkmoney.magista.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Component
public class InvoiceStatusChangedHandler extends AbstractInvoiceEventHandler {

    @Autowired
    InvoiceService invoiceService;

    @Override
    public Processor handle(StockEvent event) {
        InvoiceEventContext context = generateContext(event);
        return new InvoiceStatusChangeProcessor(invoiceService, context.getInvoiceStatusChange());
    }

    @Override
    public EventType getEventType() {
        return EventType.INVOICE_STATUS_CHANGED;
    }

    @Override
    List<Mapper> getMappers() {
        return Arrays.asList(
                new InvoiceStatusMapper()
        );
    }
}
