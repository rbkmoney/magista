package com.rbkmoney.magista.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.PayoutEventCategory;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.rbkmoney.magista.domain.tables.InvoiceEventStat.INVOICE_EVENT_STAT;
import static com.rbkmoney.magista.domain.tables.PayoutEventStat.PAYOUT_EVENT_STAT;

/**
 * Created by vpankrashkin on 10.08.16.
 */
public class StatisticsDaoImpl extends AbstractDao implements StatisticsDao {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final int MAX_LIMIT = 1000;

    private final Cache<Map.Entry<Condition, String>, List<Map.Entry<LocalDateTime, Integer>>> statCache;

    public StatisticsDaoImpl(DataSource ds, long cacheMaxSize, long expireTime) {
        super(ds);
        statCache = Caffeine.newBuilder()
                .maximumSize(cacheMaxSize)
                .expireAfterWrite(expireTime, TimeUnit.MILLISECONDS)
                .build();
    }

    @Override
    public Collection<InvoiceEventStat> getInvoices(
            ConditionParameterSource invoiceParameterSource,
            ConditionParameterSource paymentParameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException {

        int limitValue = Math.min(limit.orElse(MAX_LIMIT), MAX_LIMIT);

        if (offset.isPresent()) {
            DateTimeRange dateTimeRange = getDateTimeRangeByOffset(
                    buildInvoiceCondition(invoiceParameterSource, paymentParameterSource, fromTime, toTime),
                    fromTime,
                    toTime,
                    INVOICE_EVENT_STAT.INVOICE_CREATED_AT,
                    offset.get(),
                    limitValue
            );

            fromTime = Optional.ofNullable(dateTimeRange.getFromTime());
            toTime = Optional.ofNullable(dateTimeRange.getToTime());
            offset = Optional.ofNullable(dateTimeRange.getOffset());
        }

        Query query = getDslContext().select(
                INVOICE_EVENT_STAT.PARTY_ID,
                INVOICE_EVENT_STAT.PARTY_SHOP_ID,
                INVOICE_EVENT_STAT.INVOICE_ID,
                INVOICE_EVENT_STAT.INVOICE_CREATED_AT,
                INVOICE_EVENT_STAT.INVOICE_STATUS,
                INVOICE_EVENT_STAT.INVOICE_STATUS_DETAILS,
                INVOICE_EVENT_STAT.INVOICE_PRODUCT,
                INVOICE_EVENT_STAT.INVOICE_DESCRIPTION,
                INVOICE_EVENT_STAT.INVOICE_DUE,
                INVOICE_EVENT_STAT.INVOICE_AMOUNT,
                INVOICE_EVENT_STAT.INVOICE_CURRENCY_CODE,
                INVOICE_EVENT_STAT.INVOICE_CART,
                INVOICE_EVENT_STAT.INVOICE_CONTEXT_TYPE,
                INVOICE_EVENT_STAT.INVOICE_CONTEXT
        ).from(INVOICE_EVENT_STAT)
                .where(buildInvoiceCondition(invoiceParameterSource, paymentParameterSource, fromTime, toTime))
                .orderBy(INVOICE_EVENT_STAT.INVOICE_CREATED_AT.desc())
                .limit(limitValue)
                .offset(offset.orElse(0));
        return fetch(query, InvoiceEventDaoImpl.ROW_MAPPER);
    }

    @Override
    public int getInvoicesCount(
            ConditionParameterSource invoiceParameterSource,
            ConditionParameterSource paymentParameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) throws DaoException {
        Query query = getDslContext().select(DSL.count()).from(INVOICE_EVENT_STAT)
                .where(buildInvoiceCondition(invoiceParameterSource, paymentParameterSource, fromTime, toTime));
        return fetchOne(query, Integer.class);
    }

    @Override
    public Collection<InvoiceEventStat> getPayments(
            ConditionParameterSource parameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException {

        int limitValue = Math.min(limit.orElse(MAX_LIMIT), MAX_LIMIT);

        if (offset.isPresent()) {
            DateTimeRange dateTimeRange = getDateTimeRangeByOffset(
                    buildPaymentCondition(parameterSource, fromTime, toTime),
                    fromTime,
                    toTime,
                    INVOICE_EVENT_STAT.PAYMENT_CREATED_AT,
                    offset.get(),
                    limitValue
            );
            fromTime = Optional.ofNullable(dateTimeRange.getFromTime());
            toTime = Optional.ofNullable(dateTimeRange.getToTime());
            offset = Optional.ofNullable(dateTimeRange.getOffset());
        }

        Query query = getDslContext().select(
                INVOICE_EVENT_STAT.PAYMENT_ID,
                INVOICE_EVENT_STAT.INVOICE_ID,
                INVOICE_EVENT_STAT.PARTY_ID,
                INVOICE_EVENT_STAT.PARTY_SHOP_ID,
                INVOICE_EVENT_STAT.PAYMENT_CREATED_AT,
                INVOICE_EVENT_STAT.PAYMENT_STATUS,
                INVOICE_EVENT_STAT.PAYMENT_FAILURE_CLASS,
                INVOICE_EVENT_STAT.PAYMENT_EXTERNAL_FAILURE_CODE,
                INVOICE_EVENT_STAT.PAYMENT_EXTERNAL_FAILURE_DESCRIPTION,
                INVOICE_EVENT_STAT.PAYMENT_AMOUNT,
                INVOICE_EVENT_STAT.PAYMENT_FEE,
                INVOICE_EVENT_STAT.PAYMENT_CURRENCY_CODE,
                INVOICE_EVENT_STAT.PAYMENT_TOOL,
                INVOICE_EVENT_STAT.PAYMENT_TOKEN,
                INVOICE_EVENT_STAT.PAYMENT_SYSTEM,
                INVOICE_EVENT_STAT.PAYMENT_BIN,
                INVOICE_EVENT_STAT.PAYMENT_MASKED_PAN,
                INVOICE_EVENT_STAT.PAYMENT_TERMINAL_PROVIDER,
                INVOICE_EVENT_STAT.PAYMENT_IP,
                INVOICE_EVENT_STAT.PAYMENT_FINGERPRINT,
                INVOICE_EVENT_STAT.PAYMENT_PHONE_NUMBER,
                INVOICE_EVENT_STAT.PAYMENT_EMAIL,
                INVOICE_EVENT_STAT.PAYMENT_SESSION_ID,
                INVOICE_EVENT_STAT.PAYMENT_CUSTOMER_ID,
                INVOICE_EVENT_STAT.PAYMENT_FLOW,
                INVOICE_EVENT_STAT.PAYMENT_HOLD_ON_EXPIRATION,
                INVOICE_EVENT_STAT.PAYMENT_HOLD_UNTIL,
                INVOICE_EVENT_STAT.PAYMENT_COUNTRY_ID,
                INVOICE_EVENT_STAT.PAYMENT_CITY_ID,
                INVOICE_EVENT_STAT.PAYMENT_CONTEXT_TYPE,
                INVOICE_EVENT_STAT.PAYMENT_CONTEXT
        ).from(INVOICE_EVENT_STAT)
                .where(buildPaymentCondition(parameterSource, fromTime, toTime))
                .orderBy(INVOICE_EVENT_STAT.PAYMENT_CREATED_AT.desc())
                .limit(limitValue)
                .offset(offset.orElse(0));
        return fetch(query, InvoiceEventDaoImpl.ROW_MAPPER);
    }

    @Override
    public Integer getPaymentsCount(
            ConditionParameterSource parameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) throws DaoException {
        Query query = getDslContext().select(DSL.count()).from(INVOICE_EVENT_STAT)
                .where(buildPaymentCondition(parameterSource, fromTime, toTime));
        return fetchOne(query, Integer.class);
    }

    @Override
    public Collection<PayoutEventStat> getPayouts(
            ConditionParameterSource parameterSource,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException {
        Query query = buildPayoutSelectConditionStepQuery(parameterSource)
                .orderBy(PAYOUT_EVENT_STAT.PAYOUT_CREATED_AT.desc())
                .limit(Math.min(limit.orElse(MAX_LIMIT), MAX_LIMIT))
                .offset(offset.orElse(0));

        return fetch(query, PayoutEventDaoImpl.ROW_MAPPER);
    }

    @Override
    public Integer getPayoutsCount(ConditionParameterSource parameterSource) throws DaoException {
        Query query = buildPayoutSelectConditionStepQuery(parameterSource, DSL.count());
        return fetchOne(query, Integer.class);
    }

    @Override
    public Collection<Map<String, String>> getPaymentsTurnoverStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT payment_currency_code AS currency_symbolic_code, SUM(payment_amount - payment_fee) AS amount_with_fee, SUM(payment_amount) AS amount_without_fee, trunc(EXTRACT(EPOCH FROM (payment_created_at - (:from_time::TIMESTAMP))) / EXTRACT(EPOCH FROM INTERVAL '" + splitInterval + "  sec')) AS sp_val FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT'::mst.invoice_event_category and party_shop_id = :shop_id AND party_id = :merchant_id AND payment_created_at >= :from_time AND payment_created_at < :to_time AND payment_status = :succeeded_status::mst.invoice_payment_status GROUP BY sp_val, payment_currency_code ORDER BY sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        return fetch(sql, params, (rs, i) -> {
            Map<String, String> map = new HashMap<>();
            map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
            map.put("currency_symbolic_code", rs.getString("currency_symbolic_code"));
            map.put("amount_with_fee", rs.getString("amount_with_fee"));
            map.put("amount_without_fee", rs.getString("amount_without_fee"));
            return map;
        });
    }

    @Override
    public Collection<Map<String, String>> getPaymentsGeoStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT payment_city_id as city_id, payment_country_id as country_id, payment_currency_code as currency_symbolic_code, SUM(payment_amount - payment_fee) as amount_with_fee, SUM(payment_amount) as amount_without_fee, trunc(EXTRACT(epoch FROM (payment_created_at - (:from_time::timestamp))) / EXTRACT(epoch FROM INTERVAL '" + splitInterval + "  sec')) AS sp_val FROM mst.invoice_event_stat where event_category = 'PAYMENT'::mst.invoice_event_category and payment_status = :succeeded_status::mst.invoice_payment_status and party_shop_id = :shop_id AND party_id = :merchant_id and payment_city_id notnull and payment_country_id notnull and payment_created_at >= :from_time AND payment_created_at < :to_time  group by sp_val, payment_city_id, payment_country_id, payment_currency_code order by sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        return fetch(sql, params, (rs, i) -> {
            Map<String, String> map = new HashMap<>();
            map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
            map.put("city_id", rs.getString("city_id"));
            map.put("country_id", rs.getString("country_id"));
            map.put("currency_symbolic_code", rs.getString("currency_symbolic_code"));
            map.put("amount_with_fee", rs.getString("amount_with_fee"));
            map.put("amount_without_fee", rs.getString("amount_without_fee"));
            return map;
        });
    }

    @Override
    public Collection<Map<String, String>> getPaymentsConversionStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT t.*, t.successful_count::FLOAT / greatest(t.total_count, 1) AS conversion FROM (SELECT SUM(CASE WHEN (payment_status = :succeeded_status::mst.invoice_payment_status OR payment_status = :failed_status::mst.invoice_payment_status) THEN 1 ELSE 0 END) AS total_count, SUM(CASE WHEN event_category = 'PAYMENT'::mst.invoice_event_category and payment_status = :succeeded_status::mst.invoice_payment_status THEN 1 ELSE 0 END) AS successful_count, trunc(EXTRACT(EPOCH FROM (payment_created_at - (:from_time::TIMESTAMP))) / EXTRACT(EPOCH FROM INTERVAL '" + splitInterval + "  sec')) AS sp_val FROM mst.invoice_event_stat WHERE party_shop_id = :shop_id AND party_id = :merchant_id AND payment_created_at >= :from_time AND payment_created_at < :to_time GROUP BY sp_val ORDER BY sp_val) AS t";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        params.addValue("failed_status", InvoicePaymentStatus._Fields.FAILED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        return fetch(sql, params, (rs, i) -> {
            Map<String, String> map = new HashMap<>();
            map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
            map.put("conversion", rs.getString("conversion"));
            map.put("total_count", rs.getString("total_count"));
            map.put("successful_count", rs.getString("successful_count"));
            return map;
        });
    }

    @Override
    public Collection<Map<String, String>> getCustomersRateStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT count(payment_fingerprint) AS unic_count, trunc(EXTRACT(EPOCH FROM (payment_created_at - (:from_time::TIMESTAMP))) / EXTRACT(EPOCH FROM INTERVAL '" + splitInterval + " sec')) AS sp_val FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT'::mst.invoice_event_category and party_shop_id = :shop_id AND party_id = :merchant_id AND payment_created_at >= :from_time AND payment_created_at < :to_time GROUP BY sp_val ORDER BY sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        log.trace("SQL: {}, Params: {}", sql, params);
        return fetch(sql, params, (rs, i) -> {
            Map<String, String> map = new HashMap<>();
            map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
            map.put("unic_count", rs.getString("unic_count"));
            return map;
        });
    }

    @Override
    public Collection<Map<String, String>> getPaymentsCardTypesStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT count(payment_system) AS total_count, payment_system AS payment_system, SUM(payment_amount - payment_fee) AS amount_with_fee, SUM(payment_amount) AS amount_without_fee, trunc(EXTRACT(EPOCH FROM (payment_created_at - (:from_time::TIMESTAMP))) / EXTRACT(EPOCH FROM INTERVAL '" + splitInterval + "  sec')) AS sp_val FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT'::mst.invoice_event_category and payment_status = :succeeded_status::mst.invoice_payment_status AND payment_system NOTNULL AND party_shop_id = :shop_id AND party_id = :merchant_id AND payment_created_at >= :from_time AND payment_created_at < :to_time GROUP BY sp_val, payment_system ORDER BY sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        return fetch(sql, params, (rs, i) -> {
            Map<String, String> map = new HashMap<>();
            map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
            map.put("total_count", rs.getString("total_count"));
            map.put("payment_system", rs.getString("payment_system"));
            map.put("amount_with_fee", rs.getString("amount_with_fee"));
            map.put("amount_without_fee", rs.getString("amount_without_fee"));
            return map;
        });
    }

    @Override
    public Collection<Map<String, String>> getAccountingDataByPeriod(Instant fromTime, Instant toTime, Optional<List<Integer>> withoutShopCategoryIds) throws DaoException {
        //TODO rewrite this request to jooq
        String sql = "SELECT part1.party_id as merchant_id, part1.party_shop_id as shop_id, part1.payment_currency_code as currency_code, (coalesce(funds_to_be_paid_prev_periods,0) - coalesce(funds_paid_out_prev_periods,0) - coalesce(funds_refunded_prev_periods,0)) as opening_balance, coalesce(funds_acquired_period,0) as funds_acquired, coalesce(fee_charged_period,0) as fee_charged, coalesce(funds_paid_out_period,0) as funds_paid_out, coalesce(funds_refunded_period,0) as funds_refunded, (coalesce(funds_to_be_paid_prev_periods,0) - coalesce(funds_paid_out_prev_periods,0) - coalesce(funds_refunded_prev_periods,0) + coalesce(funds_acquired_period,0) - coalesce(fee_charged_period,0) - coalesce(funds_paid_out_period,0) - coalesce(funds_refunded_period,0)) as closing_balance FROM (SELECT party_id, party_shop_id, payment_currency_code, sum(payment_amount) as funds_acquired_period, sum(payment_fee) as fee_charged_period FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT'::mst.invoice_event_category AND payment_status IN (:succeeded_status::mst.invoice_payment_status, :refunded_status::mst.invoice_payment_status) AND party_shop_category_id NOT IN (:shop_categories) AND event_created_at >= :from_time AND event_created_at < :to_time GROUP BY party_shop_id, payment_currency_code, party_id) part1 LEFT JOIN (SELECT party_id, party_shop_id, sum(payment_amount) as funds_refunded_period FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT'::mst.invoice_event_category AND payment_status = :refunded_status::mst.invoice_payment_status AND event_created_at >= :from_time AND event_created_at < :to_time GROUP BY party_shop_id, payment_currency_code, party_id) part2 ON part1.party_id = part2.party_id AND part1.party_shop_id = part2.party_shop_id LEFT JOIN (SELECT party_id, party_shop_id, sum(payment_amount - payment_fee) as funds_to_be_paid_prev_periods FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT'::mst.invoice_event_category AND payment_status IN (:succeeded_status::mst.invoice_payment_status, :refunded_status::mst.invoice_payment_status) AND event_created_at < :from_time GROUP BY party_shop_id, party_id) part3 ON part1.party_id = part3.party_id AND part1.party_shop_id = part3.party_shop_id LEFT JOIN (SELECT party_id, party_shop_id, sum(payment_amount) as funds_refunded_prev_periods FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT'::mst.invoice_event_category AND payment_status = :refunded_status::mst.invoice_payment_status AND event_created_at < :from_time GROUP BY party_shop_id, party_id) part4 ON part1.party_id = part4.party_id AND part1.party_shop_id = part4.party_shop_id LEFT JOIN (SELECT party_id, party_shop_id, sum(payout_amount) as funds_paid_out_period FROM mst.payout_event_stat WHERE payout_status = :payout_confirmed_status::mst.payout_status AND payout_created_at >=  :from_time AND payout_created_at < :to_time GROUP BY party_shop_id, party_id) part5 ON part1.party_id = part5.party_id AND part1.party_shop_id = part5.party_shop_id LEFT JOIN (SELECT party_id, party_shop_id, sum(payout_amount) as funds_paid_out_prev_periods FROM mst.payout_event_stat WHERE payout_status = :payout_confirmed_status::mst.payout_status AND payout_created_at < :from_time GROUP BY party_shop_id, party_id) part6 ON part1.party_id = part6.party_id AND part1.party_shop_id = part6.party_shop_id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from_time", LocalDateTime.ofInstant(fromTime, ZoneOffset.UTC), Types.OTHER)
                .addValue("to_time", LocalDateTime.ofInstant(toTime, ZoneOffset.UTC), Types.OTHER)
                .addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName())
                .addValue("refunded_status", InvoicePaymentStatus._Fields.REFUNDED.getFieldName())
                .addValue("payout_confirmed_status", com.rbkmoney.damsel.payout_processing.PayoutStatus._Fields.CONFIRMED.getFieldName())
                .addValue("shop_categories", withoutShopCategoryIds.orElse(Arrays.asList(-1)));
        log.trace("SQL: {}, Params: {}", sql, params.getValues());

        return fetch(sql, params, (rs, i) -> {
            Map<String, String> map = new HashMap<>();
            map.put("merchant_id", rs.getString("merchant_id"));
            map.put("shop_id", rs.getString("shop_id"));
            map.put("currency_code", rs.getString("currency_code"));
            map.put("opening_balance", rs.getString("opening_balance"));
            map.put("funds_acquired", rs.getString("funds_acquired"));
            map.put("fee_charged", rs.getString("fee_charged"));
            map.put("funds_paid_out", rs.getString("funds_paid_out"));
            map.put("funds_refunded", rs.getString("funds_refunded"));
            map.put("closing_balance", rs.getString("closing_balance"));
            return map;
        });
    }

    private MapSqlParameterSource createParamsMap(String merchantId, String shopId, Instant fromTime, Instant toTime, Integer splitInterval) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("merchant_id", merchantId);
        params.addValue("shop_id", shopId);
        params.addValue("from_time", LocalDateTime.ofInstant(fromTime, ZoneId.of("UTC")), Types.OTHER);
        params.addValue("to_time", LocalDateTime.ofInstant(toTime, ZoneId.of("UTC")), Types.OTHER);
        params.addValue("split_interval", splitInterval);
        return params;
    }

    private Condition buildPaymentCondition(
            ConditionParameterSource paymentParameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) {
        Condition condition = appendDateTimeRange(
                INVOICE_EVENT_STAT.EVENT_CATEGORY.eq(InvoiceEventCategory.PAYMENT),
                INVOICE_EVENT_STAT.PAYMENT_CREATED_AT,
                fromTime,
                toTime);

        return appendConditions(condition, Operator.AND, paymentParameterSource);
    }

    private Condition buildInvoiceCondition(
            ConditionParameterSource invoiceParameterSource,
            ConditionParameterSource paymentParameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) {
        Condition condition = appendDateTimeRange(
                INVOICE_EVENT_STAT.EVENT_CATEGORY.eq(InvoiceEventCategory.INVOICE),
                INVOICE_EVENT_STAT.INVOICE_CREATED_AT,
                fromTime,
                toTime);

        if (!paymentParameterSource.getConditionFields().isEmpty()) {
            condition = condition.and(
                    INVOICE_EVENT_STAT.INVOICE_ID.in(
                            getDslContext().select(INVOICE_EVENT_STAT.INVOICE_ID)
                                    .from(INVOICE_EVENT_STAT)
                                    .where(buildPaymentCondition(paymentParameterSource, fromTime, toTime))
                    )
            );
        }
        return appendConditions(condition, Operator.AND, invoiceParameterSource);
    }

    private Condition appendDateTimeRange(Condition condition,
                                          Field<LocalDateTime> field,
                                          Optional<LocalDateTime> fromTime,
                                          Optional<LocalDateTime> toTime) {
        if (fromTime.isPresent()) {
            condition = condition.and(field.ge(fromTime.get()));
        }

        if (toTime.isPresent()) {
            condition = condition.and(field.lt(toTime.get()));
        }
        return condition;
    }

    private SelectConditionStep buildPayoutSelectConditionStepQuery(
            ConditionParameterSource paymentParameterSource,
            SelectField<?>... fields) {
        Condition condition = PAYOUT_EVENT_STAT.EVENT_CATEGORY.eq(PayoutEventCategory.PAYOUT);

        condition = appendConditions(condition, Operator.AND, paymentParameterSource);

        return getDslContext().select(fields).from(PAYOUT_EVENT_STAT)
                .where(condition);
    }

    private <T extends Record> DateTimeRange getDateTimeRangeByOffset(Condition condition,
                                                                      Optional<LocalDateTime> fromTime,
                                                                      Optional<LocalDateTime> toTime,
                                                                      TableField<T, LocalDateTime> dateTimeField,
                                                                      int offset,
                                                                      int limit) {
        List<Map.Entry<LocalDateTime, Integer>> dateRanges = getDateTimeRanges(condition,
                fromTime,
                toTime,
                dateTimeField);

        boolean offsetFound = false;
        int fromTimeBound = limit;
        DateTimeRange currentRange = new DateTimeRange(fromTime.orElse(null), toTime.orElse(null), offset);
        for (Map.Entry<LocalDateTime, Integer> dateRange : dateRanges) {
            int newOffset = currentRange.getOffset() - dateRange.getValue();
            if (!offsetFound && newOffset >= 0) {
                currentRange.setToTime(dateRange.getKey());
                currentRange.setOffset(newOffset);
            } else {
                offsetFound = true;
                if ((fromTimeBound + currentRange.getOffset()) - dateRange.getValue() > 0) {
                    fromTimeBound -= dateRange.getValue();
                } else {
                    currentRange.setFromTime(dateRange.getKey());
                    break;
                }

            }
        }
        return currentRange;
    }

    private <T extends Record> List<Map.Entry<LocalDateTime, Integer>> getDateTimeRanges(Condition condition,
                                                                                         Optional<LocalDateTime> fromTime,
                                                                                         Optional<LocalDateTime> toTime,
                                                                                         TableField<T, LocalDateTime> dateTimeField) {
        final Map.Entry key = new AbstractMap.SimpleEntry<>(condition, dateTimeField.getName());

        List<Map.Entry<LocalDateTime, Integer>> dateRanges = statCache.getIfPresent(key);
        if (dateRanges == null) {
            Field<LocalDateTime> spValField = DSL.field(
                    DSL.sql("date_trunc('" + DatePart.HOUR.toSQL() + "', " + dateTimeField.getName() + ")"),
                    LocalDateTime.class
            ).as("sp_val");
            Field countField = DSL.count().as("count");

            Query query = getDslContext().select(spValField, countField).from(INVOICE_EVENT_STAT)
                    .where(condition)
                    .groupBy(spValField)
                    .orderBy(spValField.desc());

            dateRanges = fetch(query,
                    (resultSet, i) -> new AbstractMap.SimpleEntry<>(
                            resultSet.getObject(spValField.getName(), LocalDateTime.class),
                            resultSet.getInt(countField.getName())
                    )
            );
            if (checkBounds(fromTime, toTime, dateTimeField)) {
                statCache.put(key, dateRanges);
            }
        }

        return dateRanges;
    }

    private <T extends Record> boolean checkBounds(Optional<LocalDateTime> fromTime,
                                                   Optional<LocalDateTime> toTime,
                                                   TableField<T, LocalDateTime> dateTimeField) {
        return fromTime.isPresent()
                && toTime.isPresent()
                && fetchOne(getDslContext().select(
                DSL.field(
                        DSL.min(dateTimeField).le(fromTime.get())
                                .and(DSL.max(dateTimeField).ge(toTime.get())))
                ).from(dateTimeField.getTable()),
                Boolean.class);
    }

    public static class DateTimeRange {

        private LocalDateTime fromTime;

        private LocalDateTime toTime;

        private int offset;

        public DateTimeRange(LocalDateTime fromTime, LocalDateTime toTime, int offset) {
            this.fromTime = fromTime;
            this.toTime = toTime;
            this.offset = offset;
        }

        public LocalDateTime getFromTime() {
            return fromTime;
        }

        public void setFromTime(LocalDateTime fromTime) {
            this.fromTime = fromTime;
        }

        public LocalDateTime getToTime() {
            return toTime;
        }

        public void setToTime(LocalDateTime toTime) {
            this.toTime = toTime;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        @Override
        public String toString() {
            return "DateTimeRange{" +
                    "fromTime=" + fromTime +
                    ", toTime=" + toTime +
                    ", offset=" + offset +
                    '}';
        }
    }

}
