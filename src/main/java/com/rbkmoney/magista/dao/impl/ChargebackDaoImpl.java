package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.magista.dao.ChargebackDao;
import com.rbkmoney.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.magista.domain.tables.pojos.ChargebackData;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.rbkmoney.magista.domain.tables.ChargebackData.CHARGEBACK_DATA;

@Component
public class ChargebackDaoImpl extends AbstractDao implements ChargebackDao {

    private final RowMapper<ChargebackData> chargebackDataRowMapper;

    public ChargebackDaoImpl(DataSource dataSource) {
        super(dataSource);
        chargebackDataRowMapper = new RecordRowMapper<>(CHARGEBACK_DATA, ChargebackData.class);
    }

    @Override
    public ChargebackData get(String invoiceId, String paymentId, String chargebackId) {
        Query query = getDslContext().selectFrom(CHARGEBACK_DATA)
                .where(
                        CHARGEBACK_DATA.ID.eq(
                                getDslContext().select(DSL.max(CHARGEBACK_DATA.ID))
                                        .from(CHARGEBACK_DATA).where(
                                        CHARGEBACK_DATA.INVOICE_ID.eq(invoiceId)
                                                .and(CHARGEBACK_DATA.PAYMENT_ID.eq(paymentId))
                                                .and(CHARGEBACK_DATA.CHARGEBACK_ID.eq(chargebackId)))
                        )
                );
        return fetchOne(query, chargebackDataRowMapper);
    }

    @Override
    public void save(ChargebackData chargebackData) {
        Query query = getDslContext().insertInto(CHARGEBACK_DATA)
                .set(getDslContext().newRecord(CHARGEBACK_DATA, chargebackData))
                .onConflict(CHARGEBACK_DATA.INVOICE_ID, CHARGEBACK_DATA.PAYMENT_ID, CHARGEBACK_DATA.CHARGEBACK_ID, CHARGEBACK_DATA.EVENT_ID, CHARGEBACK_DATA.EVENT_TYPE, CHARGEBACK_DATA.CHARGEBACK_STATUS)
                .doUpdate()
                .set(getDslContext().newRecord(CHARGEBACK_DATA, chargebackData));

        executeOne(query);
    }
}
