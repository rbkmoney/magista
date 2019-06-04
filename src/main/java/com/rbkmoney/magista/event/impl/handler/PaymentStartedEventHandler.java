package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.damsel.geo_ip.geo_ipConstants;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStarted;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.serializer.kit.tbase.TErrorUtil;
import com.rbkmoney.magista.domain.enums.BankCardTokenProvider;
import com.rbkmoney.magista.domain.enums.OnHoldExpiration;
import com.rbkmoney.magista.domain.enums.*;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentEvent;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.provider.GeoProvider;
import com.rbkmoney.magista.provider.ProviderException;
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

    private final GeoProvider geoProvider;

    @Autowired
    public PaymentStartedEventHandler(PaymentService paymentService, GeoProvider geoProvider) {
        this.paymentService = paymentService;
        this.geoProvider = geoProvider;
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

                    String paymentIpAddress = clientInfo.getIpAddress();
                    if (paymentIpAddress != null) {
                        paymentData.setPaymentIp(paymentIpAddress);
                        mapLocationInfo(paymentData, paymentIpAddress);
                    }
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
        paymentEvent.setPaymentDomainRevision(invoicePayment.getDomainRevision());

        paymentEvent.setPaymentAmount(cost.getAmount());
        paymentEvent.setPaymentCurrencyCode(cost.getCurrency().getSymbolicCode());

        if (invoicePaymentStarted.isSetRoute()) {
            PaymentRoute paymentRoute = invoicePaymentStarted.getRoute();
            paymentEvent.setPaymentProviderId(paymentRoute.getProvider().getId());
            paymentEvent.setPaymentTerminalId(paymentRoute.getTerminal().getId());
        }

        if (invoicePaymentStarted.isSetCashFlow()) {
            List<FinalCashFlowPosting> finalCashFlowPostings = invoicePaymentStarted.getCashFlow();
            Map<FeeType, Long> fees = DamselUtil.getFees(finalCashFlowPostings);
            paymentEvent.setPaymentFee(fees.getOrDefault(FeeType.FEE, 0L));
            paymentEvent.setPaymentExternalFee(fees.getOrDefault(FeeType.EXTERNAL_FEE, 0L));
            paymentEvent.setPaymentProviderFee(fees.getOrDefault(FeeType.PROVIDER_FEE, 0L));
        }

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
            if (bankCard.isSetTokenProvider()) {
                paymentData.setPaymentBankCardTokenProvider(
                        TypeUtil.toEnumField(bankCard.getTokenProvider().name(), BankCardTokenProvider.class)
                );
            }
        }

        if (paymentTool.isSetCryptoCurrency()) {
            paymentData.setCryptoCurrency(paymentTool.getCryptoCurrency().toString());
        }
    }

    private void mapContactInfo(PaymentData paymentData, ContactInfo contactInfo) {
        paymentData.setPaymentEmail(contactInfo.getEmail());
        paymentData.setPaymentPhoneNumber(contactInfo.getPhoneNumber());
    }

    private void mapLocationInfo(PaymentData paymentData, String ipAddress) {
        try {
            LocationInfo locationInfo = geoProvider.getLocationInfo(ipAddress);
            paymentData.setPaymentCityId(locationInfo.getCityGeoId());
            paymentData.setPaymentCountryId(locationInfo.getCountryGeoId());
        } catch (ProviderException ex) {
            paymentData.setPaymentCityId(geo_ipConstants.GEO_ID_UNKNOWN);
            paymentData.setPaymentCountryId(geo_ipConstants.GEO_ID_UNKNOWN);
        }
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_STARTED;
    }
}
