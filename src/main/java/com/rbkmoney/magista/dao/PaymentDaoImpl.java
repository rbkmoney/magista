package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.geck.serializer.kit.msgpack.MsgPackHandler;
import com.rbkmoney.geck.serializer.kit.msgpack.MsgPackProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Created by tolkonepiu on 23.08.16.
 */
public class PaymentDaoImpl extends NamedParameterJdbcDaoSupport implements PaymentDao {

    public PaymentDaoImpl(DataSource ds) {
        setDataSource(ds);
    }

    @Override
    public Payment findById(String paymentId, String invoiceId) throws DaoException {
        String sql = "select * from mst.payment where payment_id = :payment_id and invoice_id = :invoice_id" +
                " order by event_id desc limit 1";

        Payment payment;
        try {
            MapSqlParameterSource source = new MapSqlParameterSource()
                    .addValue("payment_id", paymentId)
                    .addValue("invoice_id", invoiceId);
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
        String updateSql = "insert into mst.payment (payment_id, event_id, invoice_id, merchant_id, shop_id, fingerprint, masked_pan, status, amount, fee, currency_code, payment_system, city_id, country_id, phone_number, email, ip, created_at, changed_at, model) " +
                "values (:payment_id, :event_id, :invoice_id, :merchant_id, :shop_id, :fingerprint, :masked_pan, :status, :amount, :fee, :currency_code, :payment_system, :city_id, :country_id, :phone_number, :email, :ip, :created_at, :changed_at, :model)";

        try {
            int rowsAffected = getNamedParameterJdbcTemplate().update(updateSql, createSqlParameterSource(payment));

            if (rowsAffected != 1) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(updateSql, 1, rowsAffected);
            }
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    private MapSqlParameterSource createSqlParameterSource(Payment payment) {
        try {
            return new MapSqlParameterSource()
                    .addValue("payment_id", payment.getId())
                    .addValue("event_id", payment.getEventId())
                    .addValue("invoice_id", payment.getInvoiceId())
                    .addValue("merchant_id", payment.getMerchantId())
                    .addValue("shop_id", payment.getShopId())
                    .addValue("fingerprint", payment.getFingerprint())
                    .addValue("masked_pan", payment.getMaskedPan())
                    .addValue("status", payment.getStatus().getFieldName())
                    .addValue("amount", payment.getAmount())
                    .addValue("fee", payment.getFee())
                    .addValue("currency_code", payment.getCurrencyCode())
                    .addValue("payment_system", payment.getPaymentSystem(), Types.VARCHAR)
                    .addValue("city_id", payment.getCityId())
                    .addValue("country_id", payment.getCountryId())
                    .addValue("phone_number", payment.getPhoneNumber())
                    .addValue("email", payment.getEmail())
                    .addValue("ip", payment.getIp())
                    .addValue("created_at", LocalDateTime.ofInstant(payment.getCreatedAt(),
                            ZoneOffset.UTC), Types.OTHER)
                    .addValue("changed_at", LocalDateTime.ofInstant(payment.getChangedAt(),
                            ZoneOffset.UTC), Types.OTHER)
                    .addValue("model", new TBaseProcessor().process(payment.getModel(),
                            MsgPackHandler.newBufferedInstance(true)));

        } catch (IOException ex) {
            throw new DaoException("Failed to serialize payment model", ex);
        }
    }

    public static RowMapper<Payment> getRowMapper() {
        return (rs, i) -> {
            try {
                Payment payment = new Payment();
                payment.setId(rs.getString("payment_id"));
                payment.setEventId(rs.getLong("event_id"));
                payment.setInvoiceId(rs.getString("invoice_id"));
                payment.setMerchantId(rs.getString("merchant_id"));
                payment.setShopId(rs.getInt("shop_id"));
                payment.setFingerprint(rs.getString("fingerprint"));
                payment.setMaskedPan(rs.getString("masked_pan"));
                payment.setStatus(InvoicePaymentStatus._Fields.findByName(rs.getString("status")));
                payment.setAmount(rs.getLong("amount"));
                payment.setFee(rs.getLong("fee"));
                payment.setCurrencyCode(rs.getString("currency_code"));
                payment.setPaymentSystem(BankCardPaymentSystem.valueOf(rs.getString("payment_system")));
                payment.setCityId(rs.getInt("city_id"));
                payment.setCountryId(rs.getInt("country_id"));
                payment.setPhoneNumber(rs.getString("phone_number"));
                payment.setEmail(rs.getString("email"));
                payment.setIp(rs.getString("ip"));
                payment.setCreatedAt(rs.getObject("created_at", LocalDateTime.class)
                        .toInstant(ZoneOffset.UTC));
                payment.setChangedAt(rs.getObject("changed_at", LocalDateTime.class)
                        .toInstant(ZoneOffset.UTC));
                InvoicePayment model =
                        MsgPackProcessor.newBinaryInstance().process(rs.getBytes("model"),
                                new TBaseHandler<>(InvoicePayment.class));
                payment.setModel(model);

                return payment;
            } catch (IOException ex) {
                throw new SQLException("Failed to deserialize payment model", ex);
            }
        };
    }
}
