package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStarted;
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

        InvoicePaymentStarted invoicePaymentStarted = invoiceChange
                .getInvoicePaymentChange()
                .getPayload()
                .getInvoicePaymentStarted();

        InvoicePayment invoicePayment = invoicePaymentStarted.getPayment();

        invoiceEventStat.setPaymentId(invoicePayment.getId());

        PaymentRoute paymentRoute = invoicePaymentStarted.getRoute();
        invoiceEventStat.setPaymentProviderId(paymentRoute.getProvider().getId());
        invoiceEventStat.setPaymentTerminalId(paymentRoute.getTerminal().getId());

        invoiceEventStat.setPaymentDomainRevision(invoicePayment.getDomainRevision());

        Payer payer = invoicePayment.getPayer();

        if (payer.isSetPaymentResource()) {
            PaymentResourcePayer resourcePayer = payer.getPaymentResource();

            DisposablePaymentResource paymentResource = resourcePayer.getResource();
            invoiceEventStat.setPaymentSessionId(paymentResource.getPaymentSessionId());

            ContactInfo contactInfo = resourcePayer.getContactInfo();
            invoiceEventStat.setPaymentEmail(contactInfo.getEmail());
            invoiceEventStat.setPaymentPhoneNumber(contactInfo.getPhoneNumber());

            ClientInfo clientInfo = paymentResource.getClientInfo();
            invoiceEventStat.setPaymentFingerprint(clientInfo.getFingerprint());
            invoiceEventStat.setPaymentIp(clientInfo.getIpAddress());

            PaymentTool paymentTool = paymentResource.getPaymentTool();
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
        } else if (payer.isSetCustomer()) {
            invoiceEventStat.setPaymentCustomerId(payer.getCustomer().getCustomerId());
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
