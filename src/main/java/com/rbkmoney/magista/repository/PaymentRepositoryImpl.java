package com.rbkmoney.magista.repository;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.magista.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
                    "SELECT id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, amount, currency_code, payment_system, city_name, ip, created_at from mst.payment where id = :id",
                    params,
                    BeanPropertyRowMapper.newInstance(Payment.class)
            );
        } catch (NestedRuntimeException ex) {
            String message = String.format("Failed to find payment by id '%s'", id);
            throw new DaoException(message, ex);
        }
        return invoice;
    }

    @Override
    public void changeStatus(String paymentId, InvoicePaymentStatus._Fields status) throws DaoException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", paymentId);
            params.put("status", status.name());
            namedParameterJdbcTemplate.update(
                    "update mst.payment set status = :status where id = :id",
                    params);
        } catch (NestedRuntimeException ex) {
            String message = String.format("Failed to change payment status to '%s', payment id '%s'", status.name(), paymentId);
            throw new DaoException(message, ex);
        }
    }

    @Override
    public void save(Payment payment) throws DaoException {
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

        try {
            namedParameterJdbcTemplate.update(
                    "insert into mst.payment (id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, amount, currency_code, payment_system, city_name, ip, created_at) " +
                            "values (:id, :event_id, :invoice_id, :merchant_id, :shop_id, :customer_id, :masked_pan, :status, :amount, :currency_code, :payment_system, :city_name, :ip, :created_at)",
                    params);
        } catch (NestedRuntimeException ex) {
            throw new DaoException("Failed to save payment event", ex);
        }
    }
}
