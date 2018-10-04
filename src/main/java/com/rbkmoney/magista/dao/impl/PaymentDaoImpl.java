package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.magista.dao.PaymentDao;
import com.rbkmoney.magista.dao.impl.AbstractDao;
import com.rbkmoney.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentEvent;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.magista.domain.Tables.PAYMENT_DATA;
import static com.rbkmoney.magista.domain.Tables.PAYMENT_EVENT;

@Component
public class PaymentDaoImpl extends AbstractDao implements PaymentDao {

    private final RowMapper<PaymentEvent> paymentEventRowMapper;

    @Autowired
    public PaymentDaoImpl(DataSource dataSource) {
        super(dataSource);
        this.paymentEventRowMapper = new RecordRowMapper<>(PAYMENT_EVENT, PaymentEvent.class);
    }

    @Override
    public void savePaymentData(PaymentData paymentData) throws DaoException {
        Query query = getDslContext().insertInto(PAYMENT_DATA)
                .set(getDslContext().newRecord(PAYMENT_DATA, paymentData))
                .onDuplicateKeyUpdate()
                .set(getDslContext().newRecord(PAYMENT_DATA, paymentData));
        executeOne(query);
    }

    @Override
    public PaymentEvent getPaymentEvent(String invoiceId, String paymentId) throws DaoException {
        Query query = getDslContext().selectFrom(PAYMENT_EVENT)
                .where(PAYMENT_EVENT.INVOICE_ID.eq(invoiceId))
                .and(PAYMENT_EVENT.PAYMENT_ID.eq(paymentId))
                .orderBy(PAYMENT_EVENT.ID.desc())
                .limit(1);
        return fetchOne(query, paymentEventRowMapper);
    }

    @Override
    public void savePaymentEvent(PaymentEvent paymentEvent) throws DaoException {
        Query query = getDslContext().insertInto(PAYMENT_EVENT)
                .set(getDslContext().newRecord(PAYMENT_EVENT, paymentEvent));
        executeOne(query);
    }
}
