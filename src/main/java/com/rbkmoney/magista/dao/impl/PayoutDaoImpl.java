package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.magista.dao.PayoutDao;
import com.rbkmoney.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.magista.domain.tables.pojos.PayoutData;
import com.rbkmoney.magista.domain.tables.records.PayoutDataRecord;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.magista.domain.tables.PayoutData.PAYOUT_DATA;

@Component
public class PayoutDaoImpl extends AbstractDao implements PayoutDao {

    public final RowMapper<PayoutData> payoutEventStatRowMapper;

    public PayoutDaoImpl(DataSource dataSource) {
        super(dataSource);
        this.payoutEventStatRowMapper = new RecordRowMapper<>(PAYOUT_DATA, PayoutData.class);
    }

    @Override
    public PayoutData get(String payoutId) throws DaoException {
        Query query = getDslContext().selectFrom(PAYOUT_DATA)
                .where(PAYOUT_DATA.PAYOUT_ID.eq(payoutId));
        return fetchOne(query, payoutEventStatRowMapper);
    }

    @Override
    public void save(PayoutData payoutData) throws DaoException {
        PayoutDataRecord payoutDataRecord = getDslContext().newRecord(PAYOUT_DATA, payoutData);
        Query query = getDslContext().insertInto(PAYOUT_DATA)
                .set(payoutDataRecord)
                .onConflict(PAYOUT_DATA.PAYOUT_ID)
                .doUpdate()
                .set(payoutDataRecord);

        executeOne(query);
    }

}
