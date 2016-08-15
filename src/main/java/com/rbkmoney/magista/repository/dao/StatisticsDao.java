package com.rbkmoney.magista.repository.dao;

import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.query.Pair;
import com.rbkmoney.magista.repository.DaoException;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Created by vpankrashkin on 10.08.16.
 */
public interface StatisticsDao {
    Pair<Integer, Collection<Invoice>> getInvoices(
            String merchantId,
            String shopId,
            Optional<String> invoiceId,
            Optional<String> invoiceStatus,
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset
    ) throws DaoException;
    Pair<Integer, Collection<Payment>> getPayments(
            String merchantId,
            String shopId,
            Optional<String> invoiceId,
            Optional<String> paymentId,
            Optional<String> paymentStatus,
            Optional<String> panMask,
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset
    ) throws DaoException;

    Collection<Map<String, String>> getPaymentsTurnoverStat(
            String merchantId,
            String shopId,
            Instant fromTime,
            Instant toTime,
            int splitInterval
    ) throws DaoException;

    Collection<Map<String, String>> getPaymentsGeoStat(
            String merchantId,
            String shopId,
            Instant fromTime,
            Instant toTime,
            int splitInterval
    ) throws DaoException;

    Collection<Map<String, String>> getPaymentsConversionStat(
            String merchantId,
            String shopId,
            Instant fromTime,
            Instant toTime,
            int splitInterval
    ) throws DaoException;

    Collection<Map<String, String>> getCustomersRateStat(
            String merchantId,
            String shopId,
            Instant fromTime,
            Instant toTime,
            int splitInterval
    ) throws DaoException;

}
