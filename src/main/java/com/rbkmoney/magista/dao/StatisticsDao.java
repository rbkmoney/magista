package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.domain.tables.pojos.Refund;
import com.rbkmoney.magista.exception.DaoException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Created by vpankrashkin on 10.08.16.
 */
public interface StatisticsDao {
    Collection<InvoiceEventStat> getInvoices(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<String> contractId,
            ConditionParameterSource invoiceParameterSource,
            ConditionParameterSource paymentParameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException;

    int getInvoicesCount(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<String> contractId,
            ConditionParameterSource invoiceParameterSource,
            ConditionParameterSource paymentParameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) throws DaoException;

    Collection<InvoiceEventStat> getPayments(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<String> contractId,
            ConditionParameterSource parameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException;

    Integer getPaymentsCount(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<String> contractId,
            ConditionParameterSource parameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) throws DaoException;

    Collection<Refund> getRefunds(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<String> contractId,
            ConditionParameterSource parameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException;

    Integer getRefundsCount(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<String> contractId,
            ConditionParameterSource parameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) throws DaoException;

    Collection<PayoutEventStat> getPayouts(
            Optional<String> merchantId,
            Optional<String> shopId,
            ConditionParameterSource parameterSource,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException;

    Integer getPayoutsCount(
            Optional<String> merchantId,
            Optional<String> shopId,
            ConditionParameterSource parameterSource
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

    Map<String, String> getPaymentAccountingData(
            String merchantId,
            String contractId,
            String currencyCode,
            Optional<LocalDateTime> fromTime,
            LocalDateTime toTime
    ) throws DaoException;

    Map<String, String> getRefundAccountingData(
            String merchantId,
            String contractId,
            String currencyCode,
            Optional<LocalDateTime> fromTime,
            LocalDateTime toTime
    ) throws DaoException;

    Map<String, String> getAdjustmentAccountingData(
            String merchantId,
            String contractId,
            String currencyCode,
            Optional<LocalDateTime> fromTime,
            LocalDateTime toTime
    ) throws DaoException;

    Map<String, String> getPayoutAccountingData(
            String merchantId,
            String contractId,
            String currencyCode,
            Optional<LocalDateTime> fromTime,
            LocalDateTime toTime
    ) throws DaoException;

}
