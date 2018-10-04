package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.magista.dao.InvoiceDao;
import com.rbkmoney.magista.dao.impl.AbstractDao;
import com.rbkmoney.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEvent;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.magista.domain.Tables.INVOICE_DATA;
import static com.rbkmoney.magista.domain.Tables.INVOICE_EVENT;

@Component
public class InvoiceDaoImpl extends AbstractDao implements InvoiceDao {

    private final RowMapper<InvoiceData> invoiceDataRowMapper;

    @Autowired
    public InvoiceDaoImpl(DataSource dataSource) {
        super(dataSource);
        invoiceDataRowMapper = new RecordRowMapper<>(INVOICE_DATA, InvoiceData.class);
    }

    @Override
    public void saveInvoiceData(InvoiceData invoiceData) throws DaoException {
        Query query = getDslContext().insertInto(INVOICE_DATA)
                .set(getDslContext().newRecord(INVOICE_DATA, invoiceData))
                .onDuplicateKeyUpdate()
                .set(getDslContext().newRecord(INVOICE_DATA, invoiceData));
        executeOne(query);
    }

    @Override
    public InvoiceData getInvoiceData(String invoiceId) throws DaoException {
        Query query = getDslContext().selectFrom(INVOICE_DATA)
                .where(INVOICE_DATA.INVOICE_ID.eq(invoiceId));
        return fetchOne(query, invoiceDataRowMapper);
    }

    @Override
    public void saveInvoiceEvent(InvoiceEvent invoiceEvent) throws DaoException {
        Query query = getDslContext().insertInto(INVOICE_EVENT)
                .set(getDslContext().newRecord(INVOICE_EVENT, invoiceEvent));
        executeOne(query);
    }
}
