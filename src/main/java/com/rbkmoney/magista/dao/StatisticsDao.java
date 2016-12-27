package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.Payment;

import java.time.Instant;
import java.util.*;

/**
 * Created by vpankrashkin on 10.08.16.
 */
public interface StatisticsDao {
    Collection<Invoice> getInvoices(
            String merchantId,
            int shopId,
            Optional<String> invoiceId,
            Optional<String> invoiceStatus,
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset
    ) throws DaoException;

    int getInvoicesCount(String merchantId,
                         int shopId,
                         Optional<String> invoiceId,
                         Optional<String> invoiceStatus,
                         Optional<Instant> fromTime,
                         Optional<Instant> toTime,
                         Optional<Integer> limit,
                         Optional<Integer> offset) throws DaoException;

    Collection<Payment> getPayments(
            String merchantId,
            int shopId,
            Optional<String> invoiceId,
            Optional<String> paymentId,
            Optional<String> paymentStatus,
            Optional<String> panMask,
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset
    ) throws DaoException;

    Integer getPaymentsCount(
            String merchantId,
            int shopId,
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
            int shopId,
            Instant fromTime,
            Instant toTime,
            int splitInterval
    ) throws DaoException;

    Collection<Map<String, String>> getPaymentsGeoStat(
            String merchantId,
            int shopId,
            Instant fromTime,
            Instant toTime,
            int splitInterval
    ) throws DaoException;

    Collection<Map<String, String>> getPaymentsConversionStat(
            String merchantId,
            int shopId,
            Instant fromTime,
            Instant toTime,
            int splitInterval
    ) throws DaoException;

    Collection<Map<String, String>> getCustomersRateStat(
            String merchantId,
            int shopId,
            Instant fromTime,
            Instant toTime,
            int splitInterval
    ) throws DaoException;

    Collection<Map<String, String>> getPaymentsCardTypesStat(
            String merchantId,
            int shopId,
            Instant fromTime,
            Instant toTime,
            int splitInterval
    ) throws DaoException;

    Collection<Map<String, String>> getAccountingDataByPeriod(
            Instant fromTime,
            Instant toTime
    ) throws DaoException;

}
