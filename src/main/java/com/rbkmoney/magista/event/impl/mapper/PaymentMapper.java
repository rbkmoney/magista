package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;

/**
 * Created by tolkonepiu on 10/11/2016.
 */
public class PaymentMapper implements Mapper<InvoiceEventContext> {
    @Override
    public InvoiceEventContext fill(InvoiceEventContext value) {

        InvoiceChange invoiceChange = value.getInvoiceChange();
        InvoiceEventStat invoiceEventStat = value.getInvoiceEventStat();
        invoiceEventStat = createInvoicePaymentEvent(invoiceChange, invoiceEventStat);

        value.setInvoiceEventStat(invoiceEventStat);

        return value;
    }

    private InvoiceEventStat createInvoicePaymentEvent(InvoiceChange invoiceChange, InvoiceEventStat invoiceEventStat) {
        invoiceEventStat.setEventCategory(InvoiceEventCategory.PAYMENT);
        invoiceEventStat.setEventType(InvoiceEventType.INVOICE_PAYMENT_STARTED);

        InvoicePayment invoicePayment = invoiceChange
                .getInvoicePaymentChange()
                .getPayload()
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
        if (paymentTool.isSetPaymentTerminal()) {
            invoiceEventStat.setPaymentTerminalProvider(
                    paymentTool.getPaymentTerminal().getTerminalType().toString()
            );
        }

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
            invoiceEventStat.setPaymentFailureClass(operationFailure.getSetField().getFieldName());
            if (operationFailure.isSetExternalFailure()) {
                ExternalFailure externalFailure = operationFailure.getExternalFailure();
                invoiceEventStat.setPaymentExternalFailureCode(externalFailure.getCode());
                invoiceEventStat.setPaymentExternalFailureDescription(externalFailure.getDescription());
            }
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

        InvoicePaymentFlow paymentFlow = invoicePayment.getFlow();
        invoiceEventStat.setPaymentFlow(paymentFlow.getSetField().getFieldName());
        if (paymentFlow.isSetHold()) {
            InvoicePaymentFlowHold hold = paymentFlow.getHold();
            invoiceEventStat.setPaymentHoldOnExpiration(hold.getOnHoldExpiration().name());
            invoiceEventStat.setPaymentHoldUntil(
                    TypeUtil.stringToLocalDateTime(hold.getHeldUntil())
            );
        }

        return invoiceEventStat;
    }

}
