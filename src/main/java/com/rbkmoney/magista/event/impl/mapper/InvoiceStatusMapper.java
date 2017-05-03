package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.model.InvoiceStatusChange;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;

import java.time.Instant;

/**
 * Created by tolkonepiu on 14/11/2016.
 */
public class InvoiceStatusMapper implements Mapper<InvoiceEventContext> {
    @Override
    public InvoiceEventContext fill(InvoiceEventContext value) {
        Event event = value.getSource().getSourceEvent().getProcessingEvent();
        long eventId = event.getId();
        String invoiceId = event.getSource().getInvoice();
//        Instant changedAt = Instant.from(TemporalConverter.stringToTemporal(event.getCreatedAt()));
        InvoiceStatus status = event.getPayload().getInvoiceEvent().getInvoiceStatusChanged().getStatus();

        InvoiceStatusChange invoiceStatusChange = new InvoiceStatusChange();
        invoiceStatusChange.setEventId(eventId);
        invoiceStatusChange.setInvoiceId(invoiceId);
//        invoiceStatusChange.setChangedAt(changedAt);
        invoiceStatusChange.setStatus(status);

        value.setInvoiceStatusChange(invoiceStatusChange);

        return value;
    }
}
