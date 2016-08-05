package com.rbkmoney.magista.handler;

import com.rbkmoney.thrift.filter.converter.TemporalConverter;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.InvoiceCreated;
import com.rbkmoney.magista.repository.InvoiceRepository;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public class InvoiceCreatedHandler implements Handler<StockEvent> {

    String path = "source_event.processing_event.payload.invoice_event.invoice_created";

    @Autowired
    InvoiceRepository repository;

    @Override
    public void handle(StockEvent value) {
        InvoiceCreated invoiceCreated = value.getSourceEvent().getProcessingEvent().getPayload().getInvoiceEvent().getInvoiceCreated();

        Invoice invoice = new Invoice();
        invoice.setId(invoiceCreated.getInvoice().getId());
        invoice.setStatus(invoiceCreated.getInvoice().getStatus().getSetField());
        invoice.setCreatedAt(Instant.from(TemporalConverter.stringToTemporal(invoiceCreated.getInvoice().getCreatedAt())));

        repository.save(invoice);
    }

    @Override
    public Filter getFilter() {
        return new PathConditionFilter(new PathConditionRule(path));
    }
}
