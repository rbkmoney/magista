package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStarted;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.serializer.kit.tbase.TErrorUtil;
import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.OnHoldExpiration;
import com.rbkmoney.magista.domain.enums.PaymentFlow;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentEvent;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.PaymentService;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.FeeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PaymentStartedEventHandler implements Handler<InvoiceChange, StockEvent> {

    private final PaymentService paymentService;

    @Autowired
    public PaymentStartedEventHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public Processor handle(InvoiceChange change, StockEvent parent) {
        Event event = parent.getSourceEvent().getProcessingEvent();

        String invoiceId = event.getSource().getInvoiceId();

        InvoicePaymentStarted invoicePaymentStarted = change
                .getInvoicePaymentChange()
                .getPayload()
                .getInvoicePaymentStarted();

        InvoicePayment invoicePayment = invoicePaymentStarted.getPayment();
        String paymentId = invoicePayment.getId();

        PaymentData paymentData = new PaymentData();
        paymentData.setInvoiceId(invoiceId);
        paymentData.setPaymentId(paymentId);

        PaymentRoute paymentRoute = invoicePaymentStarted.getRoute();
        paymentData.setPaymentProviderId(paymentRoute.getProvider().getId());
        paymentData.setPaymentTerminalId(paymentRoute.getTerminal().getId());
        paymentData.setPaymentDomainRevision(invoicePayment.getDomainRevision());

        Cash cost = invoicePayment.getCost();
        paymentData.setPaymentAmount(cost.getAmount());
        paymentData.setPaymentCurrencyCode(cost.getCurrency().getSymbolicCode());

        paymentData.setPaymentCreatedAt(TypeUtil.stringToLocalDateTime(invoicePayment.getCreatedAt()));

        InvoicePaymentFlow paymentFlow = invoicePayment.getFlow();
        paymentData.setPaymentFlow(TBaseUtil.unionFieldToEnum(paymentFlow, PaymentFlow.class));
        if (paymentFlow.isSetHold()) {
            InvoicePaymentFlowHold hold = paymentFlow.getHold();
            paymentData.setPaymentHoldOnExpiration(OnHoldExpiration.valueOf(hold.getOnHoldExpiration().name()));
            paymentData.setPaymentHoldUntil(TypeUtil.stringToLocalDateTime(hold.getHeldUntil()));
        }

        if (invoicePayment.isSetContext()) {
            Content content = invoicePayment.getContext();
            paymentData.setPaymentContextType(content.getType());
            paymentData.setPaymentContext(content.getData());
        }

        if (invoicePayment.isSetPartyRevision()) {
            paymentData.setPaymentPartyRevision(invoicePayment.getPartyRevision());
        }

        Payer payer = invoicePayment.getPayer();
        if (payer.isSetPaymentResource()) {
            PaymentResourcePayer resourcePayer = payer.getPaymentResource();

            DisposablePaymentResource paymentResource = resourcePayer.getResource();
            paymentData.setPaymentSessionId(paymentResource.getPaymentSessionId());

            mapContactInfo(paymentData, resourcePayer.getContactInfo());
            mapPaymentTool(paymentData, paymentResource.getPaymentTool());

            ClientInfo clientInfo = paymentResource.getClientInfo();
            paymentData.setPaymentFingerprint(clientInfo.getFingerprint());
            paymentData.setPaymentIp(clientInfo.getIpAddress());
        }

        if (payer.isSetCustomer()) {
            CustomerPayer customerPayer = payer.getCustomer();
            paymentData.setPaymentCustomerId(customerPayer.getCustomerId());
            mapPaymentTool(paymentData, customerPayer.getPaymentTool());
            mapContactInfo(paymentData, customerPayer.getContactInfo());
        }

        PaymentEvent paymentEvent = new PaymentEvent();
        paymentEvent.setEventType(InvoiceEventType.INVOICE_PAYMENT_STARTED);
        paymentEvent.setEventId(event.getId());
        paymentEvent.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        paymentEvent.setInvoiceId(invoiceId);
        paymentEvent.setPaymentId(paymentId);

        InvoicePaymentStatus status = invoicePayment.getStatus();
        paymentEvent.setPaymentStatus(
                TBaseUtil.unionFieldToEnum(
                        status,
                        com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class
                )
        );
        if (status.isSetFailed()) {
            OperationFailure operationFailure = status.getFailed().getFailure();
            paymentEvent.setPaymentOperationFailureClass(
                    TBaseUtil.unionFieldToEnum(operationFailure, FailureClass.class)
            );
            if (operationFailure.isSetFailure()) {
                Failure failure = operationFailure.getFailure();
                paymentEvent.setPaymentExternalFailure(TErrorUtil.toStringVal(failure));
                paymentEvent.setPaymentExternalFailureReason(failure.getReason());
            }
        }

        List<FinalCashFlowPosting> finalCashFlowPostings = invoicePaymentStarted.getCashFlow();
        Map<FeeType, Long> fees = DamselUtil.getFees(finalCashFlowPostings);

        paymentEvent.setPaymentFee(fees.get(FeeType.FEE));
        paymentEvent.setPaymentExternalFee(fees.get(FeeType.EXTERNAL_FEE));
        paymentEvent.setPaymentProviderFee(fees.get(FeeType.PROVIDER_FEE));

        return () -> paymentService.savePayment(paymentData, paymentEvent);
    }

    private void mapPaymentTool(PaymentData paymentData, PaymentTool paymentTool) {
        paymentData.setPaymentTool(
                TBaseUtil.unionFieldToEnum(
                        paymentTool,
                        com.rbkmoney.magista.domain.enums.PaymentTool.class
                )
        );
        if (paymentTool.isSetPaymentTerminal()) {
            paymentData.setPaymentTerminalProvider(
                    paymentTool.getPaymentTerminal().getTerminalType().toString()
            );
        }

        if (paymentTool.isSetDigitalWallet()) {
            DigitalWallet digitalWallet = paymentTool.getDigitalWallet();
            paymentData.setPaymentDigitalWalletId(digitalWallet.getId());
            paymentData.setPaymentDigitalWalletProvider(digitalWallet.getProvider().toString());
        }

        if (paymentTool.isSetBankCard()) {
            BankCard bankCard = paymentTool.getBankCard();
            paymentData.setPaymentBankCardMaskedPan(bankCard.getMaskedPan());
            paymentData.setPaymentBankCardSystem(bankCard.getPaymentSystem().toString());
            paymentData.setPaymentBankCardBin(bankCard.getBin());
            paymentData.setPaymentBankCardToken(bankCard.getToken());
        }
    }

    private void mapContactInfo(PaymentData paymentData, ContactInfo contactInfo) {
        paymentData.setPaymentEmail(contactInfo.getEmail());
        paymentData.setPaymentPhoneNumber(contactInfo.getPhoneNumber());
    }


    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_STARTED;
    }
}
