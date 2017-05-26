package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.util.StorageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;
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
        String sql = "SELECT * FROM mst.invoice WHERE id = :id";

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

        String updateSql = "insert into mst.invoice (id, event_id, merchant_id, shop_id, status, status_details, amount, product, description, currency_code, created_at, due, changed_at, context) " +
                "values (:id, :event_id, :merchant_id, :shop_id, :status, :status_details, :amount, :product, :description, :currency_code, :created_at, :due, :changed_at, :context)";

        execute(updateSql, createSqlParameterSource(invoice));
    }

    @Override
    public void update(Invoice invoice) throws DaoException {
        String updateSql = "update mst.invoice set " +
                "id = :id, event_id = :event_id, merchant_id = :merchant_id, shop_id = :shop_id," +
                " status = :status, status_details = :status_details, amount = :amount, product = :product, description = :description, currency_code = :currency_code," +
                " created_at = :created_at, due = :due, changed_at = :changed_at, context = :context where id = :id";

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
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", invoice.getId())
                .addValue("event_id", invoice.getEventId())
                .addValue("merchant_id", invoice.getMerchantId())
                .addValue("shop_id", invoice.getShopId())
                .addValue("status", invoice.getStatus().getFieldName())
                .addValue("status_details", invoice.getStatusDetails())
                .addValue("amount", invoice.getAmount())
                .addValue("product", invoice.getProduct())
                .addValue("description", invoice.getDescription())
                .addValue("currency_code", invoice.getCurrencyCode())
                .addValue("created_at", Timestamp.from(invoice.getCreatedAt()))
                .addValue("due", Timestamp.from(invoice.getDue()))
                .addValue("changed_at", Timestamp.from(invoice.getChangedAt()))
                .addValue("context", invoice.getContext());
        return StorageUtil.validateParams(sqlParameterSource);
    }

    public static RowMapper<Invoice> getRowMapper() {
        return (rs, i) -> {
            Invoice invoice = new Invoice();
            invoice.setId(rs.getString("id"));
            invoice.setEventId(rs.getLong("event_id"));
            invoice.setMerchantId(rs.getString("merchant_id"));
            invoice.setShopId(rs.getInt("shop_id"));
            invoice.setStatus(InvoiceStatus._Fields.findByName(rs.getString("status")));
            invoice.setStatusDetails(rs.getString("status_details"));
            invoice.setAmount(rs.getLong("amount"));
            invoice.setProduct(rs.getString("product"));
            invoice.setDescription(rs.getString("description"));
            invoice.setCurrencyCode(rs.getString("currency_code"));
            invoice.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            invoice.setDue(rs.getTimestamp("due").toInstant());
            invoice.setChangedAt(rs.getTimestamp("changed_at").toInstant());
            invoice.setContext(rs.getBytes("context"));

            return invoice;
        };
    }
}
