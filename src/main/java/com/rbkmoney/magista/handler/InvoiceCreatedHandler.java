package com.rbkmoney.magista.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.InvoiceCreated;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Component
public class InvoiceCreatedHandler implements Handler<StockEvent, Invoice> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private String path = "source_event.processing_event.payload.invoice_event.invoice_created.invoice";

    private Filter filter;

    public InvoiceCreatedHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }

    @Override
    public Invoice handle(StockEvent value) {
        long eventId = value.getSourceEvent().getProcessingEvent().getId();
        InvoiceCreated invoiceCreated = value.getSourceEvent().getProcessingEvent().getPayload().getInvoiceEvent().getInvoiceCreated();

        Invoice invoice = new Invoice();
        invoice.setId(invoiceCreated.getInvoice().getId());
        invoice.setEventId(eventId);
        invoice.setShopId(invoiceCreated.getInvoice().getShopId());
        invoice.setMerchantId(invoiceCreated.getInvoice().getOwner().getId());
        invoice.setStatus(invoiceCreated.getInvoice().getStatus().getSetField());

        Instant createdAt = Instant.from(TemporalConverter.stringToTemporal(invoiceCreated.getInvoice().getCreatedAt()));
        invoice.setCreatedAt(createdAt);
        invoice.setChangedAt(createdAt);

        invoice.setAmount(invoiceCreated.getInvoice().getCost().getAmount());
        invoice.setCurrencyCode(invoiceCreated.getInvoice().getCost().getCurrency().getSymbolicCode());
        invoice.setModel(invoiceCreated.getInvoice());

        return invoice;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
