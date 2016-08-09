package com.rbkmoney.magista.repository;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.magista.model.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tolkonepiu on 03.08.16.
 */
@Repository
public class InvoiceRepositoryImpl implements InvoiceRepository {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public InvoiceRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Invoice findById(String id) throws DaoException {
        Invoice invoice;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            invoice = namedParameterJdbcTemplate.queryForObject(
                    "SELECT id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, amount, currency_code, payment_system, city_name, ip, created_at from mst.payment where id = :id",
                    params,
                    BeanPropertyRowMapper.newInstance(Invoice.class)
            );
        } catch (NestedRuntimeException ex) {
            String message = String.format("Failed to find invoice by id '%s'", id);
            throw new DaoException(message, ex);
        }
        return invoice;
    }

    @Override
    public void changeStatus(String invoiceId, InvoiceStatus._Fields status) throws DaoException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", invoiceId);
            params.put("status", status.getFieldName());
            namedParameterJdbcTemplate.update(
                    "update mst.invoice set status = :status where id = :id",
                    params);
        } catch (NestedRuntimeException ex) {
            String message = String.format("Failed to change invoice status to '%s', invoice id '%s'", status.getFieldName(), invoiceId);
            throw new DaoException(message, ex);
        }
    }

    @Override
    public void save(Invoice invoice) throws DaoException {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(invoice);
        try {
            namedParameterJdbcTemplate.update(
                    "insert into mst.invoice (id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, amount, currency_code, payment_system, city_name, ip, created_at) " +
                            "values (:id, :event_id, :invoice_id, :merchant_id, :shop_id, :customer_id, :masked_pan, :status, :amount, :currency_code, :payment_system, :city_name, :ip, :created_at)",
                    parameterSource);
        } catch (NestedRuntimeException ex) {
            throw new DaoException("Failed to save invoice event", ex);
        }
    }


}
