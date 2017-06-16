package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.*;
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
public class PaymentMapper implements Mapper<InvoiceEventContext> {
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
        invoiceEventStat.setEventCategory(InvoiceEventCategory.PAYMENT);
        invoiceEventStat.setEventType(InvoiceEventType.INVOICE_PAYMENT_STARTED);

        Event processingEvent = stockEvent.getSourceEvent().getProcessingEvent();

        String invoiceId = processingEvent.getSource().getInvoice();

        InvoicePayment invoicePayment = processingEvent
                .getPayload()
                .getInvoiceEvent()
                .getInvoicePaymentEvent()
                .getInvoicePaymentStarted()
                .getPayment();

        invoiceEventStat.setPaymentId(invoicePayment.getId());
        invoiceEventStat.setInvoiceId(invoiceId);

        Payer payer = invoicePayment.getPayer();

        invoiceEventStat.setPaymentSessionId(payer.getSessionId());

        ContactInfo contactInfo = payer.getContactInfo();
        invoiceEventStat.setPaymentEmail(contactInfo.getEmail());
        invoiceEventStat.setPaymentPhoneNumber(contactInfo.getPhoneNumber());

        ClientInfo clientInfo = payer.getClientInfo();
        invoiceEventStat.setPaymentFingerprint(clientInfo.getFingerprint());
        invoiceEventStat.setPaymentIp(clientInfo.getIpAddress());

        PaymentTool paymentTool = payer.getPaymentTool();
        invoiceEventStat.setPaymentTool(paymentTool.getSetField().getFieldName());
        if (paymentTool.isSetBankCard()) {
            BankCard bankCard = paymentTool.getBankCard();
            invoiceEventStat.setPaymentMaskedPan(bankCard.getMaskedPan());
            invoiceEventStat.setPaymentSystem(bankCard.getPaymentSystem().toString());
            invoiceEventStat.setPaymentBin(bankCard.getBin());
            invoiceEventStat.setPaymentToken(bankCard.getToken());
        }

        InvoicePaymentStatus status = invoicePayment.getStatus();
        invoiceEventStat.setPaymentStatus(
                com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.valueOf(status.getSetField().getFieldName())
        );
        if (status.isSetFailed()) {
            OperationFailure operationFailure = status.getFailed().getFailure();
            invoiceEventStat.setPaymentStatusFailureCode(operationFailure.getCode());
            invoiceEventStat.setPaymentStatusFailureDescription(operationFailure.getDescription());
        }

        Cash cost = invoicePayment.getCost();
        invoiceEventStat.setPaymentAmount(cost.getAmount());
        invoiceEventStat.setPaymentCurrencyCode(cost.getCurrency().getSymbolicCode());

        Instant createdAt = Instant.from(TemporalConverter.stringToTemporal(invoicePayment.getCreatedAt()));
        invoiceEventStat.setPaymentCreatedAt(LocalDateTime.ofInstant(createdAt, ZoneOffset.UTC));

        if (invoicePayment.isSetContext()) {
            invoiceEventStat.setPaymentContext(invoicePayment.getContext().getData());
        }

        return invoiceEventStat;
    }

}
