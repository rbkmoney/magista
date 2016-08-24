package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.dao.InvoiceDaoImpl;
import com.rbkmoney.magista.dao.PaymentDaoImpl;
import com.rbkmoney.magista.query.Pair;
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
    public Pair<Integer, Collection<Invoice>> getInvoices(
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
        StringBuilder countSb = new StringBuilder("select count(*) from mst.invoice");

        dataSb = func.apply(dataSb);
        addPagination(dataSb, "event_id", limit, offset);

        countSb = func.apply(countSb);

        String dataSql = dataSb.toString();
        String countSql = countSb.toString();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("merchant_id", merchantId);
        params.addValue("shop_id", shopId);
        params.addValue("id", invoiceId.orElse(null));
        params.addValue("status", invoiceStatus.orElse(null));
        params.addValue("from_time", fromTime.isPresent() ? Timestamp.from(fromTime.get()): null);
        params.addValue("to_time", toTime.isPresent() ? Timestamp.from(toTime.get()): null);

        try {
            List<Invoice> invoices = getNamedParameterJdbcTemplate().query(dataSql, params, InvoiceDaoImpl.getRowMapper());
            Number count = getNamedParameterJdbcTemplate().queryForObject(countSql, params, Number.class);
            return new Pair<>(count.intValue(), invoices);
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Pair<Integer, Collection<Payment>> getPayments(
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
        StringBuilder countSb = new StringBuilder("select count(*) from mst.payment");

        dataSb = func.apply(dataSb);
        addPagination(dataSb, "event_id", limit, offset);

        countSb = func.apply(countSb);

        String dataSql = dataSb.toString();
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
            List<Payment> payments = getNamedParameterJdbcTemplate().query(dataSql, params, PaymentDaoImpl.getRowMapper());
            Number count = getNamedParameterJdbcTemplate().queryForObject(countSql, params, Number.class);
            return new Pair<>(count.intValue(), payments);
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Collection<Map<String, String>> getPaymentsTurnoverStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        StringBuilder sb = new StringBuilder("select currency_code as currency_symbolic_code, SUM(amount) as amount_with_fee, SUM(amount) as amount_without_fee, ");
        addTail(sb, "mst.payment", "sp_val", splitInterval, Arrays.asList("currency_code"), Arrays.asList("currency_code"), () -> " and status = '"+InvoicePaymentStatus._Fields.SUCCEEDED.getFieldName()+"'");
        String sql = sb.toString();
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime);
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
        StringBuilder sb = new StringBuilder("select city_name, currency_code as currency_symbolic_code, SUM(amount) as amount_with_fee, SUM(amount) as amount_without_fee, ");
        addTail(sb, "mst.payment", "sp_val", splitInterval, Arrays.asList("city_name", "currency_code"), Arrays.asList("city_name", "currency_code"), () -> " and status = '"+InvoicePaymentStatus._Fields.SUCCEEDED.getFieldName()+"'");
        String sql = sb.toString();
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime);
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
        StringBuilder sb = new StringBuilder("select t.*, t.successful_count::float / t.total_count as conversion from (SELECT SUM(case WHEN (status = '"+InvoicePaymentStatus._Fields.SUCCEEDED.getFieldName()+"' or status = '"+InvoicePaymentStatus._Fields.FAILED.getFieldName()+"') then 1 else 0 end) as total_count, SUM(CASE WHEN status = '"+ InvoicePaymentStatus._Fields.SUCCEEDED.getFieldName()+"' THEN 1 ELSE 0 END) as successful_count, ");
        addTail(sb, "mst.payment", "sp_val", splitInterval, Collections.EMPTY_LIST, Collections.EMPTY_LIST, () -> "");
        sb.append(") as t");
        String sql = sb.toString();
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime);
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
        StringBuilder sb = new StringBuilder("SELECT count(id) as unic_count, ");
        addTail(sb, "mst.customer", "sp_val", splitInterval, Collections.EMPTY_LIST, Collections.EMPTY_LIST, () -> "");
        String sql = sb.toString();
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime);
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

    private MapSqlParameterSource createParamsMap(String merchantId, String shopId, Instant fromTime, Instant toTime) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("merchant_id", merchantId);
        params.addValue("shop_id", shopId);
        params.addValue("from_time", Timestamp.from(fromTime));
        params.addValue("to_time", Timestamp.from(toTime));
        return params;
    }

    private StringBuilder addTail(StringBuilder sb, String tableName, String chunkFieldName, int splitInterval, Collection<String> extraGroupingFields, Collection<String> extraOrderingFields, Supplier<String> extraWhereSupplier) {
        String splitField = chunkFieldName;
        addChunckField(sb, "from_time", splitField, splitInterval);
        sb.append(" from ").append(tableName);
        sb.append(" where shop_id=:shop_id and merchant_id=:merchant_id and created_at >= :from_time and created_at < :to_time ");
        sb.append(extraWhereSupplier.get());
        sb.append(" group by ").append(splitField);
        for (String field: extraGroupingFields) {
            sb.append(',').append(field);
        }

        sb.append(" order by ").append(splitField);
        for (String field: extraOrderingFields) {
            sb.append(',').append(field);
        }
        return sb;
    }

    private StringBuilder addChunckField(StringBuilder sb, String fromTimeNameTemplate, String chunkFieldName, int splitInterval) {
        sb.append(" trunc(EXTRACT(epoch FROM (created_at - ").append(':').append(fromTimeNameTemplate).append(")) / EXTRACT(epoch FROM INTERVAL '").append(splitInterval).append(" sec')) AS ").append(chunkFieldName).append(' ');
        return sb;
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
