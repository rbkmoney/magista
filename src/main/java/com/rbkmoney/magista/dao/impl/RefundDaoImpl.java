package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.magista.dao.RefundDao;
import com.rbkmoney.magista.dao.impl.AbstractDao;
import com.rbkmoney.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.magista.domain.tables.pojos.Refund;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.magista.domain.tables.Refund.REFUND;

@Component
public class RefundDaoImpl extends AbstractDao implements RefundDao {

    private final RowMapper<Refund> refundRowMapper;

    public RefundDaoImpl(DataSource dataSource) {
        super(dataSource);
        refundRowMapper = new RecordRowMapper<>(REFUND, Refund.class);
    }

    @Override
    public Refund get(String invoiceId, String paymentId, String refundId) throws DaoException {
        Query query = getDslContext().selectFrom(REFUND)
                .where(
                        REFUND.ID.eq(
                                getDslContext().select(DSL.max(REFUND.ID))
                                        .from(REFUND).where(
                                        REFUND.INVOICE_ID.eq(invoiceId)
                                                .and(REFUND.PAYMENT_ID.eq(paymentId))
                                                .and(REFUND.REFUND_ID.eq(refundId)))
                        )
                );
        return fetchOne(query, refundRowMapper);
    }

    @Override
    public void save(Refund refund) throws DaoException {
        Query query = getDslContext().insertInto(REFUND)
                .set(getDslContext().newRecord(REFUND, refund))
                .onConflict(REFUND.EVENT_ID, REFUND.EVENT_TYPE, REFUND.REFUND_STATUS)
                .doUpdate()
                .set(getDslContext().newRecord(REFUND, refund));

        executeOne(query);
    }
}
