package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
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

    public StatisticsDaoImpl(DataSource ds) {
        super(ds);
    }

    @Override
    public Collection<InvoiceEventStat> getInvoices(
            String merchantId,
            int shopId,
            Optional<String> invoiceId,
            Optional<String> paymentId,
            Optional<String> invoiceStatus,
            Optional<String> paymentStatus,
            Optional<Long> invoiceAmount,
            Optional<Long> paymentAmount,
            Optional<String> paymentEmail,
            Optional<String> paymentIp,
            Optional<String> paymentFingerprint,
            Optional<String> paymentPanMask,
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset) throws DaoException {

        ConditionParameterSource conditionParameterSource = buildOptionalConditionParameters(
                invoiceId,
                paymentId,
                invoiceStatus,
                paymentStatus,
                invoiceAmount,
                paymentAmount,
                paymentEmail,
                paymentIp,
                paymentFingerprint,
                paymentPanMask,
                fromTime,
                toTime
        );

        Query query = buildInvoiceSelectConditionStepQuery(merchantId, shopId, conditionParameterSource)
                .offset(offset.orElse(0));

        return fetch(query, InvoiceEventDaoImpl.getRowMapper());
    }

    @Override
    public int getInvoicesCount(
            String merchantId,
            int shopId,
            Optional<String> invoiceId,
            Optional<String> paymentId,
            Optional<String> invoiceStatus,
            Optional<String> paymentStatus,
            Optional<Long> invoiceAmount,
            Optional<Long> paymentAmount,
            Optional<String> paymentEmail,
            Optional<String> paymentIp,
            Optional<String> paymentFingerprint,
            Optional<String> paymentPanMask,
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset) throws DaoException {

        ConditionParameterSource conditionParameterSource = buildOptionalConditionParameters(
                invoiceId,
                paymentId,
                invoiceStatus,
                paymentStatus,
                invoiceAmount,
                paymentAmount,
                paymentEmail,
                paymentIp,
                paymentFingerprint,
                paymentPanMask,
                fromTime,
                toTime
        );

        Query query = buildInvoiceSelectConditionStepQuery(merchantId, shopId, conditionParameterSource, DSL.count());

        return fetchOne(query, Integer.class);
    }

    @Override
    public Collection<InvoiceEventStat> getPayments(
            String merchantId,
            int shopId,
            Optional<String> invoiceId,
            Optional<String> paymentId,
            Optional<String> paymentStatus,
            Optional<String> paymentEmail,
            Optional<String> paymentIp,
            Optional<String> paymentFingerprint,
            Optional<String> paymentPanMask,
            Optional<Long> paymentAmount,
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset) throws DaoException {

        ConditionParameterSource parameterSource = buildOptionalConditionParameters(
                invoiceId,
                paymentId,
                Optional.empty(),
                paymentStatus,
                Optional.empty(),
                paymentAmount,
                paymentEmail,
                paymentIp,
                paymentFingerprint,
                paymentPanMask,
                fromTime,
                toTime
        );

        Query query = buildPaymentSelectConditionStepQuery(merchantId, shopId, parameterSource)
                .offset(offset.orElse(0));

        return fetch(query, InvoiceEventDaoImpl.getRowMapper());
    }

    @Override
    public Integer getPaymentsCount(
            String merchantId,
            int shopId,
            Optional<String> invoiceId,
            Optional<String> paymentId,
            Optional<String> paymentStatus,
            Optional<String> paymentEmail,
            Optional<String> paymentIp,
            Optional<String> paymentFingerprint,
            Optional<String> paymentPanMask,
            Optional<Long> paymentAmount,
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset) throws DaoException {

        ConditionParameterSource parameterSource = buildOptionalConditionParameters(
                invoiceId,
                paymentId,
                Optional.empty(),
                paymentStatus,
                Optional.empty(),
                paymentAmount,
                paymentEmail,
                paymentIp,
                paymentFingerprint,
                paymentPanMask,
                fromTime,
                toTime
        );

        Query query = buildPaymentSelectConditionStepQuery(merchantId, shopId, parameterSource, DSL.count());

        return fetchOne(query, Integer.class);
    }

    private SelectConditionStep buildInvoiceSelectConditionStepQuery(
            String merchantId,
            int shopId,
            ConditionParameterSource parameterSource,
            SelectField<?>... fields) {
        Condition condition = INVOICE_EVENT_STAT.INVOICE_ID
                .in(buildPaymentSelectConditionStepQuery(merchantId,
                        shopId,
                        parameterSource,
                        INVOICE_EVENT_STAT.INVOICE_ID));
        return getDslContext().select(fields).from(INVOICE_EVENT_STAT)
                .where(condition);
    }

    private SelectConditionStep buildPaymentSelectConditionStepQuery(String merchantId,
                                                                     int shopId,
                                                                     ConditionParameterSource parameterSource,
                                                                     SelectField<?>... fields) {
        Condition condition = INVOICE_EVENT_STAT.PARTY_ID.eq(merchantId)
                .and(INVOICE_EVENT_STAT.PARTY_SHOP_ID.eq(shopId));

        condition = appendConditions(condition, Operator.AND, parameterSource);

        return getDslContext().select(fields).from(INVOICE_EVENT_STAT)
                .where(condition);
    }

    private ConditionParameterSource buildOptionalConditionParameters(
            Optional<String> invoiceId,
            Optional<String> paymentId,
            Optional<String> invoiceStatus,
            Optional<String> paymentStatus,
            Optional<Long> invoiceAmount,
            Optional<Long> paymentAmount,
            Optional<String> paymentEmail,
            Optional<String> paymentIp,
            Optional<String> paymentFingerprint,
            Optional<String> paymentPanMask,
            Optional<Instant> fromTime,
            Optional<Instant> toTime
    ) {
        return new ConditionParameterSource()
                .addValue(INVOICE_EVENT_STAT.INVOICE_ID, invoiceId.get(), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_ID, paymentId.get(), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.INVOICE_STATUS,
                        invoiceStatus.isPresent() ? InvoiceStatus.valueOf(invoiceStatus.get()) : null,
                        Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_STATUS,
                        paymentStatus.isPresent() ?
                                com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.valueOf(paymentStatus.get()) : null,
                        Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.INVOICE_AMOUNT, invoiceAmount.get(), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_AMOUNT, paymentAmount.get(), Comparator.EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_EMAIL, paymentEmail.get(), Comparator.LIKE)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_IP, paymentIp.get(), Comparator.LIKE)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_FINGERPRINT, paymentFingerprint.get(), Comparator.LIKE)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_MASKED_PAN, paymentPanMask.get(), Comparator.LIKE)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_MASKED_PAN, paymentPanMask.get(), Comparator.LIKE)
                .addValue(INVOICE_EVENT_STAT.INVOICE_CREATED_AT,
                        fromTime.isPresent() ? LocalDateTime.ofInstant(fromTime.get(), ZoneOffset.UTC) : null,
                        Comparator.GREATER_OR_EQUAL)
                .addValue(INVOICE_EVENT_STAT.INVOICE_CREATED_AT,
                        toTime.isPresent() ? LocalDateTime.ofInstant(toTime.get(), ZoneOffset.UTC) : null,
                        Comparator.LESS);
    }

    @Override
    public Collection<Map<String, String>> getPaymentsTurnoverStat(String merchantId, int shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT currency_code AS currency_symbolic_code, SUM(amount - fee) AS amount_with_fee, SUM(amount) AS amount_without_fee, trunc(EXTRACT(EPOCH FROM (created_at - (:from_time::TIMESTAMP))) / EXTRACT(EPOCH FROM INTERVAL '" + splitInterval + "  sec')) AS sp_val FROM mst.payment WHERE shop_id = :shop_id AND merchant_id = :merchant_id AND created_at >= :from_time AND created_at < :to_time AND status = :succeeded_status GROUP BY sp_val, currency_code ORDER BY sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        try {
            return getNamedParameterJdbcTemplate().query(sql, params, (rs, i) -> {
                Map<String, String> map = new HashMap<>();
                map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
                map.put("currency_symbolic_code", rs.getString("currency_symbolic_code"));
                map.put("amount_with_fee", rs.getString("amount_with_fee"));
                map.put("amount_without_fee", rs.getString("amount_without_fee"));
                return map;
            });
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Collection<Map<String, String>> getPaymentsGeoStat(String merchantId, int shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT city_id, country_id, currency_code as currency_symbolic_code, SUM(amount - fee) as amount_with_fee, SUM(amount) as amount_without_fee, trunc(EXTRACT(epoch FROM (created_at - (:from_time::timestamp))) / EXTRACT(epoch FROM INTERVAL '" + splitInterval + "  sec')) AS sp_val FROM mst.payment where status = :succeeded_status and shop_id = :shop_id AND merchant_id = :merchant_id and created_at >= :from_time AND created_at < :to_time  group by sp_val, city_id, country_id, currency_code order by sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        try {
            return getNamedParameterJdbcTemplate().query(sql, params, (rs, i) -> {
                Map<String, String> map = new HashMap<>();
                map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
                map.put("city_id", rs.getString("city_id"));
                map.put("country_id", rs.getString("country_id"));
                map.put("currency_symbolic_code", rs.getString("currency_symbolic_code"));
                map.put("amount_with_fee", rs.getString("amount_with_fee"));
                map.put("amount_without_fee", rs.getString("amount_without_fee"));
                return map;
            });
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Collection<Map<String, String>> getPaymentsConversionStat(String merchantId, int shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "select t.*, t.successful_count::float / greatest(t.total_count, 1) as conversion from (SELECT SUM(case WHEN (status = :succeeded_status or status = :failed_status) then 1 else 0 end) as total_count, SUM(CASE WHEN status = :succeeded_status THEN 1 ELSE 0 END) as successful_count, trunc(EXTRACT(epoch FROM (created_at - (:from_time::timestamp))) / EXTRACT(epoch FROM INTERVAL '" + splitInterval + "  sec')) AS sp_val FROM mst.payment where shop_id = :shop_id AND merchant_id = :merchant_id AND created_at >= :from_time AND created_at < :to_time GROUP BY sp_val order by sp_val) as t";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        params.addValue("failed_status", InvoicePaymentStatus._Fields.FAILED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        try {
            return getNamedParameterJdbcTemplate().query(sql, params, (rs, i) -> {
                Map<String, String> map = new HashMap<>();
                map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
                map.put("conversion", rs.getString("conversion"));
                map.put("total_count", rs.getString("total_count"));
                map.put("successful_count", rs.getString("successful_count"));
                return map;
            });
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Collection<Map<String, String>> getCustomersRateStat(String merchantId, int shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT count(id) as unic_count, trunc(EXTRACT(epoch FROM (created_at - (:from_time::timestamp))) / EXTRACT(epoch FROM INTERVAL '" + splitInterval + " sec')) AS sp_val from mst.customer WHERE shop_id = :shop_id AND merchant_id = :merchant_id and created_at >= :from_time AND created_at < :to_time GROUP BY sp_val ORDER BY sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        log.trace("SQL: {}, Params: {}", sql, params);
        try {
            return getNamedParameterJdbcTemplate().query(sql, params, (rs, i) -> {
                Map<String, String> map = new HashMap<>();
                map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
                map.put("unic_count", rs.getString("unic_count"));
                return map;
            });
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Collection<Map<String, String>> getPaymentsCardTypesStat(String merchantId, int shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT count(payment_system) as total_count, payment_system as payment_system, SUM(amount - fee) as amount_with_fee, SUM(amount) as amount_without_fee, trunc(EXTRACT(epoch FROM (created_at - (:from_time::timestamp))) / EXTRACT(epoch FROM INTERVAL '" + splitInterval + "  sec')) AS sp_val FROM mst.payment where status = :succeeded_status and shop_id = :shop_id AND merchant_id = :merchant_id AND created_at >= :from_time AND created_at < :to_time GROUP BY sp_val, payment_system order by sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        try {
            return getNamedParameterJdbcTemplate().query(sql, params, (rs, i) -> {
                Map<String, String> map = new HashMap<>();
                map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
                map.put("total_count", rs.getString("total_count"));
                map.put("payment_system", rs.getString("payment_system"));
                map.put("amount_with_fee", rs.getString("amount_with_fee"));
                map.put("amount_without_fee", rs.getString("amount_without_fee"));
                return map;
            });
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Collection<Map<String, String>> getAccountingDataByPeriod(Instant fromTime, Instant toTime) throws DaoException {
        String sql = "SELECT part1.merchant_id, part1.shop_id, part1.currency_code, funds_acquired, fee_charged, coalesce(opening_balance, 0) AS opening_balance, (coalesce(opening_balance, 0) + funds_acquired - fee_charged) AS closing_balance FROM (SELECT merchant_id, shop_id, currency_code, sum(amount) AS funds_acquired, sum(fee) AS fee_charged FROM mst.payment WHERE status = :succeeded_status AND created_at >= :from_time AND created_at < :to_time GROUP BY shop_id, currency_code, merchant_id) part1 LEFT JOIN (SELECT merchant_id, shop_id, sum(amount - fee) AS opening_balance FROM mst.payment WHERE status = :succeeded_status AND created_at < :from_time GROUP BY shop_id, merchant_id) part2 ON part1.merchant_id = part2.merchant_id AND part1.shop_id = part2.shop_id ORDER BY merchant_id, shop_id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from_time", LocalDateTime.ofInstant(fromTime, ZoneId.of("UTC")), Types.OTHER)
                .addValue("to_time", LocalDateTime.ofInstant(toTime, ZoneId.of("UTC")), Types.OTHER)
                .addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params.getValues());

        try {
            return getNamedParameterJdbcTemplate().query(sql, params, (rs, i) -> {
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
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    private MapSqlParameterSource createParamsMap(String merchantId, int shopId, Instant fromTime, Instant toTime, Integer splitInterval) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("merchant_id", merchantId);
        params.addValue("shop_id", shopId);
        params.addValue("from_time", LocalDateTime.ofInstant(fromTime, ZoneId.of("UTC")), Types.OTHER);
        params.addValue("to_time", LocalDateTime.ofInstant(toTime, ZoneId.of("UTC")), Types.OTHER);
        params.addValue("split_interval", splitInterval);
        return params;
    }

    private StringBuilder addCondition(StringBuilder sb, String fieldName, boolean apply) {
        return addCondition(sb, fieldName, fieldName, "and", "=", apply);
    }

    private StringBuilder addInCondition(StringBuilder sb, String fieldName, String templateField, String op, boolean apply) {
        return apply ? sb.append(' ').append(op).append(' ').append(fieldName).append(" ").append("in").append(" ").append("(").append(':').append(templateField).append(")") : sb;
    }

    private StringBuilder addCondition(StringBuilder sb, String fieldName, String eq, boolean apply) {
        return addCondition(sb, fieldName, fieldName, "and", eq, apply);
    }

    private StringBuilder addCondition(StringBuilder sb, String fieldName, String templateField, String op, String eq, boolean apply) {
        return apply ? sb.append(' ').append(op).append(' ').append(fieldName).append(' ').append(eq).append(' ').append(':').append(templateField) : sb;
    }

    private StringBuilder addPagination(StringBuilder sb, String orderField, Optional<Integer> limit, Optional<Integer> offset) {
        sb.append(" order by ").append(orderField);
        if (limit.isPresent()) {
            sb.append(" limit ").append(limit.get());
        }
        if (offset.isPresent()) {
            sb.append(" offset ").append(offset.get());
        }
        return sb;
    }

}
