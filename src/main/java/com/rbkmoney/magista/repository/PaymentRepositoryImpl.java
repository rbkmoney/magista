package com.rbkmoney.magista.repository;

import com.rbkmoney.magista.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
    public void save(Payment payment) throws DaoException {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(payment);
        try {
            namedParameterJdbcTemplate.update(
                    "insert into magista.payment (id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, amount, currency_code, payment_system, city_name, ip, created_at) " +
                            "values (:id, :event_id, :invoice_id, :merchant_id, :shop_id, :customer_id, :masked_pan, :status, :amount, :currency_code, :payment_system, :city_name, :ip, :created_at)",
                    parameterSource);
        } catch (NestedRuntimeException ex) {
            throw new DaoException("Failed to save payment event", ex);
        }
    }
}
