package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.domain.InvoicePaymentChargeback;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackCreated;
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
public class ChargebackCreatedHandler implements Handler<InvoiceChange, StockEvent> {

    private final PaymentChargebackService paymentChargebackService;

    @Override
    public Processor handle(InvoiceChange change, StockEvent parent) {
        Event event = parent.getSourceEvent().getProcessingEvent();

        ChargebackData chargeback = new ChargebackData();
        chargeback.setEventId(event.getId());
        chargeback.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        chargeback.setEventType(InvoiceEventType.INVOICE_PAYMENT_CHARGEBACK_CREATED);
        chargeback.setInvoiceId(event.getSource().getInvoiceId());

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        chargeback.setPaymentId(paymentId);

        InvoicePaymentChargebackCreated invoicePaymentChargebackCreated = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentChargebackChange()
                .getPayload()
                .getInvoicePaymentChargebackCreated();

        InvoicePaymentChargeback invoicePaymentChargeback = invoicePaymentChargebackCreated.getChargeback();

        chargeback.setChargebackId(invoicePaymentChargeback.getId());
        chargeback.setExternalId(invoicePaymentChargeback.getExternalId());
        chargeback.setChargebackStatus(TBaseUtil.unionFieldToEnum(invoicePaymentChargeback.getStatus(), ChargebackStatus.class));
        chargeback.setChargebackCreatedAt(TypeUtil.stringToLocalDateTime(invoicePaymentChargeback.getCreatedAt()));
        chargeback.setChargebackAmount(invoicePaymentChargeback.getBody().getAmount());
        chargeback.setChargebackCurrencyCode(invoicePaymentChargeback.getBody().getCurrency().getSymbolicCode());
        chargeback.setChargebackReason(invoicePaymentChargeback.getReason().getCode());
        chargeback.setChargebackDomainRevision(invoicePaymentChargeback.getDomainRevision());
        chargeback.setChargebackPartyRevision(invoicePaymentChargeback.getPartyRevision());


        return () -> paymentChargebackService.savePaymentChargeback(chargeback);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_CHARGEBACK_CREATED;
    }
}
