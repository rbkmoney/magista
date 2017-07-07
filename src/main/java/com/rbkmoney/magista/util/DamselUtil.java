package com.rbkmoney.magista.util;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.geck.common.util.TypeUtil;

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


    public static Map<CashFlowAccount._Fields, Long> calculateCommissions(List<FinalCashFlowPosting> finalCashFlowPostings) {
        return finalCashFlowPostings.stream()
                .collect(
                        Collectors.groupingBy(
                                DamselUtil::getCommissionType,
                                Collectors.summingLong(posting -> posting.getVolume().getAmount())
                        )
                );
    }

    public static CashFlowAccount._Fields getCommissionType(FinalCashFlowPosting cashFlowPosting) {
        CashFlowAccount source = cashFlowPosting.getSource().getAccountType();
        CashFlowAccount destination = cashFlowPosting.getDestination().getAccountType();

        if (source.isSetProvider()
                && destination.isSetMerchant()
                && destination.getMerchant() == MerchantCashFlowAccount.settlement) {
            return CashFlowAccount._Fields.MERCHANT;
        }

        if (source.isSetMerchant()
                && source.getMerchant() == MerchantCashFlowAccount.settlement
                && destination.isSetSystem()) {
            return CashFlowAccount._Fields.SYSTEM;
        }

        if (source.isSetSystem()
                && destination.isSetExternal()
                && destination.getExternal() == ExternalCashFlowAccount.outcome) {
            return CashFlowAccount._Fields.EXTERNAL;
        }

        if (source.isSetSystem()
                && destination.isSetProvider()) {
            return CashFlowAccount._Fields.PROVIDER;
        }

        throw new IllegalArgumentException(String.format("Unknown posting path, source - '%s', destination - '%s'",
                source.getSetField(),
                destination.getSetField()
        ));
    }

}
