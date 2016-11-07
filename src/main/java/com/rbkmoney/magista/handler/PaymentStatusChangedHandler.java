package com.rbkmoney.magista.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStatusChanged;
import com.rbkmoney.magista.model.PaymentStatusChange;
import com.rbkmoney.magista.service.PaymentService;
import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Created by tolkonepiu on 10.08.16.
 */
@Component
public class PaymentStatusChangedHandler implements Handler<StockEvent, PaymentStatusChange> {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private String path = "source_event.processing_event.payload.invoice_event.invoice_payment_event.invoice_payment_status_changed.status";

    @Autowired
    private PaymentService paymentService;

    private Filter filter;

    public PaymentStatusChangedHandler() {
        filter = new PathConditionFilter(new PathConditionRule(path));
    }


    @Override
    public PaymentStatusChange handle(StockEvent value) {
        Event event = value.getSourceEvent().getProcessingEvent();
        String invoiceId = event.getSource().getInvoice();
        Instant changedAt = Instant.from(TemporalConverter.stringToTemporal(event.getCreatedAt()));
        InvoicePaymentStatusChanged invoicePaymentStatusChanged = event.getPayload().getInvoiceEvent().getInvoicePaymentEvent().getInvoicePaymentStatusChanged();

        PaymentStatusChange paymentStatusChange = new PaymentStatusChange();
        paymentStatusChange.setEventId(event.getId());
        paymentStatusChange.setInvoiceId(invoiceId);
        paymentStatusChange.setPaymentId(invoicePaymentStatusChanged.getPaymentId());
        paymentStatusChange.setChangedAt(changedAt);
        paymentStatusChange.setStatus(invoicePaymentStatusChanged.getStatus());

        return paymentStatusChange;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
