package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.time.LocalDateTime;

import static com.rbkmoney.magista.domain.Tables.INVOICE_EVENT_STAT;

/**
 * Created by tolkonepiu on 26/05/2017.
 */
public class InvoiceEventDaoImpl extends AbstractDao implements InvoiceEventDao {

    private final DSLContext dslContext;

    public InvoiceEventDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.POSTGRES_9_5);
        this.dslContext = DSL.using(configuration);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        Query query = dslContext.select(INVOICE_EVENT_STAT.EVENT_ID.max()).from(INVOICE_EVENT_STAT);
        return fetchOne(query, Long.class, getNamedParameterJdbcTemplate());
    }

    @Override
    public InvoiceEventStat findPaymentByInvoiceAndPaymentId(String invoiceId, String paymentId) throws DaoException {
        Query query = dslContext.selectFrom(INVOICE_EVENT_STAT)
                .where(INVOICE_EVENT_STAT.INVOICE_ID.eq(invoiceId)
                        .and(INVOICE_EVENT_STAT.PAYMENT_ID.eq(paymentId))
                        .and(INVOICE_EVENT_STAT.EVENT_CATEGORY.eq(InvoiceEventCategory.PAYMENT)));
        return fetchOne(query, getRowMapper(), getNamedParameterJdbcTemplate());
    }

    @Override
    public InvoiceEventStat findInvoiceById(String invoiceId) throws DaoException {
        Query query = dslContext.selectFrom(INVOICE_EVENT_STAT)
                .where(INVOICE_EVENT_STAT.INVOICE_ID.eq(invoiceId)
                        .and(INVOICE_EVENT_STAT.EVENT_CATEGORY.eq(InvoiceEventCategory.INVOICE)));
        return fetchOne(query, getRowMapper(), getNamedParameterJdbcTemplate());
    }

    @Override
    public void insert(InvoiceEventStat invoiceEventStat) throws DaoException {
        Query query = dslContext.insertInto(INVOICE_EVENT_STAT)
                .set(dslContext.newRecord(INVOICE_EVENT_STAT, invoiceEventStat));

        execute(query, getNamedParameterJdbcTemplate());
    }

    @Override
    public void update(InvoiceEventStat invoiceEventStat) throws DaoException {
        Query query = dslContext.update(INVOICE_EVENT_STAT)
                .set(dslContext.newRecord(INVOICE_EVENT_STAT, invoiceEventStat))
                .where(INVOICE_EVENT_STAT.INVOICE_ID.eq(invoiceEventStat.getInvoiceId()))
                .and(
                        INVOICE_EVENT_STAT.PAYMENT_ID.isNull()
                                .or(INVOICE_EVENT_STAT.PAYMENT_ID.eq(invoiceEventStat.getPaymentId()))
                );

        execute(query, getNamedParameterJdbcTemplate());
    }


    @Override
    public RowMapper<InvoiceEventStat> getRowMapper() {
        return (rs, i) -> {
            InvoiceEventStat invoiceEventStat = new InvoiceEventStat();
            invoiceEventStat.setEventId(rs.getLong("event_id"));
            invoiceEventStat.setEventCategory(InvoiceEventCategory.valueOf(rs.getString("event_category")));
            invoiceEventStat.setEventType(InvoiceEventType.valueOf(rs.getString("event_type")));
            invoiceEventStat.setEventCreatedAt(rs.getObject("event_created_at", LocalDateTime.class));
            invoiceEventStat.setPartyId(rs.getString("party_id"));
            invoiceEventStat.setPartyEmail(rs.getString("party_email"));
            invoiceEventStat.setPartyShopId(rs.getInt("party_shop_id"));
            invoiceEventStat.setPartyShopName(rs.getString("party_shop_name"));
            invoiceEventStat.setPartyShopDescription(rs.getString("party_shop_description"));
            invoiceEventStat.setPartyShopUrl(rs.getString("party_shop_url"));
            invoiceEventStat.setPartyShopCategoryId(rs.getInt("party_shop_category_id"));
            invoiceEventStat.setPartyShopPayoutToolId(rs.getInt("party_shop_payout_tool_id"));
            invoiceEventStat.setPartyContractId(rs.getInt("party_contract_id"));
            invoiceEventStat.setPartyContractRegisteredNumber(rs.getString("party_contract_registered_number"));
            invoiceEventStat.setPartyContractInn(rs.getString("party_contract_inn"));
            invoiceEventStat.setInvoiceId(rs.getString("invoice_id"));
            invoiceEventStat.setInvoiceStatus(InvoiceStatus.valueOf(rs.getString("invoice_status")));
            invoiceEventStat.setInvoiceStatusDetails(rs.getString("invoice_status_details"));
            invoiceEventStat.setInvoiceProduct(rs.getString("invoice_product"));
            invoiceEventStat.setInvoiceDescription(rs.getString("invoice_description"));
            invoiceEventStat.setInvoiceAmount(rs.getLong("invoice_amount"));
            invoiceEventStat.setInvoiceCurrencyCode(rs.getString("invoice_currency_code"));
            invoiceEventStat.setInvoiceDue(rs.getObject("invoice_due", LocalDateTime.class));
            invoiceEventStat.setInvoiceCreatedAt(rs.getObject("invoice_created_at", LocalDateTime.class));
            invoiceEventStat.setInvoiceContext(rs.getBytes("invoice_context"));
            invoiceEventStat.setPaymentId(rs.getString("payment_id"));
            invoiceEventStat.setPaymentStatus(InvoicePaymentStatus.valueOf(rs.getString("payment_status")));
            invoiceEventStat.setPaymentStatusFailureCode(rs.getString("payment_status_failure_code"));
            invoiceEventStat.setPaymentStatusFailureDescription(rs.getString("payment_status_failure_description"));
            invoiceEventStat.setPaymentAmount(rs.getLong("payment_amount"));
            invoiceEventStat.setPaymentCurrencyCode(rs.getString("payment_currency_code"));
            invoiceEventStat.setPaymentFee(rs.getLong("payment_fee"));
            invoiceEventStat.setPaymentTool(rs.getString("payment_tool"));
            invoiceEventStat.setPaymentMaskedPan(rs.getString("payment_masked_pan"));
            invoiceEventStat.setPaymentBin(rs.getString("payment_bin"));
            invoiceEventStat.setPaymentToken(rs.getString("payment_token"));
            invoiceEventStat.setPaymentSystem(rs.getString("payment_system"));
            invoiceEventStat.setPaymentSessionId(rs.getString("payment_session_id"));
            invoiceEventStat.setPaymentCountryId(rs.getInt("payment_country_id"));
            invoiceEventStat.setPaymentCityId(rs.getInt("payment_city_id"));
            invoiceEventStat.setPaymentIp(rs.getString("payment_ip"));
            invoiceEventStat.setPaymentPhoneNumber(rs.getString("payment_phone_number"));
            invoiceEventStat.setPaymentEmail(rs.getString("payment_email"));
            invoiceEventStat.setPaymentFingerprint(rs.getString("payment_fingerprint"));
            invoiceEventStat.setPaymentCreatedAt(rs.getObject("payment_created_at", LocalDateTime.class));
            invoiceEventStat.setPaymentContext(rs.getBytes("payment_context"));
            return invoiceEventStat;
        };
    }
}
