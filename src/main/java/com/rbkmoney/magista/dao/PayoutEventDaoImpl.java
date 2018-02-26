package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.enums.PayoutEventCategory;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import static com.rbkmoney.magista.domain.tables.PayoutEventStat.PAYOUT_EVENT_STAT;

public class PayoutEventDaoImpl extends AbstractDao implements PayoutEventDao {

    public static final RowMapper<PayoutEventStat> ROW_MAPPER = new RecordRowMapper<>(PAYOUT_EVENT_STAT, PayoutEventStat.class);

    public PayoutEventDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        Query query = getDslContext().select(PAYOUT_EVENT_STAT.EVENT_ID.max()).from(PAYOUT_EVENT_STAT);
        return fetchOne(query, Long.class);
    }

    @Override
    public PayoutEventStat findPayoutById(String payoutId) throws DaoException {
        Query query = getDslContext().selectFrom(PAYOUT_EVENT_STAT)
                .where(PAYOUT_EVENT_STAT.PAYOUT_ID.eq(payoutId)
                        .and(PAYOUT_EVENT_STAT.EVENT_CATEGORY.eq(PayoutEventCategory.PAYOUT)));
        return fetchOne(query, ROW_MAPPER);
    }

    @Override
    public void insert(PayoutEventStat payoutEvent) throws DaoException {
        Query query = getDslContext().insertInto(PAYOUT_EVENT_STAT)
                .set(getDslContext().newRecord(PAYOUT_EVENT_STAT, payoutEvent));

        executeOne(query);
    }

    @Override
    public void update(PayoutEventStat payoutEvent) throws DaoException {
        Query query = getDslContext().update(PAYOUT_EVENT_STAT)
                .set(getDslContext().newRecord(PAYOUT_EVENT_STAT, payoutEvent))
                .where(PAYOUT_EVENT_STAT.PAYOUT_ID.eq(payoutEvent.getPayoutId()));

        executeOne(query);
    }

}
