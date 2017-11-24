package com.rbkmoney.magista.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.damsel.merch_stat.BankCard;
import com.rbkmoney.damsel.merch_stat.CustomerPayer;
import com.rbkmoney.damsel.merch_stat.ExternalFailure;
import com.rbkmoney.damsel.merch_stat.InvoiceCancelled;
import com.rbkmoney.damsel.merch_stat.InvoiceFulfilled;
import com.rbkmoney.damsel.merch_stat.InvoicePaid;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentCancelled;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentCaptured;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentFailed;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentFlow;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentFlowHold;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentFlowInstant;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentPending;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentProcessed;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentRefunded;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentStatus;
import com.rbkmoney.damsel.merch_stat.InvoiceStatus;
import com.rbkmoney.damsel.merch_stat.InvoiceUnpaid;
import com.rbkmoney.damsel.merch_stat.OnHoldExpiration;
import com.rbkmoney.damsel.merch_stat.OperationFailure;
import com.rbkmoney.damsel.merch_stat.OperationTimeout;
import com.rbkmoney.damsel.merch_stat.Payer;
import com.rbkmoney.damsel.merch_stat.PaymentResourcePayer;
import com.rbkmoney.damsel.merch_stat.PaymentTerminal;
import com.rbkmoney.damsel.merch_stat.PaymentTool;
import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.damsel.merch_stat.TerminalPaymentProvider;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.serializer.kit.json.JsonHandler;
import com.rbkmoney.geck.serializer.kit.json.JsonProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.exception.NotFoundException;
import org.apache.thrift.TBase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by tolkonepiu on 23/06/2017.
 */
public class DamselUtil {

    public static LocalDateTime getAdjustmentStatusCreatedAt(InvoicePaymentAdjustmentStatus adjustmentStatus) {
        switch (adjustmentStatus.getSetField()) {
            case CAPTURED:
                return TypeUtil.stringToLocalDateTime(adjustmentStatus.getCaptured().getAt());
            case CANCELLED:
                return TypeUtil.stringToLocalDateTime(adjustmentStatus.getCancelled().getAt());
            default:
                return null;
        }
    }

    public static String getInvoiceStatusDetails(com.rbkmoney.damsel.domain.InvoiceStatus invoiceStatus) {
        switch (invoiceStatus.getSetField()) {
            case FULFILLED:
                return invoiceStatus.getFulfilled().getDetails();
            case CANCELLED:
                return invoiceStatus.getCancelled().getDetails();
            default:
                return null;
        }
    }

    public static String toJson(TBase tBase) {
        try {
            return new TBaseProcessor().process(tBase, new JsonHandler()).toString();
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static <T extends TBase> T fromJson(String jsonString, Class<T> type) {
        try {
            return new JsonProcessor().process(new ObjectMapper().readTree(jsonString), new TBaseHandler<>(type));
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static long getAmount(List<FinalCashFlowPosting> finalCashFlowPostings, Predicate<FinalCashFlowPosting> predicate) {
        return finalCashFlowPostings.stream()
                .filter(predicate)
                .mapToLong(posting -> posting.getVolume().getAmount())
                .sum();
    }


    public static Map<FeeType, Long> getFees(List<FinalCashFlowPosting> finalCashFlowPostings) {
        return finalCashFlowPostings.stream()
                .collect(
                        Collectors.groupingBy(
                                DamselUtil::getFeeType,
                                Collectors.summingLong(posting -> posting.getVolume().getAmount())
                        )
                );
    }

    public static FeeType getFeeType(FinalCashFlowPosting cashFlowPosting) {
        CashFlowAccount source = cashFlowPosting.getSource().getAccountType();
        CashFlowAccount destination = cashFlowPosting.getDestination().getAccountType();

        if (source.isSetMerchant()
                && source.getMerchant() == MerchantCashFlowAccount.settlement
                && destination.isSetSystem()) {
            return FeeType.FEE;
        }

        if (source.isSetSystem()
                && destination.isSetExternal()) {
            return FeeType.EXTERNAL_FEE;
        }

        if (source.isSetSystem()
                && destination.isSetProvider()) {
            return FeeType.PROVIDER_FEE;
        }

        return FeeType.UNKNOWN;
    }

    public static StatPayout toStatPayout(PayoutEventStat payoutEvent) {
        StatPayout statPayout = new StatPayout();
        statPayout.setId(payoutEvent.getPayoutId());
        statPayout.setPartyId(payoutEvent.getPartyId());
        statPayout.setShopId(payoutEvent.getPartyShopId());
        statPayout.setAmount(payoutEvent.getPayoutAmount());
        statPayout.setStatus(toPayoutStatus(payoutEvent));
        statPayout.setFee(Optional.ofNullable(payoutEvent.getPayoutFee()).orElse(0L));
        statPayout.setCurrencySymbolicCode(payoutEvent.getPayoutCurrencyCode());
        statPayout.setCreatedAt(
                TypeUtil.temporalToString(payoutEvent.getPayoutCreatedAt())
        );
        statPayout.setType(toPayoutType(payoutEvent));

        return statPayout;
    }

    public static PayoutStatus toPayoutStatus(PayoutEventStat payoutEvent) {
        PayoutStatus._Fields payoutStatus = PayoutStatus._Fields.findByName(payoutEvent.getPayoutStatus().getLiteral());
        switch (payoutStatus) {
            case UNPAID:
                return PayoutStatus.unpaid(new PayoutUnpaid());
            case PAID:
                return PayoutStatus.paid(new PayoutPaid());
            case CANCELLED:
                return PayoutStatus.cancelled(new PayoutCancelled(payoutEvent.getPayoutCancelDetails()));
            case CONFIRMED:
                return PayoutStatus.confirmed(new PayoutConfirmed());
            default:
                throw new NotFoundException(String.format("Payout status '%s' not found", payoutStatus.getFieldName()));
        }
    }

    public static PayoutType toPayoutType(PayoutEventStat payoutEvent) {
        PayoutType._Fields payoutType = PayoutType._Fields.findByName(payoutEvent.getPayoutType().getLiteral());
        switch (payoutType) {
            case BANK_ACCOUNT:
                PayoutAccount payoutAccount = new PayoutAccount();
                com.rbkmoney.damsel.merch_stat.BankAccount bankAccount = new com.rbkmoney.damsel.merch_stat.BankAccount();
                bankAccount.setAccount(payoutEvent.getPayoutAccountBankId());
                bankAccount.setBankBik(payoutEvent.getPayoutAccountBankBik());
                bankAccount.setBankPostAccount(payoutEvent.getPayoutAccountBankCorrId());
                bankAccount.setBankName(payoutEvent.getPayoutAccountBankName());
                payoutAccount.setAccount(bankAccount);
                payoutAccount.setInn(payoutEvent.getPayoutAccountInn());
                payoutAccount.setPurpose(payoutEvent.getPayoutAccountPurpose());

                return PayoutType.bank_account(payoutAccount);
            case BANK_CARD:
                PayoutCard payoutCard = new PayoutCard();
                com.rbkmoney.damsel.merch_stat.BankCard bankCard = new com.rbkmoney.damsel.merch_stat.BankCard();
                bankCard.setToken(payoutEvent.getPayoutCardToken());
                bankCard.setPaymentSystem(BankCardPaymentSystem.valueOf(payoutEvent.getPayoutCardPaymentSystem()));
                bankCard.setBin(payoutEvent.getPayoutCardBin());
                bankCard.setMaskedPan(payoutEvent.getPayoutCardMaskedPan());
                payoutCard.setCard(bankCard);
                return PayoutType.bank_card(payoutCard);
            default:
                throw new NotFoundException(String.format("Payout type '%s' not found", payoutType.getFieldName()));
        }
    }

    public static StatPayment toStatPayment(InvoiceEventStat invoicePaymentStat) {
        StatPayment statPayment = new StatPayment();

        statPayment.setId(invoicePaymentStat.getPaymentId());
        statPayment.setInvoiceId(invoicePaymentStat.getInvoiceId());
        statPayment.setOwnerId(invoicePaymentStat.getPartyId());
        statPayment.setShopId(invoicePaymentStat.getPartyShopId());
        statPayment.setCreatedAt(TypeUtil.temporalToString(invoicePaymentStat.getPaymentCreatedAt()));
        statPayment.setStatus(toStatPaymentStatus(invoicePaymentStat));

        statPayment.setAmount(invoicePaymentStat.getPaymentAmount());
        statPayment.setFee(invoicePaymentStat.getPaymentFee());
        statPayment.setCurrencySymbolicCode(invoicePaymentStat.getPaymentCurrencyCode());

        if (invoicePaymentStat.getPaymentTool() != null) {
            PaymentResourcePayer paymentResourcePayer = new PaymentResourcePayer();
            PaymentTool paymentTool = toStatPaymentTool(invoicePaymentStat);
            paymentResourcePayer.setPaymentTool(paymentTool);
            paymentResourcePayer.setIpAddress(invoicePaymentStat.getPaymentIp());
            paymentResourcePayer.setFingerprint(invoicePaymentStat.getPaymentFingerprint());
            paymentResourcePayer.setPhoneNumber(invoicePaymentStat.getPaymentPhoneNumber());
            paymentResourcePayer.setEmail(invoicePaymentStat.getPaymentEmail());
            paymentResourcePayer.setSessionId(invoicePaymentStat.getPaymentSessionId());

            statPayment.setPayer(Payer.payment_resource(paymentResourcePayer));
        } else if (invoicePaymentStat.getPaymentCustomerId() != null) {
            statPayment.setPayer(Payer.customer(new CustomerPayer(invoicePaymentStat.getPaymentCustomerId())));
        }

        if (invoicePaymentStat.getPaymentContext() != null) {
            Content content = new Content();
            content.setType(invoicePaymentStat.getPaymentContextType());
            content.setData(invoicePaymentStat.getPaymentContext());
            statPayment.setContext(content);
        }

        statPayment.setFlow(toInvoicePaymentFlow(invoicePaymentStat));

        if (invoicePaymentStat.getPaymentCountryId() != null && invoicePaymentStat.getPaymentCityId() != null) {
            LocationInfo locationInfo = new LocationInfo(
                    invoicePaymentStat.getPaymentCityId(),
                    invoicePaymentStat.getPaymentCountryId()
            );
            statPayment.setLocationInfo(locationInfo);
        }

        return statPayment;
    }

    public static InvoicePaymentFlow toInvoicePaymentFlow(InvoiceEventStat invoicePaymentStat) {
        InvoicePaymentFlow._Fields paymentFlow = InvoicePaymentFlow._Fields.findByName(invoicePaymentStat.getPaymentFlow());
        switch (paymentFlow) {
            case HOLD:
                return InvoicePaymentFlow.hold(new InvoicePaymentFlowHold(
                        OnHoldExpiration.valueOf(invoicePaymentStat.getPaymentHoldOnExpiration()),
                        TypeUtil.temporalToString(invoicePaymentStat.getPaymentHoldUntil())
                ));
            case INSTANT:
                return InvoicePaymentFlow.instant(new InvoicePaymentFlowInstant());
            default:
                throw new NotFoundException(String.format("Payment flow '%s' not found.", invoicePaymentStat.getPaymentFlow()));
        }
    }

    public static PaymentTool toStatPaymentTool(InvoiceEventStat invoicePaymentStat) {
        PaymentTool._Fields paymentTool = PaymentTool._Fields.findByName(invoicePaymentStat.getPaymentTool());
        switch (paymentTool) {
            case BANK_CARD:
                return PaymentTool.bank_card(new BankCard(
                        invoicePaymentStat.getPaymentToken(),
                        BankCardPaymentSystem.valueOf(invoicePaymentStat.getPaymentSystem()),
                        invoicePaymentStat.getPaymentBin(),
                        invoicePaymentStat.getPaymentMaskedPan()
                ));
            case PAYMENT_TERMINAL:
                return PaymentTool.payment_terminal(new PaymentTerminal(
                        TerminalPaymentProvider.valueOf(invoicePaymentStat.getPaymentTerminalProvider())
                ));
            default:
                throw new NotFoundException(String.format("Payment tool '%s' not found", paymentTool.getFieldName()));
        }
    }

    public static InvoicePaymentStatus toStatPaymentStatus(InvoiceEventStat invoicePaymentStat) {
        InvoicePaymentStatus._Fields status = InvoicePaymentStatus._Fields.findByName(
                invoicePaymentStat.getPaymentStatus().getLiteral()
        );
        switch (status) {
            case PENDING:
                return InvoicePaymentStatus.pending(new InvoicePaymentPending());
            case PROCESSED:
                return InvoicePaymentStatus.processed(new InvoicePaymentProcessed());
            case CAPTURED:
                return InvoicePaymentStatus.captured(new InvoicePaymentCaptured());
            case CANCELLED:
                return InvoicePaymentStatus.cancelled(new InvoicePaymentCancelled());
            case REFUNDED:
                return InvoicePaymentStatus.refunded(new InvoicePaymentRefunded());
            case FAILED:
                return InvoicePaymentStatus.failed(new InvoicePaymentFailed(
                        toOperationFailure(invoicePaymentStat)
                ));
            default:
                throw new NotFoundException(String.format("Payment status '%s' not found", status.getFieldName()));
        }
    }

    public static OperationFailure toOperationFailure(InvoiceEventStat invoicePaymentStat) {
        OperationFailure._Fields failureType = OperationFailure._Fields.findByName(invoicePaymentStat.getPaymentFailureClass());
        switch (failureType) {
            case OPERATION_TIMEOUT:
                return OperationFailure.operation_timeout(new OperationTimeout());
            case EXTERNAL_FAILURE:
                ExternalFailure externalFailure = new ExternalFailure();
                externalFailure.setCode(invoicePaymentStat.getPaymentExternalFailureCode());
                externalFailure.setDescription(invoicePaymentStat.getPaymentExternalFailureDescription());
                return OperationFailure.external_failure(externalFailure);
            default:
                throw new NotFoundException(String.format("Failure type '%s' not found", invoicePaymentStat.getPaymentFailureClass()));
        }
    }

    public static StatInvoice toStatInvoice(InvoiceEventStat invoiceEventStat) {
        StatInvoice statInvoice = new StatInvoice();
        statInvoice.setId(invoiceEventStat.getInvoiceId());
        statInvoice.setOwnerId(invoiceEventStat.getPartyId());
        statInvoice.setShopId(invoiceEventStat.getPartyShopId());
        statInvoice.setCreatedAt(TypeUtil.temporalToString(invoiceEventStat.getInvoiceCreatedAt()));

        statInvoice.setStatus(toStatInvoiceStatus(invoiceEventStat));

        statInvoice.setProduct(invoiceEventStat.getInvoiceProduct());
        statInvoice.setDescription(invoiceEventStat.getInvoiceDescription());

        statInvoice.setDue(
                TypeUtil.temporalToString(invoiceEventStat.getInvoiceDue())
        );
        statInvoice.setAmount(invoiceEventStat.getInvoiceAmount());
        statInvoice.setCurrencySymbolicCode(invoiceEventStat.getInvoiceCurrencyCode());

        if (Objects.nonNull(invoiceEventStat.getInvoiceCart())) {
            statInvoice.setCart(DamselUtil.fromJson(invoiceEventStat.getInvoiceCart(), InvoiceCart.class));
        }

        if (invoiceEventStat.getInvoiceContext() != null) {
            Content content = new Content();
            content.setType(invoiceEventStat.getInvoiceContextType());
            content.setData(invoiceEventStat.getInvoiceContext());
            statInvoice.setContext(content);
        }

        return statInvoice;
    }

    public static InvoiceStatus toStatInvoiceStatus(InvoiceEventStat invoiceEventStat) throws NotFoundException {
        InvoiceStatus._Fields invoiceStatus = InvoiceStatus._Fields.findByName(
                invoiceEventStat.getInvoiceStatus().getLiteral()
        );
        switch (invoiceStatus) {
            case UNPAID:
                return com.rbkmoney.damsel.merch_stat.InvoiceStatus.unpaid(new InvoiceUnpaid());
            case PAID:
                return com.rbkmoney.damsel.merch_stat.InvoiceStatus.paid(new InvoicePaid());
            case CANCELLED:
                return com.rbkmoney.damsel.merch_stat.InvoiceStatus.cancelled(
                        new InvoiceCancelled(invoiceEventStat.getInvoiceStatusDetails())
                );
            case FULFILLED:
                return com.rbkmoney.damsel.merch_stat.InvoiceStatus.fulfilled(
                        new InvoiceFulfilled(invoiceEventStat.getInvoiceStatusDetails())
                );
            default:
                throw new NotFoundException(String.format("Status '%s' not found", invoiceEventStat.getInvoiceStatus()));
        }
    }

}
