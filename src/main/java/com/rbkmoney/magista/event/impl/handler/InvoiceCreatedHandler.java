package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.event.EventType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.event.impl.mapper.EventMapper;
import com.rbkmoney.magista.event.impl.mapper.InvoiceMapper;
import com.rbkmoney.magista.event.impl.mapper.InvoicePartyMapper;
import com.rbkmoney.magista.event.impl.processor.InvoiceEventProcessor;
import com.rbkmoney.magista.service.InvoiceEventService;
import com.rbkmoney.magista.service.PartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Component
public class InvoiceCreatedHandler extends AbstractInvoiceEventHandler {

    @Autowired
    InvoiceEventService invoiceEventService;

    @Autowired
    PartyService partyService;

    @Override
    public Processor handle(StockEvent event) {
        InvoiceEventContext context = generateContext(event);
        return new InvoiceEventProcessor(invoiceEventService, context.getInvoiceEventStat());
    }

    @Override
    public EventType getEventType() {
        return EventType.INVOICE_CREATED;
    }

    @Override
    List<Mapper> getMappers() {
        return Arrays.asList(
                new EventMapper(),
                new InvoiceMapper(),
                new InvoicePartyMapper(partyService)
        );
    }
}
