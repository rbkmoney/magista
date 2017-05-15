package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.magista.event.EventType;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.InvoiceEvent;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Created by tolkonepiu on 10/05/2017.
 */
public class InvoiceEventDaoImpl extends NamedParameterJdbcDaoSupport implements InvoiceEventDao {

    public InvoiceEventDaoImpl(DataSource ds) {
        setDataSource(ds);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        try {
            return getJdbcTemplate().queryForObject("select max(event_id) from mst.invoice_event", Long.class);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public InvoiceEvent findPaymentByInvoiceAndPaymentId(String invoiceId, String paymentId) throws DaoException {
        String request = "select * from mst.invoice_event where invoice_id = :invoice_id and payment_id = :payment_id " +
                "order by event_id desc limit 1";
        InvoiceEvent invoiceEvent;
        try {
            MapSqlParameterSource source = new MapSqlParameterSource()
                    .addValue("invoice_id", invoiceId)
                    .addValue("payment_id", paymentId);
            invoiceEvent = getNamedParameterJdbcTemplate().queryForObject(
                    request,
                    source,
                    getRowMapper()
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
        return invoiceEvent;
    }

    @Override
    public InvoiceEvent findInvoiceById(String invoiceId) throws DaoException {
        String request = "select * from mst.invoice_event where invoice_id = :invoice_id and payment_id is null " +
                "order by event_id desc limit 1";
        InvoiceEvent invoiceEvent;
        try {
            MapSqlParameterSource source = new MapSqlParameterSource()
                    .addValue("invoice_id", invoiceId);
            invoiceEvent = getNamedParameterJdbcTemplate().queryForObject(
                    request,
                    source,
                    getRowMapper()
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
        return invoiceEvent;
    }

    public static RowMapper<InvoiceEvent> getRowMapper() {
        return (rs, i) -> {
            InvoiceEvent invoiceEvent = new InvoiceEvent();
            invoiceEvent.setEventId(rs.getLong("event_id"));
            invoiceEvent.setMerchantId(rs.getString("merchant_id"));
            invoiceEvent.setShopId(rs.getInt("shop_id"));
            invoiceEvent.setEventType(EventType.valueOf(rs.getString("event_type")));
            invoiceEvent.setEventCreatedAt(rs.getObject("event_created_at", LocalDateTime.class)
                    .toInstant(ZoneOffset.UTC));
            invoiceEvent.setInvoiceId(rs.getString("invoice_id"));
            invoiceEvent.setInvoiceStatus(InvoiceStatus._Fields.findByName(rs.getString("invoice_status")));
            invoiceEvent.setInvoiceAmount(rs.getLong("invoice_amount"));
            invoiceEvent.setInvoiceCurrencyCode(rs.getString("invoice_currency_code"));
            invoiceEvent.setInvoiceCreatedAt(rs.getObject("invoice_created_at", LocalDateTime.class)
                    .toInstant(ZoneOffset.UTC));
            invoiceEvent.setPaymentId(rs.getString("payment_id"));
            invoiceEvent.setPaymentStatus(InvoicePaymentStatus._Fields.findByName(rs.getString("payment_status")));
            invoiceEvent.setPaymentAmount(rs.getLong("payment_amount"));
            invoiceEvent.setPaymentFee(rs.getLong("payment_fee"));
            invoiceEvent.setPaymentSystem(rs.getString("payment_system") != null ?
                    BankCardPaymentSystem.valueOf(rs.getString("payment_system")) : null);
            invoiceEvent.setPaymentCountryId(rs.getInt("payment_country_id"));
            invoiceEvent.setPaymentCityId(rs.getInt("payment_city_id"));
            invoiceEvent.setPaymentIp(rs.getString("payment_ip"));
            invoiceEvent.setPaymentPhoneNumber(rs.getString("payment_phone_number"));
            invoiceEvent.setPaymentEmail(rs.getString("payment_email"));
            invoiceEvent.setPaymentFingerprint(rs.getString("payment_fingerprint"));
            LocalDateTime paymentCreatedAt = rs.getObject("payment_created_at", LocalDateTime.class);
            if (paymentCreatedAt != null) {
                invoiceEvent.setPaymentCreatedAt(paymentCreatedAt.toInstant(ZoneOffset.UTC));
            }
            return invoiceEvent;
        };
    }

    @Override
    public void insert(InvoiceEvent invoiceEvent) throws DaoException {
        String request = "insert into mst.invoice_event (event_id, merchant_id, shop_id, event_type, event_created_at, invoice_id, invoice_status, invoice_amount, invoice_currency_code, invoice_created_at, payment_id, payment_status, payment_amount, payment_fee, payment_system, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at)" +
                "values(:event_id, :merchant_id, :shop_id, :event_type, :event_created_at, :invoice_id, :invoice_status, :invoice_amount, :invoice_currency_code, :invoice_created_at, :payment_id, :payment_status, :payment_amount, :payment_fee, :payment_system, :payment_country_id, :payment_city_id, :payment_ip, :payment_phone_number, :payment_email, :payment_fingerprint, :payment_created_at)";
        try {
            int rowsAffected = getNamedParameterJdbcTemplate().update(request, createSqlParameterSource(invoiceEvent));

            if (rowsAffected != 1) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(request, 1, rowsAffected);
            }
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    private SqlParameterSource createSqlParameterSource(InvoiceEvent invoiceEvent) {
        return new MapSqlParameterSource()
                .addValue("event_id", invoiceEvent.getEventId())
                .addValue("merchant_id", invoiceEvent.getMerchantId())
                .addValue("shop_id", invoiceEvent.getShopId())
                .addValue("event_type", invoiceEvent.getEventType(), Types.VARCHAR)
                .addValue("event_created_at", LocalDateTime.ofInstant(invoiceEvent.getEventCreatedAt(), ZoneOffset.UTC), Types.OTHER)
                .addValue("invoice_id", invoiceEvent.getInvoiceId())
                .addValue("invoice_status",
                        invoiceEvent.getInvoiceStatus() != null ? invoiceEvent.getInvoiceStatus().getFieldName() : null)
                .addValue("invoice_amount", invoiceEvent.getInvoiceAmount())
                .addValue("invoice_currency_code", invoiceEvent.getInvoiceCurrencyCode())
                .addValue("invoice_created_at", LocalDateTime.ofInstant(invoiceEvent.getInvoiceCreatedAt(), ZoneOffset.UTC), Types.OTHER)
                .addValue("payment_id", invoiceEvent.getPaymentId())
                .addValue("payment_status",
                        invoiceEvent.getPaymentStatus() != null ? invoiceEvent.getPaymentStatus().getFieldName() : null)
                .addValue("payment_amount", invoiceEvent.getPaymentAmount())
                .addValue("payment_fee", invoiceEvent.getPaymentFee())
                .addValue("payment_system", invoiceEvent.getPaymentSystem())
                .addValue("payment_country_id", invoiceEvent.getPaymentCountryId())
                .addValue("payment_city_id", invoiceEvent.getPaymentCityId())
                .addValue("payment_ip", invoiceEvent.getPaymentIp())
                .addValue("payment_phone_number", invoiceEvent.getPaymentPhoneNumber())
                .addValue("payment_email", invoiceEvent.getPaymentEmail())
                .addValue("payment_fingerprint", invoiceEvent.getPaymentFingerprint())
                .addValue("payment_created_at", LocalDateTime.ofInstant(invoiceEvent.getPaymentCreatedAt(), ZoneOffset.UTC), Types.OTHER);
    }
}
