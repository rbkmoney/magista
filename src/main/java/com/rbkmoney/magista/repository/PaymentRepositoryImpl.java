package com.rbkmoney.magista.repository;

import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.magista.model.Payment;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TJSONProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tolkonepiu on 04.08.16.
 */
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public PaymentRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Payment findById(String id) throws DaoException {
        Payment invoice;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            invoice = namedParameterJdbcTemplate.queryForObject(
                    "SELECT id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, amount, currency_code, payment_system, city_name, ip, created_at, model from mst.payment where id = :id",
                    params,
                    getRowMapper()
            );
        } catch (NestedRuntimeException ex) {
            String message = String.format("Failed to find payment by id '%s'", id);
            throw new DaoException(message, ex);
        }
        return invoice;
    }

    @Override
    public void changeStatus(String paymentId, InvoicePaymentStatus status) throws DaoException {
        try {
            Payment payment = findById(paymentId);

            InvoicePayment invoicePayment = payment.getModel();
            invoicePayment.setStatus(status);

            Map<String, Object> params = new HashMap<>();
            params.put("status", status.getSetField().name());
            params.put("model", new TSerializer(new TJSONProtocol.Factory()).toString(payment.getModel(), StandardCharsets.UTF_8.name()));
            params.put("id", paymentId);

            namedParameterJdbcTemplate.update(
                    "update mst.payment set status = :status, model = :model where id = :id",
                    params);
        } catch (TException | NestedRuntimeException ex) {
            String message = String.format("Failed to change payment status to '%s', payment id '%s'", status.getSetField().name(), paymentId);
            throw new DaoException(message, ex);
        }
    }

    @Override
    public void save(Payment payment) throws DaoException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", payment.getId());
            params.put("event_id", payment.getEventId());
            params.put("invoice_id", payment.getInvoiceId());
            params.put("merchant_id", payment.getMerchantId());
            params.put("shop_id", payment.getShopId());
            params.put("customer_id", payment.getCustomerId());
            params.put("masked_pan", payment.getMaskedPan());
            params.put("status", payment.getStatus().name());
            params.put("amount", payment.getAmount());
            params.put("currency_code", payment.getCurrencyCode());
            params.put("payment_system", payment.getPaymentSystem().name());
            params.put("city_name", payment.getCityName());
            params.put("ip", payment.getIp());
            params.put("created_at", Timestamp.from(payment.getCreatedAt()));
            params.put("model", new TSerializer(new TJSONProtocol.Factory()).toString(payment.getModel(), StandardCharsets.UTF_8.name()));

            namedParameterJdbcTemplate.update(
                    "insert into mst.payment (id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, amount, currency_code, payment_system, city_name, ip, created_at, model) " +
                            "values (:id, :event_id, :invoice_id, :merchant_id, :shop_id, :customer_id, :masked_pan, :status, :amount, :currency_code, :payment_system, :city_name, :ip, :created_at, :model)",
                    params);
        } catch (TException | NestedRuntimeException ex) {
            throw new DaoException("Failed to save payment event", ex);
        }
    }

    public static RowMapper<Payment> getRowMapper() {
        return (rs, i) -> {
            Payment payment = new Payment();
            payment.setId(rs.getString("id"));
            payment.setEventId(rs.getLong("event_id"));
            payment.setInvoiceId(rs.getString("invoice_id"));
            payment.setMerchantId(rs.getString("merchant_id"));
            payment.setShopId(rs.getString("shop_id"));
            payment.setCustomerId(rs.getString("customer_id"));
            payment.setMaskedPan(rs.getString("masked_pan"));
            payment.setStatus(InvoicePaymentStatus._Fields.valueOf(rs.getString("status")));
            payment.setAmount(rs.getLong("amount"));
            payment.setCurrencyCode(rs.getString("currency_code"));
            payment.setPaymentSystem(BankCardPaymentSystem.valueOf(rs.getString("payment_system")));
            payment.setCityName(rs.getString("city_name"));
            payment.setIp(rs.getString("ip"));
            payment.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            InvoicePayment model = new InvoicePayment();
            try {
                new TDeserializer(new TJSONProtocol.Factory()).deserialize(model, rs.getBytes("model"));
            } catch (TException ex) {
                throw new DaoException("Failed to deserialize payment model", ex);
            }
            payment.setModel(model);

            return payment;
        };
    }
}
