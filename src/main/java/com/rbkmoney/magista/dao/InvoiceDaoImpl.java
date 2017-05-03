package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Invoice;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TJSONProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Created by tolkonepiu on 23.08.16.
 */
public class InvoiceDaoImpl extends NamedParameterJdbcDaoSupport implements InvoiceDao {

    Logger log = LoggerFactory.getLogger(this.getClass());

    public InvoiceDaoImpl(DataSource ds) {
        setDataSource(ds);
    }

    @Override
    public Invoice findById(String id) throws DaoException {
        String sql = "SELECT id, event_id, merchant_id, shop_id, status, amount, currency_code, created_at, changed_at, model " +
                "from mst.invoice where id = :id";

        Invoice invoice;

        MapSqlParameterSource source = new MapSqlParameterSource("id", id);
        try {
            invoice = getNamedParameterJdbcTemplate().queryForObject(
                    sql,
                    source,
                    getRowMapper()
            );
            log.trace("Invoice found by id: {}", id);
        } catch (EmptyResultDataAccessException ex) {
            invoice = null;
            log.trace("Invoice not found by id: {}", id);
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
        return invoice;
    }

    @Override
    public void insert(Invoice invoice) throws DaoException {

        String updateSql = "insert into mst.invoice (invoice_id, event_id, merchant_id, shop_id, status, amount, currency_code, created_at) " +
                "values (:invoice_id, :event_id, :merchant_id, :shop_id, :status, :amount, :currency_code, :created_at)";

        execute(updateSql, createSqlParameterSource(invoice));
    }

    public void execute(String updateSql, MapSqlParameterSource source) throws DaoException {
        try {
            int rowsAffected = getNamedParameterJdbcTemplate().update(updateSql, source);

            if (rowsAffected != 1) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(updateSql, 1, rowsAffected);
            }
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    private MapSqlParameterSource createSqlParameterSource(Invoice invoice) {
        return new MapSqlParameterSource()
                .addValue("invoice_id", invoice.getId())
                .addValue("event_id", invoice.getEventId())
                .addValue("merchant_id", invoice.getMerchantId())
                .addValue("shop_id", invoice.getShopId())
                .addValue("status", invoice.getStatus().getFieldName())
                .addValue("amount", invoice.getAmount())
                .addValue("currency_code", invoice.getCurrencyCode())
                .addValue("created_at", invoice.getCreatedAt(), Types.OTHER);
    }

    public static RowMapper<Invoice> getRowMapper() {
        return (rs, i) -> {
            try {
                Invoice invoice = new Invoice();
                invoice.setId(rs.getString("id"));
                invoice.setEventId(rs.getLong("event_id"));
                invoice.setMerchantId(rs.getString("merchant_id"));
                invoice.setShopId(rs.getInt("shop_id"));
                invoice.setStatus(InvoiceStatus._Fields.findByName(rs.getString("status")));
                invoice.setAmount(rs.getLong("amount"));
                invoice.setCurrencyCode(rs.getString("currency_code"));
                invoice.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                com.rbkmoney.damsel.domain.Invoice model = new com.rbkmoney.damsel.domain.Invoice();
                new TDeserializer(new TJSONProtocol.Factory()).deserialize(model, rs.getBytes("model"));
                invoice.setModel(model);

                return invoice;
            } catch (TException ex) {
                throw new SQLException("Failed to deserialize invoice model", ex);
            }
        };
    }
}
