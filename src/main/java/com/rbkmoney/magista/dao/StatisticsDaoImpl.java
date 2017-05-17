package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by vpankrashkin on 10.08.16.
 */
public class StatisticsDaoImpl extends NamedParameterJdbcDaoSupport implements StatisticsDao {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public StatisticsDaoImpl(DataSource ds) {
        setDataSource(ds);
    }

    @Override
    public Collection<Invoice> getInvoices(
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
        Function<StringBuilder, StringBuilder> func = head -> {
            head.append(" where (TRUE) ");
            addCondition(head, "merchant_id", true);
            addCondition(head, "shop_id", true);
            addInCondition(head, "id", "invoice_ids", "and",
                    invoiceId.isPresent()
                            || (paymentAmount.isPresent() || paymentEmail.isPresent()
                            || paymentFingerprint.isPresent() || paymentId.isPresent()
                            || paymentIp.isPresent() || paymentPanMask.isPresent()
                            || paymentStatus.isPresent()));
            addCondition(head, "status", invoiceStatus.isPresent());
            addCondition(head, "amount", invoiceAmount.isPresent());
            addCondition(head, "created_at", "from_time", "and", ">=", fromTime.isPresent());
            addCondition(head, "created_at", "to_time", "and", "<", toTime.isPresent());
            return head;
        };

        StringBuilder dataSb = new StringBuilder("select * from mst.invoice");

        dataSb = func.apply(dataSb);
        addPagination(dataSb, "event_id desc", limit, offset);

        String dataSql = dataSb.toString();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("merchant_id", merchantId);
        params.addValue("shop_id", shopId);

        Collection<Payment> payments = getPayments(
                merchantId,
                shopId,
                invoiceId,
                paymentId,
                paymentStatus,
                paymentEmail,
                paymentIp,
                paymentFingerprint,
                paymentPanMask,
                paymentAmount,
                fromTime,
                toTime,
                limit,
                offset
        );

        Set<String> invoiceIds = payments.stream().map(t -> t.getInvoiceId()).collect(Collectors.toSet());
        if (invoiceId.isPresent()) {
            invoiceIds.add(invoiceId.get());
        }
        if (!invoiceIds.isEmpty()) {
            params.addValue("invoice_ids", invoiceIds);
        } else {
            params.addValue("invoice_ids", null);
        }

        params.addValue("status", invoiceStatus.orElse(null));
        params.addValue("amount", invoiceAmount.orElse(null));

        if (fromTime.isPresent()) {
            params.addValue("from_time", LocalDateTime.ofInstant(fromTime.get(), ZoneId.of("UTC")), Types.OTHER);
        } else {
            params.addValue("from_time", null);
        }
        if (toTime.isPresent()) {
            params.addValue("to_time", LocalDateTime.ofInstant(toTime.get(), ZoneId.of("UTC")), Types.OTHER);
        } else {
            params.addValue("to_time", null);
        }

        try {
            List<Invoice> invoices = getNamedParameterJdbcTemplate().query(dataSql, params, InvoiceDaoImpl.getRowMapper());
            return invoices;
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
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
        Function<StringBuilder, StringBuilder> func = head -> {
            head.append(" where (TRUE) ");
            addCondition(head, "merchant_id", true);
            addCondition(head, "shop_id", true);
            addInCondition(head, "id", "invoice_ids", "and",
                    invoiceId.isPresent()
                            || (paymentAmount.isPresent() || paymentEmail.isPresent()
                            || paymentFingerprint.isPresent() || paymentId.isPresent()
                            || paymentIp.isPresent() || paymentPanMask.isPresent()
                            || paymentStatus.isPresent()));
            addCondition(head, "status", invoiceStatus.isPresent());
            addCondition(head, "amount", invoiceAmount.isPresent());
            addCondition(head, "created_at", "from_time", "and", ">=", fromTime.isPresent());
            addCondition(head, "created_at", "to_time", "and", "<", toTime.isPresent());
            return head;
        };

        StringBuilder countSb = new StringBuilder("select count(*) from mst.invoice");
        countSb = func.apply(countSb);

        String countSql = countSb.toString();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("merchant_id", merchantId);
        params.addValue("shop_id", shopId);

        Collection<Payment> payments = getPayments(
                merchantId,
                shopId,
                invoiceId,
                paymentId,
                paymentStatus,
                paymentEmail,
                paymentIp,
                paymentFingerprint,
                paymentPanMask,
                paymentAmount,
                fromTime,
                toTime,
                limit,
                offset
        );

        Set<String> invoiceIds = payments.stream().map(t -> t.getInvoiceId()).collect(Collectors.toSet());
        if (invoiceId.isPresent()) {
            invoiceIds.add(invoiceId.get());
        }
        if (!invoiceIds.isEmpty()) {
            params.addValue("invoice_ids", invoiceIds);
        } else {
            params.addValue("invoice_ids", null);
        }

        params.addValue("status", invoiceStatus.orElse(null));
        params.addValue("amount", invoiceAmount.orElse(null));

        if (fromTime.isPresent()) {
            params.addValue("from_time", LocalDateTime.ofInstant(fromTime.get(), ZoneId.of("UTC")), Types.OTHER);
        } else {
            params.addValue("from_time", null);
        }
        if (toTime.isPresent()) {
            params.addValue("to_time", LocalDateTime.ofInstant(toTime.get(), ZoneId.of("UTC")), Types.OTHER);
        } else {
            params.addValue("to_time", null);
        }
        try {
            Number count = getNamedParameterJdbcTemplate().queryForObject(countSql, params, Number.class);
            return count.intValue();
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Collection<Payment> getPayments(
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
        Function<StringBuilder, StringBuilder> func = head -> {
            head.append(" where (TRUE) ");
            addCondition(head, "merchant_id", true);
            addCondition(head, "shop_id", true);
            addCondition(head, "invoice_id", invoiceId.isPresent());
            addCondition(head, "payment_id", paymentId.isPresent());
            addCondition(head, "status", paymentStatus.isPresent());
            addCondition(head, "masked_pan", "like", paymentPanMask.isPresent());
            addCondition(head, "ip", "like", paymentIp.isPresent());
            addCondition(head, "customer_id", "like", paymentFingerprint.isPresent());
            addCondition(head, "email", "like", paymentEmail.isPresent());

            addCondition(head, "created_at", "from_time", "and", ">=", fromTime.isPresent());
            addCondition(head, "created_at", "to_time", "and", "<", toTime.isPresent());
            return head;
        };

        StringBuilder dataSb = new StringBuilder("select * from mst.payment");

        dataSb = func.apply(dataSb);
        addPagination(dataSb, "event_id desc", limit, offset);


        String dataSql = dataSb.toString();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("merchant_id", merchantId);
        params.addValue("shop_id", shopId);
        params.addValue("invoice_id", invoiceId.orElse(null));
        params.addValue("payment_id", paymentId.orElse(null));
        params.addValue("status", paymentStatus.orElse(null));
        params.addValue("masked_pan", paymentPanMask.orElse("").replaceAll("\\*", "_"));
        params.addValue("ip", paymentIp.orElse("").replaceAll("\\*", "_"));
        params.addValue("customer_id", paymentFingerprint.orElse("").replaceAll("\\*", "_"));
        params.addValue("email", paymentEmail.orElse("").replaceAll("\\*", "_"));

        if (fromTime.isPresent()) {
            params.addValue("from_time", LocalDateTime.ofInstant(fromTime.get(), ZoneId.of("UTC")), Types.OTHER);
        } else {
            params.addValue("from_time", null);
        }
        if (toTime.isPresent()) {
            params.addValue("to_time", LocalDateTime.ofInstant(toTime.get(), ZoneId.of("UTC")), Types.OTHER);
        } else {
            params.addValue("to_time", null);
        }

        try {
            List<Payment> payments = getNamedParameterJdbcTemplate().query(dataSql, params, PaymentDaoImpl.getRowMapper());
            return payments;
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
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
        Function<StringBuilder, StringBuilder> func = head -> {
            head.append(" where (TRUE) ");
            addCondition(head, "merchant_id", true);
            addCondition(head, "shop_id", true);
            addCondition(head, "invoice_id", invoiceId.isPresent());
            addCondition(head, "payment_id", paymentId.isPresent());
            addCondition(head, "status", paymentStatus.isPresent());
            addCondition(head, "masked_pan", "like", paymentPanMask.isPresent());
            addCondition(head, "ip", "like", paymentIp.isPresent());
            addCondition(head, "customer_id", "like", paymentFingerprint.isPresent());
            addCondition(head, "email", "like", paymentEmail.isPresent());

            addCondition(head, "created_at", "from_time", "and", ">=", fromTime.isPresent());
            addCondition(head, "created_at", "to_time", "and", "<", toTime.isPresent());
            return head;
        };

        StringBuilder countSb = new StringBuilder("select count(*) from mst.payment");

        countSb = func.apply(countSb);

        String countSql = countSb.toString();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("merchant_id", merchantId);
        params.addValue("shop_id", shopId);
        params.addValue("invoice_id", invoiceId.orElse(null));
        params.addValue("payment_id", paymentId.orElse(null));
        params.addValue("status", paymentStatus.orElse(null));
        params.addValue("masked_pan", paymentPanMask.orElse("").replaceAll("\\*", "_"));
        params.addValue("ip", paymentIp.orElse("").replaceAll("\\*", "_"));
        params.addValue("customer_id", paymentFingerprint.orElse("").replaceAll("\\*", "_"));
        params.addValue("email", paymentEmail.orElse("").replaceAll("\\*", "_"));

        if (fromTime.isPresent()) {
            params.addValue("from_time", LocalDateTime.ofInstant(fromTime.get(), ZoneId.of("UTC")), Types.OTHER);
        } else {
            params.addValue("from_time", null);
        }
        if (toTime.isPresent()) {
            params.addValue("to_time", LocalDateTime.ofInstant(toTime.get(), ZoneId.of("UTC")), Types.OTHER);
        } else {
            params.addValue("to_time", null);
        }

        try {
            Number count = getNamedParameterJdbcTemplate().queryForObject(countSql, params, Number.class);
            return count.intValue();
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Collection<Map<String, String>> getPaymentsTurnoverStat(String merchantId, int shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT invoice_currency_code AS currency_symbolic_code, SUM(payment_amount - payment_fee) AS amount_with_fee, SUM(payment_amount) AS amount_without_fee, trunc(EXTRACT(EPOCH FROM (payment_created_at - (:from_time::TIMESTAMP))) / EXTRACT(EPOCH FROM INTERVAL '" + splitInterval + "  sec')) AS sp_val FROM mst.invoice_event t1 WHERE event_id = (select max(t2.event_id) from mst.invoice_event t2 where t2.invoice_id = t1.invoice_id and t2.payment_id = t1.payment_id) and shop_id = :shop_id AND merchant_id = :merchant_id AND payment_created_at >= :from_time AND payment_created_at < :to_time AND payment_status = :succeeded_status GROUP BY sp_val, invoice_currency_code ORDER BY sp_val";
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
        String sql = "SELECT payment_city_id, payment_country_id, invoice_currency_code AS currency_symbolic_code, sum(payment_amount - payment_fee) AS amount_with_fee, sum(payment_amount) as amount_without_fee, trunc(EXTRACT(EPOCH from (payment_created_at - (:from_time :: TIMESTAMP))) / EXTRACT(EPOCH FROM INTERVAL '" + splitInterval + " sec')) as sp_val from mst.invoice_event t1 where event_id = (select max(t2.event_id) from mst.invoice_event t2 where t2.invoice_id = t1.invoice_id and t2.payment_id = t1.payment_id) and payment_status = :succeeded_status and shop_id = :shop_id and merchant_id = :merchant_id and payment_created_at >= :from_time and payment_created_at < :to_time group by sp_val, payment_city_id, payment_country_id, invoice_currency_code order by sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        try {
            return getNamedParameterJdbcTemplate().query(sql, params, (rs, i) -> {
                Map<String, String> map = new HashMap<>();
                map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
                map.put("city_id", rs.getString("payment_city_id"));
                map.put("country_id", rs.getString("payment_country_id"));
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
        String sql = "SELECT t.*, t.successful_count :: FLOAT / greatest(t.total_count, 1) AS conversion FROM (SELECT SUM(CASE WHEN (payment_status = :succeeded_status OR payment_status = :failed_status) THEN 1 ELSE 0 END) AS total_count, SUM(CASE WHEN payment_status = :succeeded_status THEN 1 ELSE 0 END) AS successful_count, trunc(EXTRACT(EPOCH FROM (payment_created_at - (:from_time :: TIMESTAMP))) / EXTRACT(EPOCH FROM INTERVAL '" + splitInterval + " sec')) AS sp_val FROM mst.invoice_event t1 WHERE event_id = (select max(event_id) from mst.invoice_event t2 where t2.invoice_id = t1.invoice_id and t2.payment_id = t1.payment_id) and shop_id = :shop_id AND merchant_id = :merchant_id AND payment_created_at >= :from_time AND payment_created_at < :to_time GROUP BY sp_val ORDER BY sp_val) AS t";
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
        String sql = "select count(payment_fingerprint) as unic_count, trunc(EXTRACT(epoch from (event_created_at - (:from_time::timestamp))) / EXTRACT(epoch from interval '" + splitInterval + " sec')) as sp_val from (select distinct on (payment_fingerprint) payment_fingerprint, event_created_at from mst.invoice_event where payment_fingerprint is not null and shop_id = :shop_id and merchant_id = :merchant_id and event_created_at >= :from_time and event_created_at < :to_time) as customers group by sp_val order by sp_val";
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
        String sql = "select count(payment_system) as total_count, payment_system as payment_system, sum(payment_amount - payment_fee) as amount_with_fee, sum(payment_amount) as amount_without_fee, trunc(EXTRACT(EPOCH FROM (payment_created_at - (:from_time :: TIMESTAMP))) / EXTRACT(EPOCH FROM INTERVAL '" + splitInterval + " sec')) as sp_val FROM mst.invoice_event t1 WHERE event_id = (select max(event_id) from mst.invoice_event t2 where t2.invoice_id = t1.invoice_id and t2.payment_id = t1.payment_id) and payment_status = :succeeded_status and shop_id = :shop_id and merchant_id = :merchant_id and payment_created_at >= :from_time and payment_created_at < :to_time GROUP BY sp_val, payment_system ORDER BY sp_val";
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
        return apply ? sb.append(' ').append(op).append(' ').append(fieldName).append(eq).append(':').append(templateField) : sb;
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
