package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.*;
import com.rbkmoney.magista.okko.EnrichedStatInvoice;
import com.rbkmoney.magista.okko.StatEnrichedStatInvoiceResponse;
import com.rbkmoney.magista.query.impl.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public interface SearchDao {

    Collection<Map.Entry<Long, StatInvoice>> getInvoices(
            InvoiceSearchQuery invoiceSearchQuery
    );

    Collection<Map.Entry<Long, StatPayment>> getPayments(
            PaymentsFunction.PaymentsParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            LocalDateTime whereTime,
            int limit
    );

    Collection<Map.Entry<Long, StatRefund>> getRefunds(
            RefundsFunction.RefundsParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            LocalDateTime whereTime,
            int limit
    );

    Collection<Map.Entry<Long, StatPayout>> getPayouts(
            PayoutsFunction.PayoutsParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            LocalDateTime whereTime,
            int limit
    );

    Collection<Map.Entry<Long, StatChargeback>> getChargebacks(
            ChargebacksFunction.ChargebacksParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            LocalDateTime whereTime,
            int limit
    );

    Collection<Map.Entry<Long, EnrichedStatInvoice>> getEnrichedInvoices(
            RefundsFunction.RefundsParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            LocalDateTime whereTime,
            int limit
    );

    Collection<Map.Entry<Long, EnrichedStatInvoice>> getEnrichedInvoices(
            PaymentsFunction.PaymentsParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            LocalDateTime whereTime,
            int limit
    );

}
