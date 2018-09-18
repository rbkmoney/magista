package com.rbkmoney.magista.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.merch_stat.InternationalBankDetails;
import com.rbkmoney.damsel.merch_stat.InternationalBankAccount;
import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentRefundFailed;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentRefundPending;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentRefundStatus;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentRefundSucceeded;
import com.rbkmoney.damsel.merch_stat.OperationFailure;
import com.rbkmoney.damsel.merch_stat.OperationTimeout;
import com.rbkmoney.damsel.merch_stat.RussianBankAccount;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.serializer.kit.json.JsonHandler;
import com.rbkmoney.geck.serializer.kit.json.JsonProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TErrorUtil;
import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.domain.tables.pojos.Refund;
import com.rbkmoney.magista.exception.NotFoundException;
import org.apache.thrift.TBase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by tolkonepiu on 23/06/2017.
 */
public class DamselUtil {

    public final static ObjectMapper objectMapper = new ObjectMapper();

    public final static JsonProcessor jsonProcessor = new JsonProcessor();

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

    public static String toJsonString(TBase tBase) {
        return toJson(tBase).toString();
    }

    public static JsonNode toJson(TBase tBase) {
        try {
            return new TBaseProcessor().process(tBase, new JsonHandler());
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
        if (payoutEvent.getPayoutSummary() != null) {
            statPayout.setSummary(toPayoutSummary(payoutEvent));
        }

        return statPayout;
    }

    public static String toPayoutSummaryStatString(List<com.rbkmoney.damsel.payout_processing.PayoutSummaryItem> payoutSummaryItems) {
        try {
            return new ObjectMapper().writeValueAsString(payoutSummaryItems.stream()
                    .map(
                            payoutSummaryItem -> {
                                try {
                                    return new TBaseProcessor().process(payoutSummaryItem, new JsonHandler());
                                } catch (IOException ex) {
                                    throw new RuntimeJsonMappingException(ex.getMessage());
                                }
                            }).collect(Collectors.toList())
            );
        } catch (IOException ex) {
            throw new RuntimeJsonMappingException(ex.getMessage());
        }
    }

    public static List<PayoutSummaryItem> toPayoutSummary(PayoutEventStat payoutEvent) {
        List<PayoutSummaryItem> payoutSummaryItems = new ArrayList<>();
        try {
            for (JsonNode jsonNode : objectMapper.readTree(payoutEvent.getPayoutSummary())) {
                PayoutSummaryItem payoutSummaryItem = jsonToTBase(jsonNode, PayoutSummaryItem.class);
                payoutSummaryItems.add(payoutSummaryItem);
            }
        } catch (IOException ex) {
            throw new RuntimeJsonMappingException(ex.getMessage());
        }
        return payoutSummaryItems;
    }

    public static <T extends TBase> T jsonToTBase(JsonNode jsonNode, Class<T> type) throws IOException {
        return jsonProcessor.process(jsonNode, new TBaseHandler<>(type));
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
                return PayoutType.bank_account(toPayoutAccount(payoutEvent));
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

    private static PayoutAccount toPayoutAccount(PayoutEventStat payoutEvent) {
        switch (payoutEvent.getPayoutAccountType()) {
            case RUSSIAN_PAYOUT_ACCOUNT:
                RussianBankAccount russianBankAccount = new RussianBankAccount();
                russianBankAccount.setAccount(payoutEvent.getPayoutAccountBankId());
                russianBankAccount.setBankBik(payoutEvent.getPayoutAccountBankLocalCode());
                russianBankAccount.setBankPostAccount(payoutEvent.getPayoutAccountBankCorrId());
                russianBankAccount.setBankName(payoutEvent.getPayoutAccountBankName());

                RussianPayoutAccount russianPayoutAccount = new RussianPayoutAccount();
                russianPayoutAccount.setBankAccount(russianBankAccount);
                russianPayoutAccount.setInn(payoutEvent.getPayoutAccountInn());
                russianPayoutAccount.setPurpose(payoutEvent.getPayoutAccountPurpose());
                return PayoutAccount.russian_payout_account(russianPayoutAccount);
            case INTERNATIONAL_PAYOUT_ACCOUNT:
                InternationalBankAccount internationalBankAccount = new InternationalBankAccount();
                internationalBankAccount.setAccountHolder(payoutEvent.getPayoutAccountBankId());
                internationalBankAccount.setIban(payoutEvent.getPayoutAccountBankIban());

                InternationalBankDetails bankDetails = new InternationalBankDetails();
                bankDetails.setName(payoutEvent.getPayoutAccountBankName());
                bankDetails.setBic(payoutEvent.getPayoutAccountBankBic());
                bankDetails.setAbaRtn(payoutEvent.getPayoutAccountBankAbaRtn());
                bankDetails.setAddress(payoutEvent.getPayoutAccountBankAddress());
                bankDetails.setCountry(TypeUtil.toEnumField(payoutEvent.getPayoutAccountBankCountryCode(), Residence.class));
                internationalBankAccount.setBank(bankDetails);

                InternationalBankAccount correspondentBankAccount = new InternationalBankAccount();
                correspondentBankAccount.setAccountHolder(payoutEvent.getPayoutInternationalCorrespondentAccountBankAccount());
                correspondentBankAccount.setNumber(payoutEvent.getPayoutInternationalCorrespondentAccountBankNumber());
                correspondentBankAccount.setIban(payoutEvent.getPayoutInternationalCorrespondentAccountBankIban());
                InternationalBankDetails correspondentBankDetails = new InternationalBankDetails();
                correspondentBankDetails.setName(payoutEvent.getPayoutInternationalCorrespondentAccountBankName());
                correspondentBankDetails.setBic(payoutEvent.getPayoutInternationalCorrespondentAccountBankBic());
                correspondentBankDetails.setAddress(payoutEvent.getPayoutInternationalCorrespondentAccountBankAddress());
                correspondentBankDetails.setAbaRtn(payoutEvent.getPayoutInternationalCorrespondentAccountBankAbaRtn());
                correspondentBankDetails.setCountry(TypeUtil.toEnumField(payoutEvent.getPayoutInternationalCorrespondentAccountBankCountryCode(), Residence.class));
                correspondentBankAccount.setBank(correspondentBankDetails);
                internationalBankAccount.setCorrespondentAccount(correspondentBankAccount);

                InternationalPayoutAccount internationalPayoutAccount = new InternationalPayoutAccount();
                internationalPayoutAccount.setBankAccount(internationalBankAccount);
                internationalPayoutAccount.setPurpose(payoutEvent.getPayoutAccountPurpose());
                return PayoutAccount.international_payout_account(internationalPayoutAccount);
            default:
                throw new NotFoundException(String.format("Payout account type '%s' not found", payoutEvent.getPayoutAccountType()));
        }
    }

    public static OperationFailure toOperationFailure(FailureClass failureClass, String failure, String failureDescription) {
        switch (failureClass) {
            case operation_timeout:
                return OperationFailure.operation_timeout(new OperationTimeout());
            case failure:
                Failure externalFailure = TErrorUtil.toGeneral(failure);
                externalFailure.setReason(failureDescription);
                return OperationFailure.failure(externalFailure);
            default:
                throw new NotFoundException(String.format("Failure type '%s' not found", failureClass));
        }
    }

    public static StatRefund toStatRefund(Refund refundEvent) {
        StatRefund statRefund = new StatRefund();
        statRefund.setId(refundEvent.getRefundId());
        statRefund.setInvoiceId(refundEvent.getInvoiceId());
        statRefund.setPaymentId(refundEvent.getPaymentId());
        statRefund.setOwnerId(refundEvent.getPartyId());
        statRefund.setShopId(refundEvent.getPartyShopId());
        statRefund.setCurrencySymbolicCode(refundEvent.getRefundCurrencyCode());
        statRefund.setStatus(toRefundStatus(refundEvent));
        statRefund.setAmount(refundEvent.getRefundAmount());
        statRefund.setFee(refundEvent.getRefundFee());
        statRefund.setReason(refundEvent.getRefundReason());
        statRefund.setCreatedAt(TypeUtil.temporalToString(refundEvent.getRefundCreatedAt()));
        return statRefund;
    }

    public static InvoicePaymentRefundStatus toRefundStatus(Refund refundEvent) {
        switch (refundEvent.getRefundStatus()) {
            case pending:
                return InvoicePaymentRefundStatus.pending(new InvoicePaymentRefundPending());
            case succeeded:
                return InvoicePaymentRefundStatus.succeeded(new InvoicePaymentRefundSucceeded(
                        TypeUtil.temporalToString(refundEvent.getEventCreatedAt())
                ));
            case failed:
                return InvoicePaymentRefundStatus.failed(new InvoicePaymentRefundFailed(
                        toOperationFailure(
                                refundEvent.getRefundOperationFailureClass(),
                                refundEvent.getRefundExternalFailure(),
                                refundEvent.getRefundExternalFailureReason()
                        ),
                        TypeUtil.temporalToString(refundEvent.getEventCreatedAt())
                ));
            default:
                throw new NotFoundException(String.format("Refund status '%s' not found", refundEvent.getRefundStatus()));
        }
    }

}
