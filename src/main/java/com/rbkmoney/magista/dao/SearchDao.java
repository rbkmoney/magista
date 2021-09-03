package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.*;
import com.rbkmoney.magista.okko.EnrichedStatInvoice;

import java.util.List;

public interface SearchDao {

    List<StatInvoice> getInvoices(InvoiceSearchQuery invoiceSearchQuery);

    List<StatPayment> getPayments(PaymentSearchQuery paymentSearchQuery);

    List<StatRefund> getRefunds(RefundSearchQuery refundSearchQuery);

    List<StatPayout> getPayouts(PayoutSearchQuery payoutSearchQuery);

    List<StatChargeback> getChargebacks(ChargebackSearchQuery chargebackSearchQuery);

    List<EnrichedStatInvoice> getEnrichedInvoices(com.rbkmoney.magista.okko.PaymentSearchQuery paymentSearchQuery);

    List<EnrichedStatInvoice> getEnrichedInvoices(com.rbkmoney.magista.okko.RefundSearchQuery refundSearchQuery);

}
