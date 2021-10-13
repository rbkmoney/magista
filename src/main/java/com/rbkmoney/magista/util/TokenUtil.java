package com.rbkmoney.magista.util;

import com.rbkmoney.magista.StatPayment;
import com.rbkmoney.magista.StatRefund;
import com.rbkmoney.magista.dark.messiah.EnrichedStatInvoice;

import java.util.List;

public class TokenUtil {
    public static  <T> T getLastElement(List<T> objects) {
        return objects.get(objects.size() - 1);
    }

    public static String getEnrichedPaymentsDateTime(List<EnrichedStatInvoice> invoices) {
        return invoices
                .stream()
                .flatMap(enrichedStatInvoice -> enrichedStatInvoice.getPayments().stream())
                .map(StatPayment::getStatusChangedAt)
                .min(String::compareTo)
                .orElse(null);
    }

    public static String getEnrichedRefundsDateTime(List<EnrichedStatInvoice> invoices) {
        return invoices
                .stream()
                .flatMap(enrichedStatInvoice -> enrichedStatInvoice.getRefunds().stream())
                .map(StatRefund::getStatusChangedAt)
                .min(String::compareTo)
                .orElse(null);
    }
}
