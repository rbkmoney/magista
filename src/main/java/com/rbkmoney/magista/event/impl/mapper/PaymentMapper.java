package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStarted;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.serializer.kit.tbase.TErrorUtil;
import com.rbkmoney.magista.domain.enums.FailureClass;
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

            mapContactInfo(invoiceEventStat, resourcePayer.getContactInfo());
            mapPaymentTool(invoiceEventStat, paymentResource.getPaymentTool());

            ClientInfo clientInfo = paymentResource.getClientInfo();
            invoiceEventStat.setPaymentFingerprint(clientInfo.getFingerprint());
            invoiceEventStat.setPaymentIp(clientInfo.getIpAddress());
        }

        if (payer.isSetCustomer()) {
            CustomerPayer customerPayer = payer.getCustomer();
            invoiceEventStat.setPaymentCustomerId(customerPayer.getCustomerId());
            mapPaymentTool(invoiceEventStat, customerPayer.getPaymentTool());
            mapContactInfo(invoiceEventStat, customerPayer.getContactInfo());
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
            invoiceEventStat.setPaymentOperationFailureClass(
                    TBaseUtil.unionFieldToEnum(operationFailure, FailureClass.class)
            );
            if (operationFailure.isSetFailure()) {
                Failure failure = operationFailure.getFailure();
                invoiceEventStat.setPaymentExternalFailure(TErrorUtil.toStringVal(failure));
                invoiceEventStat.setPaymentExternalFailureDescription(failure.getReason());
            }
        }

        if (invoicePayment.isSetPartyRevision()) {
            invoiceEventStat.setPaymentPartyRevision(invoicePayment.getPartyRevision());
        }

        Cash cost = invoicePayment.getCost();
        invoiceEventStat.setPaymentAmount(cost.getAmount());
        invoiceEventStat.setPaymentCurrencyCode(cost.getCurrency().getSymbolicCode());

        invoiceEventStat.setPaymentCreatedAt(
                TypeUtil.stringToLocalDateTime(invoicePayment.getCreatedAt())
        );

        if (invoicePayment.isSetContext()) {
            Content content = invoicePayment.getContext();
            invoiceEventStat.setPaymentContextType(content.getType());
            invoiceEventStat.setPaymentContext(content.getData());
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

    private void mapPaymentTool(InvoiceEventStat invoiceEventStat, PaymentTool paymentTool) {
        invoiceEventStat.setPaymentTool(paymentTool.getSetField().getFieldName());
        if (paymentTool.isSetPaymentTerminal()) {
            invoiceEventStat.setPaymentTerminalProvider(
                    paymentTool.getPaymentTerminal().getTerminalType().toString()
            );
        }

        if (paymentTool.isSetDigitalWallet()) {
            DigitalWallet digitalWallet = paymentTool.getDigitalWallet();
            invoiceEventStat.setPaymentDigitalWalletId(digitalWallet.getId());
            invoiceEventStat.setPaymentDigitalWalletProvider(digitalWallet.getProvider().toString());
        }

        if (paymentTool.isSetBankCard()) {
            BankCard bankCard = paymentTool.getBankCard();
            invoiceEventStat.setPaymentMaskedPan(bankCard.getMaskedPan());
            invoiceEventStat.setPaymentSystem(bankCard.getPaymentSystem().toString());
            invoiceEventStat.setPaymentBin(bankCard.getBin());
            invoiceEventStat.setPaymentToken(bankCard.getToken());
        }
    }

    private void mapContactInfo(InvoiceEventStat invoiceEventStat, ContactInfo contactInfo) {
        invoiceEventStat.setPaymentEmail(contactInfo.getEmail());
        invoiceEventStat.setPaymentPhoneNumber(contactInfo.getPhoneNumber());
    }

}
