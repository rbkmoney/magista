package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStarted;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.serializer.kit.tbase.TErrorUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.BankCardTokenProvider;
import com.rbkmoney.magista.domain.enums.OnHoldExpiration;
import com.rbkmoney.magista.domain.enums.*;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.PaymentMapper;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.FeeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentStartedEventMapper implements PaymentMapper {

    @Override
    public PaymentData map(InvoiceChange change, MachineEvent machineEvent) {

        String invoiceId = machineEvent.getSourceId();

        InvoicePaymentStarted invoicePaymentStarted = change
                .getInvoicePaymentChange()
                .getPayload()
                .getInvoicePaymentStarted();

        InvoicePayment invoicePayment = invoicePaymentStarted.getPayment();
        String paymentId = invoicePayment.getId();

        PaymentData paymentData = new PaymentData();
        paymentData.setInvoiceId(invoiceId);
        paymentData.setPaymentId(paymentId);

        Cash cost = invoicePayment.getCost();
        paymentData.setPaymentOriginAmount(cost.getAmount());
        paymentData.setPaymentCurrencyCode(cost.getCurrency().getSymbolicCode());

        paymentData.setPaymentCreatedAt(TypeUtil.stringToLocalDateTime(invoicePayment.getCreatedAt()));

        InvoicePaymentFlow paymentFlow = invoicePayment.getFlow();
        paymentData.setPaymentFlow(TBaseUtil.unionFieldToEnum(paymentFlow, PaymentFlow.class));
        if (paymentFlow.isSetHold()) {
            InvoicePaymentFlowHold hold = paymentFlow.getHold();
            paymentData.setPaymentHoldOnExpiration(OnHoldExpiration.valueOf(hold.getOnHoldExpiration().name()));
            paymentData.setPaymentHoldUntil(TypeUtil.stringToLocalDateTime(hold.getHeldUntil()));
        }

        if (invoicePayment.isSetMakeRecurrent()) {
            paymentData.setPaymentMakeRecurrentFlag(invoicePayment.isMakeRecurrent());
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

        PaymentPayerType payerType = TBaseUtil.unionFieldToEnum(payer, PaymentPayerType.class);
        paymentData.setPaymentPayerType(payerType);
        switch (paymentData.getPaymentPayerType()) {
            case payment_resource:
                PaymentResourcePayer resourcePayer = payer.getPaymentResource();

                DisposablePaymentResource paymentResource = resourcePayer.getResource();
                paymentData.setPaymentSessionId(paymentResource.getPaymentSessionId());

                mapContactInfo(paymentData, resourcePayer.getContactInfo());
                mapPaymentTool(paymentData, paymentResource.getPaymentTool());

                if (paymentResource.isSetClientInfo()) {
                    ClientInfo clientInfo = paymentResource.getClientInfo();
                    paymentData.setPaymentFingerprint(clientInfo.getFingerprint());
                }
                break;
            case customer:
                CustomerPayer customerPayer = payer.getCustomer();
                paymentData.setPaymentCustomerId(customerPayer.getCustomerId());
                mapPaymentTool(paymentData, customerPayer.getPaymentTool());
                mapContactInfo(paymentData, customerPayer.getContactInfo());
                break;
            case recurrent:
                RecurrentPayer recurrentPayer = payer.getRecurrent();
                mapContactInfo(paymentData, recurrentPayer.getContactInfo());
                mapPaymentTool(paymentData, recurrentPayer.getPaymentTool());
                RecurrentParentPayment recurrentParentPayment = recurrentPayer.getRecurrentParent();
                paymentData.setPaymentRecurrentPayerParentInvoiceId(recurrentParentPayment.getInvoiceId());
                paymentData.setPaymentRecurrentPayerParentPaymentId(recurrentParentPayment.getPaymentId());
                break;
            default:
                throw new NotFoundException(String.format("Payment type '%s' not found", payerType));
        }

        paymentData.setEventType(InvoiceEventType.INVOICE_PAYMENT_STARTED);
        paymentData.setEventId(machineEvent.getEventId());

        Optional.ofNullable(invoicePayment.getOwnerId())
                .map(UUID::fromString)
                .ifPresent(paymentData::setPartyId);

        paymentData.setPartyShopId(invoicePayment.getShopId());
        paymentData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        paymentData.setInvoiceId(invoiceId);
        paymentData.setPaymentId(paymentId);

        InvoicePaymentStatus status = invoicePayment.getStatus();
        paymentData.setPaymentStatus(
                TBaseUtil.unionFieldToEnum(
                        status,
                        com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class
                )
        );
        if (status.isSetFailed()) {
            OperationFailure operationFailure = status.getFailed().getFailure();
            paymentData.setPaymentOperationFailureClass(
                    TBaseUtil.unionFieldToEnum(operationFailure, FailureClass.class)
            );
            if (operationFailure.isSetFailure()) {
                Failure failure = operationFailure.getFailure();
                paymentData.setPaymentExternalFailure(TErrorUtil.toStringVal(failure));
                paymentData.setPaymentExternalFailureReason(failure.getReason());
            }
        }
        paymentData.setPaymentDomainRevision(invoicePayment.getDomainRevision());

        paymentData.setPaymentAmount(cost.getAmount());
        paymentData.setPaymentCurrencyCode(cost.getCurrency().getSymbolicCode());

        if (invoicePaymentStarted.isSetRoute()) {
            PaymentRoute paymentRoute = invoicePaymentStarted.getRoute();
            paymentData.setPaymentProviderId(paymentRoute.getProvider().getId());
            paymentData.setPaymentTerminalId(paymentRoute.getTerminal().getId());
        }

        if (invoicePaymentStarted.isSetCashFlow()) {
            List<FinalCashFlowPosting> finalCashFlowPostings = invoicePaymentStarted.getCashFlow();
            Map<FeeType, Long> fees = DamselUtil.getFees(finalCashFlowPostings);
            paymentData.setPaymentFee(fees.getOrDefault(FeeType.FEE, 0L));
            paymentData.setPaymentExternalFee(fees.getOrDefault(FeeType.EXTERNAL_FEE, 0L));
            paymentData.setPaymentProviderFee(fees.getOrDefault(FeeType.PROVIDER_FEE, 0L));
        }

        return paymentData;
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
            paymentData.setPaymentBankCardLast4(bankCard.getMaskedPan());
            paymentData.setPaymentBankCardSystem(bankCard.getPaymentSystem().toString());
            paymentData.setPaymentBankCardFirst6(bankCard.getBin());
            paymentData.setPaymentBankCardToken(bankCard.getToken());
            if (bankCard.isSetTokenProvider()) {
                paymentData.setPaymentBankCardTokenProvider(
                        TypeUtil.toEnumField(bankCard.getTokenProvider().name(), BankCardTokenProvider.class)
                );
            }
        }

        if (paymentTool.isSetCryptoCurrency()) {
            paymentData.setCryptoCurrency(paymentTool.getCryptoCurrency().toString());
        }

        if (paymentTool.isSetMobileCommerce()) {
            MobileOperator mobileOperator = paymentTool.getMobileCommerce().getOperator();
            paymentData.setPaymentMobileOperator(TypeUtil.toEnumField(mobileOperator.name(), MobileOperatorType.class));
            paymentData.setPaymentMobilePhoneCc(paymentTool.getMobileCommerce().getPhone().getCc());
            paymentData.setPaymentMobilePhoneCtn(paymentTool.getMobileCommerce().getPhone().getCtn());
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
