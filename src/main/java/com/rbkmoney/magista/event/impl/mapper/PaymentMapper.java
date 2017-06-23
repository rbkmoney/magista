package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
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
        InvoiceEventStat invoiceEventStat = value.getInvoiceEventStat();
        invoiceEventStat = createInvoicePaymentEvent(stockEvent, invoiceEventStat);

        value.setInvoiceEventStat(invoiceEventStat);

        return value;
    }

    private InvoiceEventStat createInvoicePaymentEvent(StockEvent stockEvent, InvoiceEventStat invoiceEventStat) {
        invoiceEventStat.setEventCategory(InvoiceEventCategory.PAYMENT);
        invoiceEventStat.setEventType(InvoiceEventType.INVOICE_PAYMENT_STARTED);

        Event processingEvent = stockEvent.getSourceEvent().getProcessingEvent();

        InvoicePayment invoicePayment = processingEvent
                .getPayload()
                .getInvoiceEvent()
                .getInvoicePaymentEvent()
                .getInvoicePaymentStarted()
                .getPayment();

        invoiceEventStat.setPaymentId(invoicePayment.getId());

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
                TBaseUtil.unionFieldToEnum(
                        status,
                        com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class
                )
        );
        if (status.isSetFailed()) {
            OperationFailure operationFailure = status.getFailed().getFailure();
            invoiceEventStat.setPaymentStatusFailureCode(operationFailure.getCode());
            invoiceEventStat.setPaymentStatusFailureDescription(operationFailure.getDescription());
        }

        Cash cost = invoicePayment.getCost();
        invoiceEventStat.setPaymentAmount(cost.getAmount());
        invoiceEventStat.setPaymentCurrencyCode(cost.getCurrency().getSymbolicCode());

        invoiceEventStat.setPaymentCreatedAt(
                TypeUtil.stringToLocalDateTime(invoicePayment.getCreatedAt())
        );

        if (invoicePayment.isSetContext()) {
            invoiceEventStat.setPaymentContext(invoicePayment.getContext().getData());
        }

        return invoiceEventStat;
    }

}
