package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.magista.dao.PayoutEventDao;
import com.rbkmoney.magista.dao.impl.AbstractDao;
import com.rbkmoney.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.magista.domain.enums.PayoutEventCategory;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Condition;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.magista.domain.tables.PayoutEventStat.PAYOUT_EVENT_STAT;

@Component
public class PayoutEventDaoImpl extends AbstractDao implements PayoutEventDao {

    public final RowMapper<PayoutEventStat> payoutEventStatRowMapper;

    public PayoutEventDaoImpl(DataSource dataSource) {
        super(dataSource);
        this.payoutEventStatRowMapper = new RecordRowMapper<>(PAYOUT_EVENT_STAT, PayoutEventStat.class);
    }

    @Override
    public PayoutEventStat findPayoutById(String payoutId) throws DaoException {
        Condition condition = PAYOUT_EVENT_STAT.PAYOUT_ID.eq(payoutId)
                .and(PAYOUT_EVENT_STAT.EVENT_CATEGORY.eq(PayoutEventCategory.PAYOUT));

        Query query = getDslContext().selectFrom(PAYOUT_EVENT_STAT)
                .where(PAYOUT_EVENT_STAT.ID.eq(
                        getDslContext().select(DSL.max(PAYOUT_EVENT_STAT.ID))
                                .from(PAYOUT_EVENT_STAT).where(condition)
                        )
                );
        return fetchOne(query, payoutEventStatRowMapper);
    }

    @Override
    public void insert(PayoutEventStat payoutEvent) throws DaoException {
        Query query = getDslContext().insertInto(PAYOUT_EVENT_STAT)
                .set(getDslContext().newRecord(PAYOUT_EVENT_STAT, payoutEvent));

        executeOne(query);
    }

}
