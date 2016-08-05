package com.rbkmoney.magista.repository;

import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public class InvoiceRepositoryImpl implements InvoiceRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public InvoiceRepositoryImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void changeStatus(String invoiceId, InvoiceStatus._Fields status) throws DaoException {
        try {
            jdbcTemplate.update("update magista.invoice set status = ? where id = ?", status.getFieldName(), invoiceId);
        } catch (NestedRuntimeException ex) {
            throw new DaoException("Failed to change invoice status", ex);
        }
    }

    @Override
    public void save(Invoice invoice) throws DaoException {
        try {
            jdbcTemplate.update("insert into magista.invoice (id, status, created_at) values (?, ?, ?)", invoice.getId(), invoice.getStatus().getFieldName(), invoice.getCreatedAt());
        } catch (NestedRuntimeException ex) {
            throw new DaoException("Failed to save invoice event", ex);
        }
    }


}
