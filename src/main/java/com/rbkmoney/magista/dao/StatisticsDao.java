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
            ConditionParameterSource invoiceParameterSource,
            ConditionParameterSource paymentParameterSource,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException;

    int getInvoicesCount(
            ConditionParameterSource invoiceParameterSource,
            ConditionParameterSource paymentParameterSource
    ) throws DaoException;

    Collection<InvoiceEventStat> getPayments(
            ConditionParameterSource parameterSource,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException;

    Integer getPaymentsCount(ConditionParameterSource parameterSource) throws DaoException;

    Collection<PayoutEventStat> getPayouts(
            ConditionParameterSource parameterSource,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException;

    Integer getPayoutsCount(ConditionParameterSource parameterSource) throws DaoException;

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
            Instant toTime,
            Optional<List<Integer>> withoutShopCategoryIds
    ) throws DaoException;

}
