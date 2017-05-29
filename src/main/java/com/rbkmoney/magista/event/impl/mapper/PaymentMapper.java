package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.model.Payment;
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

        Payment payment = createPayment(stockEvent);
        value.setPayment(payment);
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
//TODO            invoiceEventStat.setPa(bankCard.getToken());
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

    private Payment createPayment(StockEvent stockEvent) {
        Event event = stockEvent.getSourceEvent().getProcessingEvent();
        long eventId = event.getId();
        String invoiceId = event.getSource().getInvoice();
        InvoicePayment invoicePayment = event.getPayload().getInvoiceEvent().getInvoicePaymentEvent().getInvoicePaymentStarted().getPayment();

        Payment payment = new Payment();
        payment.setId(invoicePayment.getId());
        payment.setEventId(eventId);
        payment.setInvoiceId(invoiceId);

        Payer payer = invoicePayment.getPayer();

        payment.setSessionId(payer.getSessionId());

        ContactInfo contactInfo = payer.getContactInfo();
        payment.setEmail(contactInfo.getEmail());
        payment.setPhoneNumber(contactInfo.getPhoneNumber());

        ClientInfo clientInfo = payer.getClientInfo();
        payment.setCustomerId(clientInfo.getFingerprint());
        payment.setIp(clientInfo.getIpAddress());

        PaymentTool paymentTool = payer.getPaymentTool();
        payment.setPaymentTool(paymentTool.getSetField());
        if (paymentTool.isSetBankCard()) {
            BankCard bankCard = paymentTool.getBankCard();
            payment.setMaskedPan(bankCard.getMaskedPan());
            payment.setPaymentSystem(bankCard.getPaymentSystem());
            payment.setBin(bankCard.getBin());
            payment.setToken(bankCard.getToken());
        }

        InvoicePaymentStatus status = invoicePayment.getStatus();
        payment.setStatus(status.getSetField());
        if (status.isSetFailed()) {
            OperationFailure operationFailure = status.getFailed().getFailure();
            payment.setFailureCode(operationFailure.getCode());
            payment.setFailureDescription(operationFailure.getDescription());
        }

        Cash cost = invoicePayment.getCost();
        payment.setAmount(cost.getAmount());
        payment.setCurrencyCode(cost.getCurrency().getSymbolicCode());

        Instant createdAt = Instant.from(TemporalConverter.stringToTemporal(invoicePayment.getCreatedAt()));
        payment.setCreatedAt(createdAt);
        payment.setChangedAt(createdAt);

        if (invoicePayment.isSetContext()) {
            payment.setContext(invoicePayment.getContext().getData());
        }

        return payment;
    }
}
