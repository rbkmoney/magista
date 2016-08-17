package com.rbkmoney.magista.repository;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.magista.model.Invoice;
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
import java.sql.Time;
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
        //TODO: Возвращать null, если пустой ResultSet
        Invoice invoice;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            invoice = namedParameterJdbcTemplate.queryForObject(
                    "SELECT id, event_id, merchant_id, shop_id, status, amount, currency_code, created_at, model from mst.invoice where id = :id",
                    params,
                    getRowMapper()
            );
        } catch (NestedRuntimeException ex) {
            String message = String.format("Failed to find invoice by id '%s'", id);
            throw new DaoException(message, ex);
        }
        return invoice;
    }

    @Override
    public void changeStatus(String invoiceId, InvoiceStatus status) throws DaoException {
        try {
            Invoice invoice = findById(invoiceId);
            com.rbkmoney.damsel.domain.Invoice model = invoice.getModel();
            model.setStatus(status);


            Map<String, Object> params = new HashMap<>();
            params.put("id", invoiceId);
            params.put("status", status.getSetField().getFieldName());
            params.put("model", new TSerializer(new TJSONProtocol.Factory()).toString(model, StandardCharsets.UTF_8.name()));

            namedParameterJdbcTemplate.update(
                    "update mst.invoice set status = :status, model = :model where id = :id",
                    params);
        } catch (TException | NestedRuntimeException ex) {
            String message = String.format("Failed to change invoice status to '%s', invoice id '%s'", status.getSetField().getFieldName(), invoiceId);
            throw new DaoException(message, ex);
        }
    }

    @Override
    public void save(Invoice invoice) throws DaoException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", invoice.getId());
            params.put("event_id", invoice.getEventId());
            params.put("merchant_id", invoice.getMerchantId());
            params.put("shop_id", invoice.getShopId());
            params.put("status", invoice.getStatus().getFieldName());
            params.put("amount", invoice.getAmount());
            params.put("currency_code", invoice.getCurrencyCode());
            params.put("created_at", Timestamp.from(invoice.getCreatedAt()));
            params.put("model", new TSerializer(new TJSONProtocol.Factory()).toString(invoice.getModel(), StandardCharsets.UTF_8.name()));

            namedParameterJdbcTemplate.update(
                    "insert into mst.invoice (id, event_id, merchant_id, shop_id, status, amount, currency_code, created_at, model) " +
                            "values (:id, :event_id, :merchant_id, :shop_id, :status, :amount, :currency_code, :created_at, :model)",
                    params);
        } catch (TException | NestedRuntimeException ex) {
            throw new DaoException("Failed to save invoice event", ex);
        }
    }

    public static RowMapper<Invoice> getRowMapper() {
        return (rs, i) -> {
            Invoice invoice = new Invoice();
            invoice.setId(rs.getString("id"));
            invoice.setEventId(rs.getLong("event_id"));
            invoice.setMerchantId(rs.getString("merchant_id"));
            invoice.setShopId(rs.getString("shop_id"));
            invoice.setStatus(InvoiceStatus._Fields.findByName(rs.getString("status")));
            invoice.setAmount(rs.getLong("amount"));
            invoice.setCurrencyCode(rs.getString("currency_code"));
            invoice.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            com.rbkmoney.damsel.domain.Invoice model = new com.rbkmoney.damsel.domain.Invoice();
            try {
                new TDeserializer(new TJSONProtocol.Factory()).deserialize(model, rs.getBytes("model"));
            } catch (TException ex) {
                throw new DaoException("Failed to deserialize invoice model", ex);
            }
            invoice.setModel(model);

            return invoice;
        };
    }

}
