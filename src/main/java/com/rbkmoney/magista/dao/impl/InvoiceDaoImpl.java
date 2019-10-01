package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.magista.dao.InvoiceDao;
import com.rbkmoney.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.records.InvoiceDataRecord;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.magista.domain.Tables.INVOICE_DATA;
import static com.rbkmoney.magista.domain.Tables.PAYMENT_DATA;

@Component
public class InvoiceDaoImpl extends AbstractDao implements InvoiceDao {

    private final RowMapper<InvoiceData> invoiceDataRowMapper;

    @Autowired
    public InvoiceDaoImpl(DataSource dataSource) {
        super(dataSource);
        invoiceDataRowMapper = new RecordRowMapper<>(INVOICE_DATA, InvoiceData.class);
    }

    @Override
    public InvoiceData get(String invoiceId) throws DaoException {
        Query query = getDslContext().selectFrom(INVOICE_DATA)
                .where(INVOICE_DATA.INVOICE_ID.eq(invoiceId));
        return fetchOne(query, invoiceDataRowMapper);
    }

    @Override
    public void insert(List<InvoiceData> invoices) throws DaoException {
        List<Query> queries = invoices.stream()
                .map(
                        invoiceData -> {
                            InvoiceDataRecord invoiceDataRecord = getDslContext().newRecord(INVOICE_DATA, invoiceData);
                            invoiceDataRecord.changed(true);
                            invoiceDataRecord.changed(INVOICE_DATA.ID, invoiceDataRecord.getId() != null);
                            return invoiceDataRecord;
                        }
                )
                .map(
                        invoiceDataRecord ->
                                getDslContext().insertInto(INVOICE_DATA)
                                        .set(invoiceDataRecord)
                                        .onConflict(INVOICE_DATA.INVOICE_ID)
                                        .doNothing()
                )
                .collect(Collectors.toList());
        batchExecute(queries);
    }

    @Override
    public void update(List<InvoiceData> invoices) throws DaoException {
        List<Query> queries = invoices.stream()
                .map(
                        invoiceData -> {
                            InvoiceDataRecord invoiceDataRecord = getDslContext().newRecord(INVOICE_DATA, invoiceData);
                            invoiceDataRecord.changed(true);
                            invoiceDataRecord.changed(INVOICE_DATA.ID, invoiceDataRecord.getId() != null);
                            return invoiceDataRecord;
                        }
                )
                .map(
                        invoiceDataRecord ->
                                getDslContext().update(INVOICE_DATA)
                                        .set(invoiceDataRecord)
                                        .where(
                                                INVOICE_DATA.INVOICE_ID.eq(invoiceDataRecord.getInvoiceId())
                                                        .and(INVOICE_DATA.EVENT_ID.le(invoiceDataRecord.getEventId()))
                                        )
                )
                .collect(Collectors.toList());
        batchExecute(queries);
    }

}
