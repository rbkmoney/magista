package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.magista.dao.EventDao;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

import static com.rbkmoney.magista.domain.tables.Adjustment.ADJUSTMENT;
import static com.rbkmoney.magista.domain.tables.InvoiceEvent.INVOICE_EVENT;
import static com.rbkmoney.magista.domain.tables.PaymentEvent.PAYMENT_EVENT;
import static com.rbkmoney.magista.domain.tables.PayoutEventStat.PAYOUT_EVENT_STAT;
import static com.rbkmoney.magista.domain.tables.Refund.REFUND;

@Component
public class EventDaoImpl extends AbstractDao implements EventDao {

    public EventDaoImpl(DataSource ds) {
        super(ds);
    }

    @Override
    public Optional<Long> getLastInvoiceEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(DSL.field("event_id"))).from(
                getDslContext().select(DSL.max(INVOICE_EVENT.EVENT_ID).as("event_id")).from(INVOICE_EVENT)
                        .unionAll(getDslContext().select(DSL.max(PAYMENT_EVENT.EVENT_ID).as("event_id")).from(PAYMENT_EVENT))
                        .unionAll(getDslContext().select(DSL.max(REFUND.EVENT_ID).as("event_id")).from(REFUND))
                        .unionAll(getDslContext().select(DSL.max(ADJUSTMENT.EVENT_ID).as("event_id")).from(ADJUSTMENT))
        );
        return Optional.ofNullable(fetchOne(query, Long.class));
    }

    @Override
    public Optional<Long> getLastPayoutEventId() throws DaoException {
        Query query = getDslContext().select(DSL.max(PAYOUT_EVENT_STAT.EVENT_ID)).from(PAYOUT_EVENT_STAT);
        return Optional.ofNullable(fetchOne(query, Long.class));
    }
}
