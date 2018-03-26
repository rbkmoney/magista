package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.Adjustment;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import static com.rbkmoney.magista.domain.Tables.ADJUSTMENT;

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
                .set(getDslContext().newRecord(ADJUSTMENT, adjustment));

        executeOne(query);
    }
}
