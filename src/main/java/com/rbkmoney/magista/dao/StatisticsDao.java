package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.exception.DaoException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * Created by vpankrashkin on 10.08.16.
 */
public interface StatisticsDao {

    Collection<Map<String, String>> getPaymentsTurnoverStat(
            String merchantId,
            String shopId,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            int splitInterval
    ) throws DaoException;

    Collection<Map<String, String>> getPaymentsGeoStat(
            String merchantId,
            String shopId,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            int splitInterval
    ) throws DaoException;

    Collection<Map<String, String>> getPaymentsConversionStat(
            String merchantId,
            String shopId,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            int splitInterval
    ) throws DaoException;

    Collection<Map<String, String>> getCustomersRateStat(
            String merchantId,
            String shopId,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            int splitInterval
    ) throws DaoException;

    Collection<Map<String, String>> getPaymentsCardTypesStat(
            String merchantId,
            String shopId,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            int splitInterval
    ) throws DaoException;

}
