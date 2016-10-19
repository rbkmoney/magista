package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Invoice;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TSimpleJSONProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;

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
            log.trace("SQL: {}, Params: {}", sql, source.getValues());
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

        String updateSql = "insert into mst.invoice (id, event_id, merchant_id, shop_id, status, amount, currency_code, created_at, changed_at, model, data) " +
                "values (:id, :event_id, :merchant_id, :shop_id, :status, :amount, :currency_code, :created_at, :changed_at, :model, :data)";

        execute(updateSql, createSqlParameterSource(invoice));
    }

    @Override
    public void update(Invoice invoice) throws DaoException {
        String updateSql = "update mst.invoice set " +
                "id = :id, event_id = :event_id, merchant_id = :merchant_id, shop_id = :shop_id," +
                " status = :status, amount = :amount, currency_code = :currency_code," +
                " created_at = :created_at, changed_at = :changed_at, model = :model, data = :data where id = :id";

        execute(updateSql, createSqlParameterSource(invoice));
    }

    public void execute(String updateSql, MapSqlParameterSource source) throws DaoException {
        try {
            log.trace("SQL: {}, Params: {}", updateSql, source.getValues());
            int rowsAffected = getNamedParameterJdbcTemplate().update(updateSql, source);

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
                    .addValue("id", invoice.getId())
                    .addValue("event_id", invoice.getEventId())
                    .addValue("merchant_id", invoice.getMerchantId())
                    .addValue("shop_id", invoice.getShopId())
                    .addValue("status", invoice.getStatus().getFieldName())
                    .addValue("amount", invoice.getAmount())
                    .addValue("currency_code", invoice.getCurrencyCode())
                    .addValue("created_at", Timestamp.from(invoice.getCreatedAt()))
                    .addValue("changed_at", Timestamp.from(invoice.getChangedAt()))
                    .addValue("model", new TSerializer(new TJSONProtocol.Factory()).toString(invoice.getModel(), StandardCharsets.UTF_8.name()))
                    .addValue("data", new TSerializer(new TSimpleJSONProtocol.Factory()).toString(invoice.getModel(), StandardCharsets.UTF_8.name()));
        } catch (TException ex) {
            throw new DaoException("Failed to serialize invoice model", ex);
        }
    }

    public static RowMapper<Invoice> getRowMapper() {
        return (rs, i) -> {
            try {
                Invoice invoice = new Invoice();
                invoice.setId(rs.getString("id"));
                invoice.setEventId(rs.getLong("event_id"));
                invoice.setMerchantId(rs.getString("merchant_id"));
                invoice.setShopId(rs.getString("shop_id"));
                invoice.setStatus(InvoiceStatus._Fields.findByName(rs.getString("status")));
                invoice.setAmount(rs.getLong("amount"));
                invoice.setCurrencyCode(rs.getString("currency_code"));
                invoice.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                invoice.setChangedAt(rs.getTimestamp("changed_at").toInstant());
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
