package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Condition;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import static com.rbkmoney.magista.domain.Tables.INVOICE_EVENT_STAT;

/**
 * Created by tolkonepiu on 26/05/2017.
 */
public class InvoiceEventDaoImpl extends AbstractDao implements InvoiceEventDao {

    public static final RowMapper<InvoiceEventStat> ROW_MAPPER = new RecordRowMapper<>(INVOICE_EVENT_STAT, InvoiceEventStat.class);

    public InvoiceEventDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        Query query = getDslContext().select(INVOICE_EVENT_STAT.EVENT_ID.max()).from(INVOICE_EVENT_STAT);
        return fetchOne(query, Long.class);
    }

    @Override
    public InvoiceEventStat findPaymentByInvoiceAndPaymentId(String invoiceId, String paymentId) throws DaoException {
        Query query = getDslContext().selectFrom(INVOICE_EVENT_STAT)
                .where(INVOICE_EVENT_STAT.INVOICE_ID.eq(invoiceId)
                        .and(INVOICE_EVENT_STAT.PAYMENT_ID.eq(paymentId))
                        .and(INVOICE_EVENT_STAT.EVENT_CATEGORY.eq(InvoiceEventCategory.PAYMENT)));
        return fetchOne(query, ROW_MAPPER);
    }

    @Override
    public InvoiceEventStat findInvoiceById(String invoiceId) throws DaoException {
        Query query = getDslContext().selectFrom(INVOICE_EVENT_STAT)
                .where(INVOICE_EVENT_STAT.INVOICE_ID.eq(invoiceId)
                        .and(INVOICE_EVENT_STAT.EVENT_CATEGORY.eq(InvoiceEventCategory.INVOICE)));
        return fetchOne(query, ROW_MAPPER);
    }

    @Override
    public void insert(InvoiceEventStat invoiceEventStat) throws DaoException {
        Query query = getDslContext().insertInto(INVOICE_EVENT_STAT)
                .set(getDslContext().newRecord(INVOICE_EVENT_STAT, invoiceEventStat));

        executeOne(query);
    }

    @Override
    public void update(InvoiceEventStat invoiceEventStat) throws DaoException {
        Condition condition;
        if (invoiceEventStat.getEventCategory() == InvoiceEventCategory.INVOICE) {
            condition = INVOICE_EVENT_STAT.PAYMENT_ID.isNull();
        } else {
            condition = INVOICE_EVENT_STAT.PAYMENT_ID.eq(invoiceEventStat.getPaymentId());
        }

        Query query = getDslContext().update(INVOICE_EVENT_STAT)
                .set(getDslContext().newRecord(INVOICE_EVENT_STAT, invoiceEventStat))
                .where(INVOICE_EVENT_STAT.INVOICE_ID.eq(invoiceEventStat.getInvoiceId()))
                .and(condition);

        executeOne(query);
    }

}
