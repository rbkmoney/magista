package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.merch_stat.StatPayment;
import com.rbkmoney.magista.exception.DaoException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface ReportDao {

    Map<String, String> getPaymentAccountingData(
            String merchantId,
            String shopId,
            String currencyCode,
            Optional<LocalDateTime> fromTime,
            LocalDateTime toTime
    ) throws DaoException;

    Map<String, String> getRefundAccountingData(
            String merchantId,
            String shopId,
            String currencyCode,
            Optional<LocalDateTime> fromTime,
            LocalDateTime toTime
    ) throws DaoException;

    Map<String, String> getAdjustmentAccountingData(
            String merchantId,
            String shopId,
            String currencyCode,
            Optional<LocalDateTime> fromTime,
            LocalDateTime toTime
    ) throws DaoException;

    Map<String, String> getPayoutAccountingData(
            String merchantId,
            String shopId,
            String currencyCode,
            Optional<LocalDateTime> fromTime,
            LocalDateTime toTime
    ) throws DaoException;

    Collection<Map.Entry<Long, StatPayment>> getPaymentsForReport(
            String partyId,
            String shopId,
            Optional<String> invoiceId,
            Optional<String> paymentId,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<LocalDateTime> whereTime,
            int limit
    ) throws DaoException;

}
