package com.rbkmoney.magista.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.geck.serializer.kit.json.JsonHandler;
import com.rbkmoney.geck.serializer.kit.json.JsonProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import org.apache.thrift.TBase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    public static String getInvoiceStatusDetails(InvoiceStatus invoiceStatus) {
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


    public static Map<PostingType, Long> calculateCommissions(List<FinalCashFlowPosting> finalCashFlowPostings) {
        return finalCashFlowPostings.stream()
                .collect(
                        Collectors.groupingBy(
                                DamselUtil::getCommissionType,
                                Collectors.summingLong(posting -> posting.getVolume().getAmount())
                        )
                );
    }

    public static PostingType getCommissionType(FinalCashFlowPosting cashFlowPosting) {
        CashFlowAccount source = cashFlowPosting.getSource().getAccountType();
        CashFlowAccount destination = cashFlowPosting.getDestination().getAccountType();

        if (source.isSetProvider()
                && destination.isSetMerchant()
                && destination.getMerchant() == MerchantCashFlowAccount.settlement) {
            return PostingType.AMOUNT;
        }

        if (source.isSetMerchant()
                && source.getMerchant() == MerchantCashFlowAccount.settlement
                && destination.isSetProvider()) {
            return PostingType.REFUND_AMOUNT;
        }

        if (source.isSetMerchant()
                && source.getMerchant() == MerchantCashFlowAccount.settlement
                && destination.isSetSystem()) {
            return PostingType.FEE;
        }

        if (source.isSetSystem()
                && destination.isSetExternal()) {
            return PostingType.EXTERNAL_FEE;
        }

        if (source.isSetSystem()
                && destination.isSetProvider()) {
            return PostingType.PROVIDER_FEE;
        }

        throw new IllegalArgumentException(String.format("Unknown posting path, source - '%s', destination - '%s'",
                source,
                destination
        ));
    }

}
