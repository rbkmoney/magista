package com.rbkmoney.magista.repository;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.magista.model.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
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
        Map<String, Object> params = new HashMap<>();
        params.put("id", invoice.getId());
        params.put("event_id", invoice.getEventId());
        params.put("merchant_id", invoice.getMerchantId());
        params.put("shop_id", invoice.getShopId());
        params.put("status", invoice.getStatus().getFieldName());
        params.put("amount", invoice.getAmount());
        params.put("currency_code", invoice.getCurrencyCode());
        params.put("created_at", Timestamp.from(invoice.getCreatedAt()));

        try {
            namedParameterJdbcTemplate.update(
                    "insert into mst.invoice (id, event_id, merchant_id, shop_id, status, amount, currency_code, created_at) " +
                            "values (:id, :event_id, :merchant_id, :shop_id, :status, :amount, :currency_code, :created_at)",
                    params);
        } catch (NestedRuntimeException ex) {
            throw new DaoException("Failed to save invoice event", ex);
        }
    }


}
