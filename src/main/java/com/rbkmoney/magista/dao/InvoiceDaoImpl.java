package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.geck.serializer.kit.msgpack.MsgPackHandler;
import com.rbkmoney.geck.serializer.kit.msgpack.MsgPackProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseProcessor;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Invoice;
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
public class InvoiceDaoImpl extends NamedParameterJdbcDaoSupport implements InvoiceDao {

    public InvoiceDaoImpl(DataSource ds) {
        setDataSource(ds);
    }

    @Override
    public Invoice findById(String invoiceId) throws DaoException {
        String sql = "select * from mst.invoice where invoice_id = :invoice_id order by event_id desc limit 1";

        Invoice invoice;

        MapSqlParameterSource source = new MapSqlParameterSource("invoice_id", invoiceId);
        try {
            invoice = getNamedParameterJdbcTemplate().queryForObject(
                    sql,
                    source,
                    getRowMapper()
            );
        } catch (EmptyResultDataAccessException ex) {
            invoice = null;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
        return invoice;
    }

    @Override
    public void insert(Invoice invoice) throws DaoException {
        String updateSql = "insert into mst.invoice (invoice_id, event_id, merchant_id, shop_id, status, amount, currency_code, created_at, changed_at, model) " +
                "values (:invoice_id, :event_id, :merchant_id, :shop_id, :status::invoice_status, :amount, :currency_code, :created_at, :changed_at, :model)";

        try {
            int rowsAffected = getNamedParameterJdbcTemplate()
                    .update(updateSql, createSqlParameterSource(invoice));

            if (rowsAffected != 1) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(updateSql, 1, rowsAffected);
            }
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    private MapSqlParameterSource createSqlParameterSource(Invoice invoice) {
        try {
            return new MapSqlParameterSource()
                    .addValue("invoice_id", invoice.getId())
                    .addValue("event_id", invoice.getEventId())
                    .addValue("merchant_id", invoice.getMerchantId())
                    .addValue("shop_id", invoice.getShopId())
                    .addValue("status", invoice.getStatus().getFieldName())
                    .addValue("amount", invoice.getAmount())
                    .addValue("currency_code", invoice.getCurrencyCode())
                    .addValue("created_at", LocalDateTime.ofInstant(invoice.getCreatedAt(), ZoneOffset.UTC),
                            Types.OTHER)
                    .addValue("changed_at", LocalDateTime.ofInstant(invoice.getChangedAt(), ZoneOffset.UTC),
                            Types.OTHER)
                    .addValue("model", new TBaseProcessor().process(invoice.getModel(),
                            MsgPackHandler.newBufferedInstance(true)));
        } catch (IOException ex) {
            throw new DaoException("Failed to deserialize invoice model", ex);
        }
    }

    public static RowMapper<Invoice> getRowMapper() {
        return (rs, i) -> {
            try {
                Invoice invoice = new Invoice();
                invoice.setId(rs.getString("invoice_id"));
                invoice.setEventId(rs.getLong("event_id"));
                invoice.setMerchantId(rs.getString("merchant_id"));
                invoice.setShopId(rs.getInt("shop_id"));
                invoice.setStatus(InvoiceStatus._Fields.findByName(rs.getString("status")));
                invoice.setAmount(rs.getLong("amount"));
                invoice.setCurrencyCode(rs.getString("currency_code"));
                invoice.setCreatedAt(rs.getObject("created_at", LocalDateTime.class)
                        .toInstant(ZoneOffset.UTC));
                invoice.setChangedAt(rs.getObject("changed_at", LocalDateTime.class)
                        .toInstant(ZoneOffset.UTC));
                com.rbkmoney.damsel.domain.Invoice model =
                        MsgPackProcessor.newBinaryInstance().process(rs.getBytes("model"),
                                new TBaseHandler<>(com.rbkmoney.damsel.domain.Invoice.class));
                invoice.setModel(model);

                return invoice;
            } catch (IOException ex) {
                throw new SQLException("Failed to deserialize invoice model", ex);
            }
        };
    }
}
