package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.event.impl.mapper.InvoiceMapper;
import com.rbkmoney.magista.event.impl.mapper.InvoicePartyMapper;
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

    private final InvoiceEventService invoiceEventService;
    private final PartyService partyService;

    @Autowired
    public InvoiceCreatedHandler(InvoiceEventService invoiceEventService, PartyService partyService) {
        this.invoiceEventService = invoiceEventService;
        this.partyService = partyService;
    }

    @Override
    public Processor handle(InvoiceChange change, StockEvent event) {
        InvoiceEventContext context = generateContext(change, event);
        return () -> invoiceEventService.saveInvoiceEvent(context.getInvoiceEventStat());
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_CREATED;
    }

    @Override
    List<Mapper> getMappers() {
        return Arrays.asList(
                new InvoiceMapper(),
                new InvoicePartyMapper(partyService)
        );
    }
}
