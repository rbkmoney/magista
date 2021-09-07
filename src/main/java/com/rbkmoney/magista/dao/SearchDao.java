package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.*;

import java.util.List;

public interface SearchDao extends DarkMessiahSearchDao {

    List<StatInvoice> getInvoices(InvoiceSearchQuery invoiceSearchQuery);

    List<StatPayment> getPayments(PaymentSearchQuery paymentSearchQuery);

    List<StatRefund> getRefunds(RefundSearchQuery refundSearchQuery);

    List<StatPayout> getPayouts(PayoutSearchQuery payoutSearchQuery);

    List<StatChargeback> getChargebacks(ChargebackSearchQuery chargebackSearchQuery);

}
