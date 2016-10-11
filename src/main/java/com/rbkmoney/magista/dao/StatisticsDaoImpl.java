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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

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
            String shopId,
            Optional<String> invoiceId,
            Optional<String> invoiceStatus,
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset
    ) throws DaoException {
        Function<StringBuilder, StringBuilder> func = head -> {
            head.append(" where (TRUE) ");
            addCondition(head, "merchant_id", true);
            addCondition(head, "shop_id", true);
            addCondition(head, "id", invoiceId.isPresent());
            addCondition(head, "status", invoiceStatus.isPresent());
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
        params.addValue("id", invoiceId.orElse(null));
        params.addValue("status", invoiceStatus.orElse(null));
        params.addValue("from_time", fromTime.isPresent() ? Timestamp.from(fromTime.get()): null);
        params.addValue("to_time", toTime.isPresent() ? Timestamp.from(toTime.get()): null);

        try {
            List<Invoice> invoices = getNamedParameterJdbcTemplate().query(dataSql, params, InvoiceDaoImpl.getRowMapper());
            return invoices;
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public int getInvoicesCount(String merchantId, String shopId, Optional<String> invoiceId, Optional<String> invoiceStatus, Optional<Instant> fromTime, Optional<Instant> toTime, Optional<Integer> limit, Optional<Integer> offset) throws DaoException {
        Function<StringBuilder, StringBuilder> func = head -> {
            head.append(" where (TRUE) ");
            addCondition(head, "merchant_id", true);
            addCondition(head, "shop_id", true);
            addCondition(head, "id", invoiceId.isPresent());
            addCondition(head, "status", invoiceStatus.isPresent());
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
        params.addValue("id", invoiceId.orElse(null));
        params.addValue("status", invoiceStatus.orElse(null));
        params.addValue("from_time", fromTime.isPresent() ? Timestamp.from(fromTime.get()): null);
        params.addValue("to_time", toTime.isPresent() ? Timestamp.from(toTime.get()): null);

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
            String shopId,
            Optional<String> invoiceId,
            Optional<String> paymentId,
            Optional<String> paymentStatus,
            Optional<String> panMask,
            Optional<Instant> fromTime,
            Optional<Instant> toTime,
            Optional<Integer> limit,
            Optional<Integer> offset
    ) throws DaoException {
        Function<StringBuilder, StringBuilder> func = head -> {
            head.append(" where (TRUE) ");
            addCondition(head, "merchant_id", true);
            addCondition(head, "shop_id", true);
            addCondition(head, "invoice_id", invoiceId.isPresent());
            addCondition(head, "id", paymentId.isPresent());
            addCondition(head, "status", paymentStatus.isPresent());
            if (panMask.isPresent()) {
                head.append(" masked_pan like :masked_pan ");
            }
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
        params.addValue("id", paymentId.orElse(null));
        params.addValue("status", paymentStatus.orElse(null));
        params.addValue("masked_pan", paymentStatus.orElse("").replaceAll("\\*", "_"));
        params.addValue("from_time", fromTime.isPresent() ? Timestamp.from(fromTime.get()): null);
        params.addValue("to_time", toTime.isPresent() ? Timestamp.from(toTime.get()): null);

        try {
            List<Payment> payments = getNamedParameterJdbcTemplate().query(dataSql, params, PaymentDaoImpl.getRowMapper());
            return payments;
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Integer getPaymentsCount(String merchantId, String shopId, Optional<String> invoiceId, Optional<String> paymentId, Optional<String> paymentStatus, Optional<String> panMask, Optional<Instant> fromTime, Optional<Instant> toTime, Optional<Integer> limit, Optional<Integer> offset) throws DaoException {
        Function<StringBuilder, StringBuilder> func = head -> {
            head.append(" where (TRUE) ");
            addCondition(head, "merchant_id", true);
            addCondition(head, "shop_id", true);
            addCondition(head, "invoice_id", invoiceId.isPresent());
            addCondition(head, "id", paymentId.isPresent());
            addCondition(head, "status", paymentStatus.isPresent());
            if (panMask.isPresent()) {
                head.append(" masked_pan like :masked_pan ");
            }
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
        params.addValue("id", paymentId.orElse(null));
        params.addValue("status", paymentStatus.orElse(null));
        params.addValue("masked_pan", paymentStatus.orElse("").replaceAll("\\*", "_"));
        params.addValue("from_time", fromTime.isPresent() ? Timestamp.from(fromTime.get()): null);
        params.addValue("to_time", toTime.isPresent() ? Timestamp.from(toTime.get()): null);

        try {
            Number count = getNamedParameterJdbcTemplate().queryForObject(countSql, params, Number.class);
            return count.intValue();
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }    }

    @Override
    public Collection<Map<String, String>> getPaymentsTurnoverStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT currency_code AS currency_symbolic_code, SUM(amount) AS amount_with_fee, SUM(amount) AS amount_without_fee, trunc(EXTRACT(EPOCH FROM (created_at - (:from_time::TIMESTAMP))) / EXTRACT(EPOCH FROM INTERVAL '"+splitInterval+"  sec')) AS sp_val FROM mst.payment WHERE shop_id = :shop_id AND merchant_id = :merchant_id AND created_at >= :from_time AND created_at < :to_time AND status = :succeeded_status GROUP BY sp_val, currency_code ORDER BY sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        try {
            return getNamedParameterJdbcTemplate().query(sql, params, (rs, i) -> {
                Map<String, String> map = new HashMap<>();
                map.put("offset", (rs.getLong("sp_val")*splitInterval)+"" );
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
    public Collection<Map<String, String>> getPaymentsGeoStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT city_name, currency_code as currency_symbolic_code, SUM(amount) as amount_with_fee, SUM(amount) as amount_without_fee, trunc(EXTRACT(epoch FROM (created_at - (:from_time::timestamp))) / EXTRACT(epoch FROM INTERVAL '"+splitInterval+"  sec')) AS sp_val FROM mst.payment where status = :succeeded_status and shop_id = :shop_id AND merchant_id = :merchant_id and created_at >= :from_time AND created_at < :to_time  group by sp_val, city_name, currency_code order by sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        try {
            return getNamedParameterJdbcTemplate().query(sql, params, (rs, i) -> {
                Map<String, String> map = new HashMap<>();
                map.put("offset", (rs.getLong("sp_val")*splitInterval)+"" );
                map.put("city_name", rs.getString("city_name"));
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
    public Collection<Map<String, String>> getPaymentsConversionStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "select t.*, t.successful_count::float / t.total_count as conversion from (SELECT SUM(case WHEN (status = :succeeded_status or status = :failed_status) then 1 else 0 end) as total_count, SUM(CASE WHEN status = :succeeded_status THEN 1 ELSE 0 END) as successful_count, trunc(EXTRACT(epoch FROM (created_at - (:from_time::timestamp))) / EXTRACT(epoch FROM INTERVAL '"+splitInterval+"  sec')) AS sp_val FROM mst.payment where shop_id = :shop_id AND merchant_id = :merchant_id AND created_at >= :from_time AND created_at < :to_time GROUP BY sp_val order by sp_val) as t";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        params.addValue("failed_status", InvoicePaymentStatus._Fields.FAILED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        try {
            return getNamedParameterJdbcTemplate().query(sql, params, (rs, i) -> {
                Map<String, String> map = new HashMap<>();
                map.put("offset", (rs.getLong("sp_val")*splitInterval)+"" );
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
    public Collection<Map<String, String>> getCustomersRateStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT count(id) as unic_count, trunc(EXTRACT(epoch FROM (created_at - (:from_time::timestamp))) / EXTRACT(epoch FROM INTERVAL '"+splitInterval+" sec')) AS sp_val from mst.customer WHERE shop_id = :shop_id AND merchant_id = :merchant_id and created_at >= :from_time AND created_at < :to_time GROUP BY sp_val ORDER BY sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        log.trace("SQL: {}, Params: {}", sql, params);
        try {
            return getNamedParameterJdbcTemplate().query(sql, params, (rs, i) -> {
                Map<String, String> map = new HashMap<>();
                map.put("offset", (rs.getLong("sp_val")*splitInterval)+"" );
                map.put("unic_count", rs.getString("unic_count"));
                return map;
            });
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Collection<Map<String, String>> getPaymentsCardTypesStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT count(payment_system) as total_count, payment_system as payment_system, SUM(amount) as amount_with_fee, SUM(amount) as amount_without_fee, trunc(EXTRACT(epoch FROM (created_at - (:from_time::timestamp))) / EXTRACT(epoch FROM INTERVAL '"+splitInterval+"  sec')) AS sp_val FROM mst.payment where status = :succeeded_status and shop_id = :shop_id AND merchant_id = :merchant_id AND created_at >= :from_time AND created_at < :to_time GROUP BY sp_val, payment_system order by sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        try {
            return getNamedParameterJdbcTemplate().query(sql, params, (rs, i) -> {
                Map<String, String> map = new HashMap<>();
                map.put("offset", (rs.getLong("sp_val")*splitInterval)+"" );
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

    private MapSqlParameterSource createParamsMap(String merchantId, String shopId, Instant fromTime, Instant toTime, Integer splitInterval) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("merchant_id", merchantId);
        params.addValue("shop_id", shopId);
        params.addValue("from_time", Timestamp.from(fromTime));
        params.addValue("to_time", Timestamp.from(toTime));
        params.addValue("split_interval", splitInterval);
        return params;
    }

    private StringBuilder addCondition(StringBuilder sb, String fieldName, boolean apply) {
        return addCondition(sb, fieldName, fieldName, "and", "=", apply);
    }

    private StringBuilder addCondition(StringBuilder sb, String fieldName, String templateField, String op, String eq, boolean apply) {
        return apply ? sb.append(' ').append(op).append(' '). append(fieldName).append(eq).append(':').append(templateField) : sb;
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
