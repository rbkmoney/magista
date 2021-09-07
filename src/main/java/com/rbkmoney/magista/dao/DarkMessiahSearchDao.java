package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.dark.messiah.EnrichedStatInvoice;
import com.rbkmoney.magista.dark.messiah.PaymentSearchQuery;
import com.rbkmoney.magista.dark.messiah.RefundSearchQuery;

import java.util.List;

public interface DarkMessiahSearchDao {

    List<EnrichedStatInvoice> getEnrichedPaymentInvoices(PaymentSearchQuery paymentSearchQuery);

    List<EnrichedStatInvoice> getEnrichedRefundInvoices(RefundSearchQuery refundSearchQuery);

}
