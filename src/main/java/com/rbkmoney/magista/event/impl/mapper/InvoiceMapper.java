package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.InvoiceCreated;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;

import java.time.Instant;

/**
 * Created by tolkonepiu on 10/11/2016.
 */
public class InvoiceMapper implements Mapper<InvoiceEventContext> {

    @Override
    public InvoiceEventContext fill(InvoiceEventContext value) {

        StockEvent stockEvent = value.getSource();

        long eventId = stockEvent.getSourceEvent().getProcessingEvent().getId();
        InvoiceCreated invoiceCreated = stockEvent.getSourceEvent().getProcessingEvent().getPayload().getInvoiceEvent().getInvoiceCreated();

        Invoice invoice = new Invoice();
        invoice.setId(invoiceCreated.getInvoice().getId());
        invoice.setEventId(eventId);
        invoice.setShopId(invoiceCreated.getInvoice().getShopId());
        invoice.setMerchantId(invoiceCreated.getInvoice().getOwnerId());
        invoice.setStatus(invoiceCreated.getInvoice().getStatus().getSetField());

        Instant createdAt = Instant.from(TemporalConverter.stringToTemporal(invoiceCreated.getInvoice().getCreatedAt()));
        invoice.setCreatedAt(createdAt);

        invoice.setAmount(invoiceCreated.getInvoice().getCost().getAmount());
        invoice.setCurrencyCode(invoiceCreated.getInvoice().getCost().getCurrency().getSymbolicCode());
        invoice.setModel(invoiceCreated.getInvoice());

        value.setInvoice(invoice);

        return value;
    }

}
