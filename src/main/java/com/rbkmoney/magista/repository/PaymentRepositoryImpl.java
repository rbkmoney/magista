package com.rbkmoney.magista.repository;

import com.rbkmoney.magista.model.Payment;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by tolkonepiu on 04.08.16.
 */
public class PaymentRepositoryImpl implements PaymentRepository {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public PaymentRepositoryImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public void save(Payment payment) throws DaoException {
        try {
            jdbcTemplate.update("insert into magista.payment (id, invoice_id, status, created_at) values (?, ?, ?, ?)", payment.getId(), payment.getInvoiceId(), payment.getStatus().getFieldName(), payment.getCreatedAt());
        } catch (RuntimeException ex) {
            throw new DaoException("Failed to save payment event", ex);
        }
    }
}
