package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.magista.dao.PaymentDao;
import com.rbkmoney.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.records.PaymentDataRecord;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.magista.domain.Tables.PAYMENT_DATA;

@Component
public class PaymentDaoImpl extends AbstractDao implements PaymentDao {

    private final RowMapper<PaymentData> paymentDataRowMapper;

    @Autowired
    public PaymentDaoImpl(DataSource dataSource) {
        super(dataSource);
        this.paymentDataRowMapper = new RecordRowMapper<>(PAYMENT_DATA, PaymentData.class);
    }

    @Override
    public PaymentData get(String invoiceId, String paymentId) throws DaoException {
        Query query = getDslContext().selectFrom(PAYMENT_DATA)
                .where(PAYMENT_DATA.INVOICE_ID.eq(invoiceId))
                .and(PAYMENT_DATA.PAYMENT_ID.eq(paymentId));
        return fetchOne(query, paymentDataRowMapper);
    }

    @Override
    public void save(PaymentData paymentData) throws DaoException {
        PaymentDataRecord paymentDataRecord = getDslContext().newRecord(PAYMENT_DATA, paymentData);
        Query query = getDslContext().insertInto(PAYMENT_DATA)
                .set(paymentDataRecord)
                .onConflict(PAYMENT_DATA.INVOICE_ID, PAYMENT_DATA.PAYMENT_ID)
                .doUpdate()
                .set(paymentDataRecord);
        executeOne(query);
    }

}
