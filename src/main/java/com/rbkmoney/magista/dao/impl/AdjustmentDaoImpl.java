package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.magista.dao.AdjustmentDao;
import com.rbkmoney.magista.dao.impl.AbstractDao;
import com.rbkmoney.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.magista.domain.tables.pojos.Adjustment;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.magista.domain.Tables.ADJUSTMENT;

@Component
public class AdjustmentDaoImpl extends AbstractDao implements AdjustmentDao {

    private final RowMapper<Adjustment> adjustmentRowMapper;

    public AdjustmentDaoImpl(DataSource dataSource) {
        super(dataSource);
        adjustmentRowMapper = new RecordRowMapper<>(ADJUSTMENT, Adjustment.class);
    }

    @Override
    public Adjustment get(String invoiceId, String paymentId, String adjustmentId) throws DaoException {
        Query query = getDslContext().selectFrom(ADJUSTMENT)
                .where(
                        ADJUSTMENT.ID.eq(
                                getDslContext().select(DSL.max(ADJUSTMENT.ID))
                                        .from(ADJUSTMENT).where(
                                        ADJUSTMENT.INVOICE_ID.eq(invoiceId)
                                                .and(ADJUSTMENT.PAYMENT_ID.eq(paymentId))
                                                .and(ADJUSTMENT.ADJUSTMENT_ID.eq(adjustmentId)))
                        )
                );
        return fetchOne(query, adjustmentRowMapper);
    }

    @Override
    public void save(Adjustment adjustment) throws DaoException {
        Query query = getDslContext().insertInto(ADJUSTMENT)
                .set(getDslContext().newRecord(ADJUSTMENT, adjustment))
                .onConflict(ADJUSTMENT.INVOICE_ID, ADJUSTMENT.PAYMENT_ID, ADJUSTMENT.ADJUSTMENT_ID, ADJUSTMENT.EVENT_ID, ADJUSTMENT.EVENT_TYPE, ADJUSTMENT.ADJUSTMENT_STATUS)
                .doUpdate()
                .set(getDslContext().newRecord(ADJUSTMENT, adjustment));

        executeOne(query);
    }
}
