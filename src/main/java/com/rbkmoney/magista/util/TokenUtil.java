package com.rbkmoney.magista.util;

import com.rbkmoney.magista.InvoicePaymentRefundStatus;
import com.rbkmoney.magista.InvoicePaymentStatus;
import com.rbkmoney.magista.StatPayment;
import com.rbkmoney.magista.StatRefund;
import com.rbkmoney.magista.okko.EnrichedStatInvoice;

import java.util.List;

import static org.apache.http.util.TextUtils.isBlank;

public class TokenUtil {
    public static  <T> T getLastElement(List<T> objects) {
        return objects.get(objects.size() - 1);
    }

    public static String getEnrichedPaymentsDateTime(List<EnrichedStatInvoice> invoices) {
        return invoices
                .stream()
                .flatMap(enrichedStatInvoice -> enrichedStatInvoice.getPayments().stream())
                .map(TokenUtil::extractEventOccuredAtTime)
                .min(String::compareTo)
                .orElse(null);
    }

    public static String getEnrichedRefundsDateTime(List<EnrichedStatInvoice> invoices) {
        return invoices
                .stream()
                .flatMap(enrichedStatInvoice -> enrichedStatInvoice.getRefunds().stream())
                .map(TokenUtil::extractEventOccuredAtTime)
                .min(String::compareTo)
                .orElse(null);
    }

    public static String extractEventOccuredAtTime(StatRefund o) {
        InvoicePaymentRefundStatus status = o.getStatus();
        String eventOccuredAt = null;
        if (status.isSetFailed()) {
            eventOccuredAt = status.getFailed().getAt();
        } else if (status.isSetPending()) {
            // no eventOccuredAt field
        } else if (status.isSetSucceeded()) {
            eventOccuredAt = status.getSucceeded().getAt();
        }
        if (!isBlank(eventOccuredAt)) {
            return eventOccuredAt;
        } else {
            return o.getCreatedAt(); //we can't return null, return CreatedAt instead
        }
    }

    private static String extractEventOccuredAtTime(StatPayment o) {
        InvoicePaymentStatus status = o.getStatus();
        String eventOccuredAt = null;
        if (status.isSetFailed()) {
            eventOccuredAt = status.getFailed().getAt();
        } else if (status.isSetCancelled()) {
            eventOccuredAt = status.getCancelled().getAt();
        } else if (status.isSetCaptured()) {
            eventOccuredAt = status.getCaptured().getAt();
        } else if (status.isSetPending()) {
            // no eventOccuredAt field
        } else if (status.isSetProcessed()) {
            eventOccuredAt = status.getProcessed().getAt();
        } else if (status.isSetRefunded()) {
            eventOccuredAt = status.getRefunded().getAt();
        } else if (status.isSetChargedBack()) {
            eventOccuredAt = status.getChargedBack().getAt();
        }
        if (!isBlank(eventOccuredAt)) {
            return eventOccuredAt;
        } else {
            return o.getCreatedAt(); //we can't return null, return CreatedAt instead
        }
    }
}
