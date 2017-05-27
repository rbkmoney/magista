package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.util.StorageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;
import java.sql.Timestamp;

/**
 * Created by tolkonepiu on 23.08.16.
 */
public class PaymentDaoImpl extends NamedParameterJdbcDaoSupport implements PaymentDao {

    Logger log = LoggerFactory.getLogger(this.getClass());

    public PaymentDaoImpl(DataSource ds) {
        setDataSource(ds);
    }

    @Override
    public Payment findById(String paymentId, String invoiceId) throws DaoException {
        String sql = "SELECT * FROM mst.payment WHERE payment_id = :payment_id AND invoice_id = :invoice_id";

        Payment payment;
        try {
            MapSqlParameterSource source = new MapSqlParameterSource()
                    .addValue("payment_id", paymentId)
                    .addValue("invoice_id", invoiceId);
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
        String updateSql = "insert into mst.payment (payment_id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, failure_code, failure_description, amount, fee, token, session_id, bin, payment_tool, currency_code, payment_system, city_id, country_id, phone_number, email, ip, created_at, changed_at, context) " +
                "values (:payment_id, :event_id, :invoice_id, :merchant_id, :shop_id, :customer_id, :masked_pan, :status, :failure_code, :failure_description, :amount, :fee, :token, :session_id, :bin, :payment_tool, :currency_code, :payment_system, :city_id, :country_id, :phone_number, :email, :ip, :created_at, :changed_at, :context)";
        execute(updateSql, createSqlParameterSource(payment));
    }

    @Override
    public void update(Payment payment) throws DaoException {
        String updateSql = "update mst.payment set " +
                "payment_id = :payment_id, event_id = :event_id, invoice_id = :invoice_id, merchant_id = :merchant_id, " +
                "shop_id = :shop_id, customer_id = :customer_id, masked_pan = :masked_pan, status = :status, failure_code = :failure_code, failure_description = :failure_description, " +
                "amount = :amount, fee = :fee, token = :token, session_id = :session_id, bin = :bin, payment_tool = :payment_tool, currency_code = :currency_code, payment_system = :payment_system, " +
                "city_id = :city_id, country_id = :country_id, phone_number = :phone_number, email = :email, ip = :ip, created_at = :created_at, changed_at=:changed_at, context = :context where payment_id = :payment_id and invoice_id = :invoice_id";
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
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("payment_id", payment.getId())
                .addValue("event_id", payment.getEventId())
                .addValue("invoice_id", payment.getInvoiceId())
                .addValue("merchant_id", payment.getMerchantId())
                .addValue("shop_id", payment.getShopId())
                .addValue("customer_id", payment.getCustomerId())
                .addValue("masked_pan", payment.getMaskedPan())
                .addValue("status", payment.getStatus().getFieldName())
                .addValue("failure_code", payment.getFailureCode())
                .addValue("failure_description", payment.getFailureDescription())
                .addValue("amount", payment.getAmount())
                .addValue("fee", payment.getFee())
                .addValue("token", payment.getToken())
                .addValue("session_id", payment.getSessionId())
                .addValue("bin", payment.getBin())
                .addValue("payment_tool", payment.getPaymentTool().getFieldName())
                .addValue("currency_code", payment.getCurrencyCode())
                .addValue("payment_system", payment.getPaymentSystem().name())
                .addValue("city_id", payment.getCityId())
                .addValue("country_id", payment.getCountryId())
                .addValue("phone_number", payment.getPhoneNumber())
                .addValue("email", payment.getEmail())
                .addValue("ip", payment.getIp())
                .addValue("created_at", Timestamp.from(payment.getCreatedAt()))
                .addValue("changed_at", Timestamp.from(payment.getChangedAt()))
                .addValue("context", payment.getContext());
        return StorageUtil.validateParams(sqlParameterSource);
    }

    public static RowMapper<Payment> getRowMapper() {
        return (rs, i) -> {
            Payment payment = new Payment();
            payment.setId(rs.getString("payment_id"));
            payment.setEventId(rs.getLong("event_id"));
            payment.setInvoiceId(rs.getString("invoice_id"));
            payment.setMerchantId(rs.getString("merchant_id"));
            payment.setShopId(rs.getInt("shop_id"));
            payment.setCustomerId(rs.getString("customer_id"));
            payment.setMaskedPan(rs.getString("masked_pan"));
            payment.setStatus(InvoicePaymentStatus._Fields.findByName(rs.getString("status")));
            payment.setFailureCode(rs.getString("failure_code"));
            payment.setFailureDescription(rs.getString("failure_description"));
            payment.setAmount(rs.getLong("amount"));
            payment.setFee(rs.getLong("fee"));
            payment.setToken(rs.getString("token"));
            payment.setSessionId(rs.getString("session_id"));
            payment.setBin(rs.getString("bin"));
            payment.setPaymentTool(PaymentTool._Fields.findByName(rs.getString("payment_tool")));
            payment.setCurrencyCode(rs.getString("currency_code"));
            payment.setPaymentSystem(BankCardPaymentSystem.valueOf(rs.getString("payment_system")));
            payment.setCityId(rs.getInt("city_id"));
            payment.setCountryId(rs.getInt("country_id"));
            payment.setPhoneNumber(rs.getString("phone_number"));
            payment.setEmail(rs.getString("email"));
            payment.setIp(rs.getString("ip"));
            payment.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            payment.setChangedAt(rs.getTimestamp("changed_at").toInstant());
            payment.setContext(rs.getBytes("context"));

            return payment;
        };
    }
}
