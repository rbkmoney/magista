package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.domain.Cash;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.PaymentEvent;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.PaymentService;
import org.springframework.stereotype.Component;

@Component
public class PaymentCaptureStartedMapper implements Handler<InvoiceChange, StockEvent> {

    private final PaymentService paymentService;

    public PaymentCaptureStartedMapper(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public Processor handle(InvoiceChange change, StockEvent parent) {
        Event event = parent.getSourceEvent().getProcessingEvent();

        PaymentEvent paymentEvent = new PaymentEvent();
        paymentEvent.setEventId((long) event.getSequence());
        paymentEvent.setEventType(InvoiceEventType.INVOICE_PAYMENT_CAPTURE_STARTED);
        paymentEvent.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        paymentEvent.setInvoiceId(event.getSource().getInvoiceId());

        final InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        paymentEvent.setPaymentId(invoicePaymentChange.getId());

        final InvoicePaymentCaptureStarted invoicePaymentCaptureStarted = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentCaptureStarted();

        if (invoicePaymentCaptureStarted.getParams().isSetCash()) {
            Cash cash = invoicePaymentCaptureStarted.getParams().getCash();
            paymentEvent.setPaymentAmount(cash.getAmount());
            paymentEvent.setPaymentCurrencyCode(cash.getCurrency().getSymbolicCode());
        }

        return () -> paymentService.savePaymentChange(paymentEvent);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_CAPTURE_STARTED;
    }
}
