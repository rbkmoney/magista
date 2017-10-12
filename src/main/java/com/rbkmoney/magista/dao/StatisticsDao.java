package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.exception.DaoException;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by vpankrashkin on 10.08.16.
 */
public interface StatisticsDao {
    Collection<InvoiceEventStat> getInvoices(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<List<Integer>> shopCategoryIds,
            Optional<String> invoiceId,
            Optional<String> paymentId,
            Optional<String> invoiceStatus,
            Optional<String> paymentStatus,
            Optional<Long> invoiceAmount,
            Optional<Long> paymentAmount,
            Optional<String> paymentFlow,
            Optional<String> paymentMethod,
            Optional<String> paymentTerminalProvider,
            Optional<String> paymentEmail,
            Optional<String> paymentIp,
            Optional<String> paymentFingerprint,
            Optional<String> paymentPanMask,
            Optional<String> paymentCustomerId,
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset
    ) throws DaoException;

    int getInvoicesCount(Optional<String> merchantId,
                         Optional<String> shopId,
                         Optional<List<Integer>> shopCategoryIds,
                         Optional<String> invoiceId,
                         Optional<String> paymentId,
                         Optional<String> invoiceStatus,
                         Optional<String> paymentStatus,
                         Optional<Long> invoiceAmount,
                         Optional<Long> paymentAmount,
                         Optional<String> paymentFlow,
                         Optional<String> paymentMethod,
                         Optional<String> paymentTerminalProvider,
                         Optional<String> paymentEmail,
                         Optional<String> paymentIp,
                         Optional<String> paymentFingerprint,
                         Optional<String> paymentPanMask,
                         Optional<String> paymentCustomerId,
                         Optional<Instant> fromTime,
                         Optional<Instant> toTime,
                         Optional<Integer> limit,
                         Optional<Integer> offset) throws DaoException;

    Collection<InvoiceEventStat> getPayments(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<List<Integer>> shopCategoryIds,
            Optional<String> invoiceId,
            Optional<String> paymentId,
            Optional<String> paymentStatus,
            Optional<String> paymentFlow,
            Optional<String> paymentMethod,
            Optional<String> paymentTerminalProvider,
            Optional<String> paymentEmail,
            Optional<String> paymentIp,
            Optional<String> paymentFingerprint,
            Optional<String> paymentPanMask,
            Optional<Long> paymentAmount,
            Optional<String> paymentCustomerId,
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset
    ) throws DaoException;

    Integer getPaymentsCount(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<List<Integer>> shopCategoryIds,
            Optional<String> invoiceId,
            Optional<String> paymentId,
            Optional<String> paymentStatus,
            Optional<String> paymentFlow,
            Optional<String> paymentMethod,
            Optional<String> paymentTerminalProvider,
            Optional<String> paymentEmail,
            Optional<String> paymentIp,
            Optional<String> paymentFingerprint,
            Optional<String> paymentPanMask,
            Optional<Long> paymentAmount,
            Optional<String> paymentCustomerId,
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset
    ) throws DaoException;

    Collection<PayoutEventStat> getPayouts(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<String> payoutId,
            Optional<String> payoutStatus,
            Optional<String> payoutType,
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset
    ) throws DaoException;

    Integer getPayoutsCount(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<String> payoutId,
            Optional<String> payoutStatus,
            Optional<String> payoutType,
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

    Collection<Map<String, String>> getPaymentsCardTypesStat(
            String merchantId,
            String shopId,
            Instant fromTime,
            Instant toTime,
            int splitInterval
    ) throws DaoException;

    Collection<Map<String, String>> getAccountingDataByPeriod(
            Instant fromTime,
            Instant toTime
    ) throws DaoException;

}
