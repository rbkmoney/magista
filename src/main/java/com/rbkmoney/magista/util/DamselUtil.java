package com.rbkmoney.magista.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.serializer.kit.json.JsonHandler;
import com.rbkmoney.geck.serializer.kit.json.JsonProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TErrorUtil;
import com.rbkmoney.magista.OperationFailure;
import com.rbkmoney.magista.OperationTimeout;
import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.exception.NotFoundException;
import org.apache.thrift.TBase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DamselUtil {

    public static final JsonProcessor jsonProcessor = new JsonProcessor();

    public static LocalDateTime getAdjustmentStatusCreatedAt(InvoicePaymentAdjustmentStatus adjustmentStatus) {
        return switch (adjustmentStatus.getSetField()) {
            case CAPTURED -> TypeUtil.stringToLocalDateTime(adjustmentStatus.getCaptured().getAt());
            case CANCELLED -> TypeUtil.stringToLocalDateTime(adjustmentStatus.getCancelled().getAt());
            default -> null;
        };
    }

    public static String getInvoiceStatusDetails(com.rbkmoney.damsel.domain.InvoiceStatus invoiceStatus) {
        return switch (invoiceStatus.getSetField()) {
            case FULFILLED -> invoiceStatus.getFulfilled().getDetails();
            case CANCELLED -> invoiceStatus.getCancelled().getDetails();
            default -> null;
        };
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

    public static long getAmount(
            List<FinalCashFlowPosting> finalCashFlowPostings,
            Predicate<FinalCashFlowPosting> predicate) {
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

        if (source.isSetProvider() && source.getProvider() == ProviderCashFlowAccount.settlement
                && destination.isSetMerchant() && destination.getMerchant() == MerchantCashFlowAccount.settlement) {
            return FeeType.AMOUNT;
        }

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

    public static <T extends TBase> T jsonToTBase(JsonNode jsonNode, Class<T> type) throws IOException {
        return jsonProcessor.process(jsonNode, new TBaseHandler<>(type));
    }

    @Deprecated
    public static com.rbkmoney.damsel.merch_stat.OperationFailure toOperationFailureDeprecated(
            FailureClass failureClass,
            String failure,
            String failureDescription) {
        switch (failureClass) {
            case operation_timeout:
                return com.rbkmoney.damsel.merch_stat.OperationFailure.operation_timeout(
                        new com.rbkmoney.damsel.merch_stat.OperationTimeout());
            case failure:
                Failure externalFailure = TErrorUtil.toGeneral(failure);
                externalFailure.setReason(failureDescription);
                return com.rbkmoney.damsel.merch_stat.OperationFailure.failure(externalFailure);
            default:
                throw new NotFoundException(String.format("Failure type '%s' not found", failureClass));
        }
    }

    public static OperationFailure toOperationFailure(
            FailureClass failureClass, String failure,
            String failureDescription) {
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

    public static String toPayoutSummaryStatString(
            List<com.rbkmoney.damsel.payout_processing.PayoutSummaryItem> payoutSummaryItems) {
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

}
