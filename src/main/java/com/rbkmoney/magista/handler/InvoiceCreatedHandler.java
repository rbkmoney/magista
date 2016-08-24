package com.rbkmoney.magista.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.InvoiceCreated;
import com.rbkmoney.magista.service.InvoiceService;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Component
public class InvoiceCreatedHandler implements Handler<StockEvent> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private String path = "source_event.processing_event.payload.invoice_event.invoice_created.invoice";

    @Autowired
    private InvoiceService invoiceService;

    private Filter filter;

    public InvoiceCreatedHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }

    @Override
    public void handle(StockEvent value) {
        long eventId = value.getSourceEvent().getProcessingEvent().getId();
        InvoiceCreated invoiceCreated = value.getSourceEvent().getProcessingEvent().getPayload().getInvoiceEvent().getInvoiceCreated();

        invoiceService.saveInvoice(eventId, invoiceCreated);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
