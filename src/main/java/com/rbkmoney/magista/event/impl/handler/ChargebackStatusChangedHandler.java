package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.domain.InvoicePaymentChargebackStatus;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.ChargebackStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.ChargebackData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.PaymentChargebackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChargebackStatusChangedHandler implements Handler<InvoiceChange, StockEvent> {

    private final PaymentChargebackService paymentChargebackService;

    @Override
    public Processor handle(InvoiceChange change, StockEvent parent) {
        Event event = parent.getSourceEvent().getProcessingEvent();

        ChargebackData chargebackData = new ChargebackData();
        chargebackData.setEventId(parent.getId());
        chargebackData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        chargebackData.setEventType(InvoiceEventType.INVOICE_PAYMENT_CHARGEBACK_STATUS_CHANGED);
        chargebackData.setInvoiceId(event.getSource().getInvoiceId());

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        chargebackData.setPaymentId(paymentId);

        InvoicePaymentChargebackChange invoicePaymentChargebackChange = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentChargebackChange();
        chargebackData.setChargebackId(invoicePaymentChargebackChange.getId());

        InvoicePaymentChargebackStatusChanged invoicePaymentChargebackStatusChanged = invoicePaymentChargebackChange
                .getPayload().getInvoicePaymentChargebackStatusChanged();
        InvoicePaymentChargebackStatus status = invoicePaymentChargebackStatusChanged.getStatus();
        chargebackData.setChargebackStatus(TBaseUtil.unionFieldToEnum(status, ChargebackStatus.class));

        return () -> paymentChargebackService.savePaymentChargeback(chargebackData);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_CHARGEBACK_STATUS_CHANGED;
    }
}
