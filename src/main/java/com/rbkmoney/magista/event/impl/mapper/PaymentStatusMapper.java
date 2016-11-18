package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStatusChanged;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.model.PaymentStatusChange;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;

import java.time.Instant;

/**
 * Created by tolkonepiu on 14/11/2016.
 */
public class PaymentStatusMapper implements Mapper<InvoiceEventContext> {
    @Override
    public InvoiceEventContext fill(InvoiceEventContext value) {
        Event event = value.getSource().getSourceEvent().getProcessingEvent();
        String invoiceId = event.getSource().getInvoice();
        Instant changedAt = Instant.from(TemporalConverter.stringToTemporal(event.getCreatedAt()));
        InvoicePaymentStatusChanged invoicePaymentStatusChanged = event.getPayload().getInvoiceEvent().getInvoicePaymentEvent().getInvoicePaymentStatusChanged();

        PaymentStatusChange paymentStatusChange = new PaymentStatusChange();
        paymentStatusChange.setEventId(event.getId());
        paymentStatusChange.setInvoiceId(invoiceId);
        paymentStatusChange.setPaymentId(invoicePaymentStatusChanged.getPaymentId());
        paymentStatusChange.setChangedAt(changedAt);
        paymentStatusChange.setStatus(invoicePaymentStatusChanged.getStatus());

        value.setPaymentStatusChange(paymentStatusChange);

        return value;
    }
}
