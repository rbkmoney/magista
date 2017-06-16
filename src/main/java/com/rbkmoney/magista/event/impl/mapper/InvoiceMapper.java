package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.InvoiceDetails;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Created by tolkonepiu on 10/11/2016.
 */
public class InvoiceMapper implements Mapper<InvoiceEventContext> {

    @Override
    public InvoiceEventContext fill(InvoiceEventContext value) {

        StockEvent stockEvent = value.getSource();

        InvoiceEventStat invoiceEventStat = createInvoiceEvent(stockEvent);
        value.setInvoiceEventStat(invoiceEventStat);

        return value;
    }

    private InvoiceEventStat createInvoiceEvent(StockEvent stockEvent) {
        InvoiceEventStat invoiceEventStat = new InvoiceEventStat();

        Event event = stockEvent.getSourceEvent().getProcessingEvent();
        invoiceEventStat.setEventId(event.getId());

        Instant eventCreatedAt = Instant.from(TemporalConverter.stringToTemporal(event.getCreatedAt()));
        invoiceEventStat.setEventCreatedAt(LocalDateTime.ofInstant(eventCreatedAt, ZoneOffset.UTC));
        invoiceEventStat.setEventCategory(InvoiceEventCategory.INVOICE);
        invoiceEventStat.setEventType(InvoiceEventType.INVOICE_CREATED);

        com.rbkmoney.damsel.domain.Invoice invoice = event.getPayload()
                .getInvoiceEvent()
                .getInvoiceCreated().getInvoice();

        invoiceEventStat.setInvoiceId(invoice.getId());
        invoiceEventStat.setPartyShopId(invoice.getShopId());
        invoiceEventStat.setPartyId(invoice.getOwnerId());

        InvoiceDetails details = invoice.getDetails();
        invoiceEventStat.setInvoiceProduct(details.getProduct());
        invoiceEventStat.setInvoiceDescription(details.getDescription());

        InvoiceStatus invoiceStatus = invoice.getStatus();
        invoiceEventStat.setInvoiceStatus(
                com.rbkmoney.magista.domain.enums.InvoiceStatus.valueOf(invoiceStatus.getSetField().getFieldName())
        );
        if (invoiceStatus.isSetCancelled()) {
            invoiceEventStat.setInvoiceStatusDetails(invoiceStatus.getCancelled().getDetails());
        } else if (invoiceStatus.isSetFulfilled()) {
            invoiceEventStat.setInvoiceStatusDetails(invoiceStatus.getFulfilled().getDetails());
        }

        Instant invoiceCreatedAt = Instant.from(TemporalConverter.stringToTemporal(invoice.getCreatedAt()));
        invoiceEventStat.setInvoiceCreatedAt(LocalDateTime.ofInstant(invoiceCreatedAt, ZoneOffset.UTC));

        Instant invoiceDue = Instant.from(TemporalConverter.stringToTemporal(invoice.getDue()));
        invoiceEventStat.setInvoiceDue(LocalDateTime.ofInstant(invoiceDue, ZoneOffset.UTC));

        invoiceEventStat.setInvoiceAmount(invoice.getCost().getAmount());
        invoiceEventStat.setInvoiceCurrencyCode(invoice.getCost().getCurrency().getSymbolicCode());

        if (invoice.isSetContext()) {
            invoiceEventStat.setInvoiceContext(invoice.getContext().getData());
        }

        return invoiceEventStat;
    }


}
