package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.rbkmoney.magista.domain.tables.InvoiceEventStat.INVOICE_EVENT_STAT;

/**
 * Created by vpankrashkin on 10.08.16.
 */
public class StatisticsDaoImpl extends AbstractDao implements StatisticsDao {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final int MAX_LIMIT = 1000;

    public StatisticsDaoImpl(DataSource ds) {
        super(ds);
    }

    @Override
    public Collection<InvoiceEventStat> getInvoices(
            Optional<String> merchantId,
            Optional<String> shopId,
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
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset) throws DaoException {

        ConditionParameterSource invoiceSource = buildOptionalInvoiceParameters(
                merchantId,
                shopId,
                invoiceId,
                invoiceStatus,
                invoiceAmount,
                fromTime,
                toTime
        );

        ConditionParameterSource paymentSource = buildOptionalPaymentParameters(
                merchantId,
                shopId,
                invoiceId,
                paymentId,
                paymentStatus,
                paymentFlow,
                paymentMethod,
                paymentTerminalProvider,
                paymentEmail,
                paymentIp,
                paymentFingerprint,
                paymentPanMask,
                paymentAmount,
                Optional.empty(),
                Optional.empty()
        );

        Query query = buildInvoiceSelectConditionStepQuery(invoiceSource, paymentSource)
                .orderBy(INVOICE_EVENT_STAT.INVOICE_CREATED_AT.desc())
                .limit(Math.min(limit.orElse(MAX_LIMIT), MAX_LIMIT))
                .offset(offset.orElse(0));
        return fetch(query, InvoiceEventDaoImpl.getRowMapper());
    }

    @Override
    public int getInvoicesCount(
            Optional<String> merchantId,
            Optional<String> shopId,
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
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset) throws DaoException {

        ConditionParameterSource invoiceSource = buildOptionalInvoiceParameters(
                merchantId,
                shopId,
                invoiceId,
                invoiceStatus,
                invoiceAmount,
                fromTime,
                toTime
        );

        ConditionParameterSource paymentSource = buildOptionalPaymentParameters(
                merchantId,
                shopId,
                invoiceId,
                paymentId,
                paymentStatus,
                paymentFlow,
                paymentMethod,
                paymentTerminalProvider,
                paymentEmail,
                paymentIp,
                paymentFingerprint,
                paymentPanMask,
                paymentAmount,
                Optional.empty(),
                Optional.empty()
        );

        Query query = buildInvoiceSelectConditionStepQuery(invoiceSource, paymentSource, DSL.count());
        return fetchOne(query, Integer.class);
    }

    @Override
    public Collection<InvoiceEventStat> getPayments(
            Optional<String> merchantId,
            Optional<String> shopId,
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
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset) throws DaoException {

        ConditionParameterSource paymentSource = buildOptionalPaymentParameters(
                merchantId,
                shopId,
                invoiceId,
                paymentId,
                paymentStatus,
                paymentFlow,
                paymentMethod,
                paymentTerminalProvider,
                paymentEmail,
                paymentIp,
                paymentFingerprint,
                paymentPanMask,
                paymentAmount,
                fromTime,
                toTime
        );

        Query query = buildPaymentSelectConditionStepQuery(paymentSource)
                .orderBy(INVOICE_EVENT_STAT.PAYMENT_CREATED_AT.desc())
                .limit(Math.min(limit.orElse(MAX_LIMIT), MAX_LIMIT))
                .offset(offset.orElse(0));
        return fetch(query, InvoiceEventDaoImpl.getRowMapper());
    }

    @Override
    public Integer getPaymentsCount(
            Optional<String> merchantId,
            Optional<String> shopId,
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
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset) throws DaoException {

        ConditionParameterSource parameterSource = buildOptionalPaymentParameters(
                merchantId,
                shopId,
                invoiceId,
                paymentId,
                paymentStatus,
                paymentFlow,
                paymentMethod,
                paymentTerminalProvider,
                paymentEmail,
                paymentIp,
                paymentFingerprint,
                paymentPanMask,
                paymentAmount,
                fromTime,
                toTime
        );

        Query query = buildPaymentSelectConditionStepQuery(parameterSource, DSL.count());
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
        String sql = "SELECT payment_city_id as city_id, payment_country_id as country_id, payment_currency_code as currency_symbolic_code, SUM(payment_amount - payment_fee) as amount_with_fee, SUM(payment_amount) as amount_without_fee, trunc(EXTRACT(epoch FROM (payment_created_at - (:from_time::timestamp))) / EXTRACT(epoch FROM INTERVAL '" + splitInterval + "  sec')) AS sp_val FROM mst.invoice_event_stat where event_category = 'PAYMENT'::mst.invoice_event_category and payment_status = :succeeded_status::mst.invoice_payment_status and party_shop_id = :shop_id AND party_id = :merchant_id and payment_created_at >= :from_time AND payment_created_at < :to_time  group by sp_val, payment_city_id, payment_country_id, payment_currency_code order by sp_val";
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
    public Collection<Map<String, String>> getAccountingDataByPeriod(Instant fromTime, Instant toTime) throws DaoException {
        String sql = "SELECT part1.party_id as merchant_id, part1.party_shop_id as shop_id, part1.payment_currency_code as currency_code, funds_acquired, fee_charged, coalesce(opening_balance, 0) AS opening_balance, (coalesce(opening_balance, 0) + funds_acquired - fee_charged) AS closing_balance FROM (SELECT party_id, party_shop_id, payment_currency_code, sum(payment_amount) AS funds_acquired, sum(payment_fee) AS fee_charged FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT'::mst.invoice_event_category and payment_status = :succeeded_status::mst.invoice_payment_status AND payment_created_at >= :from_time AND payment_created_at < :to_time GROUP BY party_shop_id, payment_currency_code, party_id) part1 LEFT JOIN (SELECT party_id, party_shop_id, sum(payment_amount - payment_fee) AS opening_balance FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT'::mst.invoice_event_category and payment_status = :succeeded_status::mst.invoice_payment_status AND payment_created_at < :from_time GROUP BY party_shop_id, party_id) part2 ON part1.party_id = part2.party_id AND part1.party_shop_id = part2.party_shop_id ORDER BY merchant_id, shop_id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from_time", LocalDateTime.ofInstant(fromTime, ZoneOffset.UTC), Types.OTHER)
                .addValue("to_time", LocalDateTime.ofInstant(toTime, ZoneOffset.UTC), Types.OTHER)
                .addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params.getValues());

        return fetch(sql, params, (rs, i) -> {
            Map<String, String> map = new HashMap<>();
            map.put("merchant_id", rs.getString("merchant_id"));
            map.put("shop_id", rs.getString("shop_id"));
            map.put("currency_code", rs.getString("currency_code"));
            map.put("opening_balance", rs.getString("opening_balance"));
            map.put("funds_acquired", rs.getString("funds_acquired"));
            map.put("fee_charged", rs.getString("fee_charged"));
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

    private SelectConditionStep buildPaymentSelectConditionStepQuery(
            ConditionParameterSource paymentParameterSource,
            SelectField<?>... fields) {
        Condition condition = INVOICE_EVENT_STAT.EVENT_CATEGORY.eq(InvoiceEventCategory.PAYMENT);

        condition = appendConditions(condition, Operator.AND, paymentParameterSource);

        return getDslContext().select(fields).from(INVOICE_EVENT_STAT)
                .where(condition);
    }

    private SelectConditionStep buildInvoiceSelectConditionStepQuery(ConditionParameterSource invoiceSource,
                                                                     ConditionParameterSource paymentSource,
                                                                     SelectField<?>... fields) {
        Condition condition = INVOICE_EVENT_STAT.EVENT_CATEGORY.eq(InvoiceEventCategory.INVOICE);

        long paymentParamsCount = paymentSource
                .getConditionFields()
                .stream()
                .filter(
                        t -> t.getField() != INVOICE_EVENT_STAT.INVOICE_ID
                                && t.getField() != INVOICE_EVENT_STAT.PARTY_ID
                                && t.getField() != INVOICE_EVENT_STAT.PARTY_SHOP_ID
                )
                .count();

        if (paymentParamsCount > 0) {
            condition = condition.and(INVOICE_EVENT_STAT.INVOICE_ID
                    .in(buildPaymentSelectConditionStepQuery(
                            paymentSource,
                            INVOICE_EVENT_STAT.INVOICE_ID)));
        }

        condition = appendConditions(condition, Operator.AND, invoiceSource);


        return getDslContext().select(fields).from(INVOICE_EVENT_STAT)
                .where(condition);
    }

    private ConditionParameterSource buildOptionalInvoiceParameters(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<String> invoiceId,
            Optional<String> invoiceStatus,
            Optional<Long> invoiceAmount,
            Optional<Instant> fromTime,
            Optional<Instant> toTime
    ) {
        return new ConditionParameterSource()
                .addValue(INVOICE_EVENT_STAT.PARTY_ID, merchantId.orElse(null), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.PARTY_SHOP_ID, shopId.orElse(null), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.INVOICE_ID, invoiceId.orElse(null), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.INVOICE_STATUS,
                        invoiceStatus.isPresent() ? InvoiceStatus.valueOf(invoiceStatus.get()) : null,
                        Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.INVOICE_AMOUNT, invoiceAmount.orElse(null), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.INVOICE_CREATED_AT,
                        fromTime.isPresent() ? LocalDateTime.ofInstant(fromTime.get(), ZoneOffset.UTC) : null,
                        Comparator.GREATER_OR_EQUAL)
                .addValue(INVOICE_EVENT_STAT.INVOICE_CREATED_AT,
                        toTime.isPresent() ? LocalDateTime.ofInstant(toTime.get(), ZoneOffset.UTC) : null,
                        Comparator.LESS);
    }

    private ConditionParameterSource buildOptionalPaymentParameters(
            Optional<String> merchantId,
            Optional<String> shopId,
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
            Optional<Instant> fromTime,
            Optional<Instant> toTime
    ) {
        return new ConditionParameterSource()
                .addValue(INVOICE_EVENT_STAT.PARTY_ID, merchantId.orElse(null), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.PARTY_SHOP_ID, shopId.orElse(null), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.INVOICE_ID, invoiceId.orElse(null), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_ID, paymentId.orElse(null), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_STATUS,
                        paymentStatus.isPresent() ?
                                com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.valueOf(paymentStatus.get()) : null,
                        Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_FLOW, paymentFlow.orElse(null), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_TOOL, paymentMethod.orElse(null), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_TERMINAL_PROVIDER, paymentTerminalProvider.orElse(null), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_AMOUNT, paymentAmount.orElse(null), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_EMAIL, paymentEmail.orElse(null), Comparator.LIKE)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_IP, paymentIp.orElse(null), Comparator.LIKE)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_FINGERPRINT, paymentFingerprint.orElse(null), Comparator.LIKE)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_MASKED_PAN, paymentPanMask.orElse(null), Comparator.LIKE)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_MASKED_PAN, paymentPanMask.orElse(null), Comparator.LIKE)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_CREATED_AT,
                        fromTime.isPresent() ? LocalDateTime.ofInstant(fromTime.get(), ZoneOffset.UTC) : null,
                        Comparator.GREATER_OR_EQUAL)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_CREATED_AT,
                        toTime.isPresent() ? LocalDateTime.ofInstant(toTime.get(), ZoneOffset.UTC) : null,
                        Comparator.LESS);
    }

}
