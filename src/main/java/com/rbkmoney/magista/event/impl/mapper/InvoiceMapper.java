package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.InvoiceDetails;
import com.rbkmoney.damsel.domain.InvoiceStatus;
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

        InvoiceDetails details = invoiceCreated.getInvoice().getDetails();
        invoice.setProduct(details.getProduct());
        invoice.setDescription(details.getDescription());

        InvoiceStatus invoiceStatus = invoiceCreated.getInvoice().getStatus();
        invoice.setStatus(invoiceStatus.getSetField());
        if (invoiceStatus.isSetCancelled()) {
            invoice.setStatusDetails(invoiceStatus.getCancelled().getDetails());
        } else if (invoiceStatus.isSetFulfilled()) {
            invoice.setStatusDetails(invoiceStatus.getFulfilled().getDetails());
        }

        Instant createdAt = Instant.from(TemporalConverter.stringToTemporal(invoiceCreated.getInvoice().getCreatedAt()));
        invoice.setCreatedAt(createdAt);
        invoice.setChangedAt(createdAt);

        Instant due = Instant.from(TemporalConverter.stringToTemporal(invoiceCreated.getInvoice().getDue()));
        invoice.setDue(due);

        invoice.setAmount(invoiceCreated.getInvoice().getCost().getAmount());
        invoice.setCurrencyCode(invoiceCreated.getInvoice().getCost().getCurrency().getSymbolicCode());

        if(invoiceCreated.getInvoice().isSetContext()) {
            invoice.setContext(invoiceCreated.getInvoice().getContext().getData());
        }

        value.setInvoice(invoice);

        return value;
    }

}
