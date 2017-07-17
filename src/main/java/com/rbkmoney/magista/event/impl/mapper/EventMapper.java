package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;

import java.time.LocalDateTime;

/**
 * Created by tolkonepiu on 22/06/2017.
 */
public class EventMapper implements Mapper<InvoiceEventContext> {
    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {
        Event event = context.getSource().getSourceEvent().getProcessingEvent();

        long eventId = event.getId();
        LocalDateTime eventCreatedAt = TypeUtil.stringToLocalDateTime(event.getCreatedAt());
        String invoiceId = event.getSource().getInvoiceId();

        InvoiceEventStat invoiceEventStat = context.getInvoiceEventStat();
        invoiceEventStat.setEventId(eventId);
        invoiceEventStat.setEventCreatedAt(eventCreatedAt);
        invoiceEventStat.setInvoiceId(invoiceId);

        return context.setInvoiceEventStat(invoiceEventStat);
    }
}
