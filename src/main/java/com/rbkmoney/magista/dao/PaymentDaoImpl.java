package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Payment;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TSimpleJSONProtocol;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Created by tolkonepiu on 23.08.16.
 */
public class PaymentDaoImpl extends NamedParameterJdbcDaoSupport implements PaymentDao {

    Logger log = LoggerFactory.getLogger(this.getClass());

    public PaymentDaoImpl(DataSource ds) {
        setDataSource(ds);
    }

    @Override
    public Payment findById(String id) throws DaoException {
        String sql = "SELECT id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, " +
                "amount, currency_code, payment_system, city_name, ip, created_at, model " +
                "from mst.payment where id = :id";

        Payment payment;
        try {
            MapSqlParameterSource source = new MapSqlParameterSource("id", id);
            log.trace("SQL: {}, Params: {}", sql, source.getValues());
            payment = getNamedParameterJdbcTemplate().queryForObject(
                    sql,
                    source,
                    getRowMapper()
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
        return payment;
    }

    @Override
    public void insert(Payment payment) throws DaoException {
        String updateSql = "insert into mst.payment (id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, amount, currency_code, payment_system, city_name, ip, created_at, changed_at, model, data) " +
                "values (:id, :event_id, :invoice_id, :merchant_id, :shop_id, :customer_id, :masked_pan, :status, :amount, :currency_code, :payment_system, :city_name, :ip, :created_at, :changed_at, :model, :data)";
        execute(updateSql, createSqlParameterSource(payment));
    }

    @Override
    public void update(Payment payment) throws DaoException {
        String updateSql = "update mst.payment set " +
                "id = :id, event_id = :event_id, invoice_id = :invoice_id, merchant_id = :merchant_id, " +
                "shop_id = :shop_id, customer_id = :customer_id, masked_pan = :masked_pan, status = :status, " +
                "amount = :amount, currency_code = :currency_code, payment_system = :payment_system, " +
                "city_name = :city_name, ip = :ip, created_at = :created_at, changed_at=:changed_at, model = :model, data = :data where id = :id";
        execute(updateSql, createSqlParameterSource(payment));
    }

    public void execute(String updateSql, MapSqlParameterSource source) throws DaoException {
        try {
            log.trace("SQL: {}, Params: {}", updateSql, source.getValues());
            int rowsAffected = getNamedParameterJdbcTemplate().update(updateSql, source);

            if (rowsAffected != 1) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(updateSql, 1, rowsAffected);
            }
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    private MapSqlParameterSource createSqlParameterSource(Payment payment) {
        try {
            MapSqlParameterSource source = new MapSqlParameterSource()
                    .addValue("id", payment.getId())
                    .addValue("event_id", payment.getEventId())
                    .addValue("invoice_id", payment.getInvoiceId())
                    .addValue("merchant_id", payment.getMerchantId())
                    .addValue("shop_id", payment.getShopId())
                    .addValue("customer_id", payment.getCustomerId())
                    .addValue("masked_pan", payment.getMaskedPan())
                    .addValue("status", payment.getStatus().getFieldName())
                    .addValue("amount", payment.getAmount())
                    .addValue("currency_code", payment.getCurrencyCode())
                    .addValue("payment_system", payment.getPaymentSystem().name())
                    .addValue("city_name", payment.getCityName())
                    .addValue("ip", payment.getIp())
                    .addValue("created_at", Timestamp.from(payment.getCreatedAt()))
                    .addValue("changed_at", Timestamp.from(payment.getChangedAt()))
                    .addValue("model", new TSerializer(new TJSONProtocol.Factory()).toString(payment.getModel(), StandardCharsets.UTF_8.name()));

            PGobject data = new PGobject();
            data.setType("jsonb");
            data.setValue(new TSerializer(new TSimpleJSONProtocol.Factory()).toString(payment.getModel(), StandardCharsets.UTF_8.name()));
            source.addValue("data", data, Types.OTHER);

            return source;

        } catch (SQLException | TException ex) {
            throw new DaoException("Failed to serialize payment model", ex);
        }
    }

    public static RowMapper<Payment> getRowMapper() {
        return (rs, i) -> {
            try {
                Payment payment = new Payment();
                payment.setId(rs.getString("id"));
                payment.setEventId(rs.getLong("event_id"));
                payment.setInvoiceId(rs.getString("invoice_id"));
                payment.setMerchantId(rs.getString("merchant_id"));
                payment.setShopId(rs.getString("shop_id"));
                payment.setCustomerId(rs.getString("customer_id"));
                payment.setMaskedPan(rs.getString("masked_pan"));
                payment.setStatus(InvoicePaymentStatus._Fields.findByName(rs.getString("status")));
                payment.setAmount(rs.getLong("amount"));
                payment.setCurrencyCode(rs.getString("currency_code"));
                payment.setPaymentSystem(BankCardPaymentSystem.valueOf(rs.getString("payment_system")));
                payment.setCityName(rs.getString("city_name"));
                payment.setIp(rs.getString("ip"));
                payment.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                payment.setChangedAt(rs.getTimestamp("changed_at").toInstant());
                InvoicePayment model = new InvoicePayment();
                new TDeserializer(new TJSONProtocol.Factory()).deserialize(model, rs.getBytes("model"));

                payment.setModel(model);

                return payment;
            } catch (TException ex) {
                throw new SQLException("Failed to deserialize payment model", ex);
            }
        };
    }
}
