package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.thrift.filter.converter.TemporalConverter;

import java.time.Instant;

/**
 * Created by tolkonepiu on 10/11/2016.
 */
public class PaymentMapper implements Mapper<InvoiceEventContext> {
    @Override
    public InvoiceEventContext fill(InvoiceEventContext value) {

        StockEvent stockEvent = value.getSource();

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

        value.setPayment(payment);

        return value;
    }
}
