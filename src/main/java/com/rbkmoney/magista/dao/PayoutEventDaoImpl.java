package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.enums.PayoutEventCategory;
import com.rbkmoney.magista.domain.enums.PayoutEventType;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.PayoutType;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.time.LocalDateTime;

import static com.rbkmoney.magista.domain.tables.PayoutEventStat.PAYOUT_EVENT_STAT;

public class PayoutEventDaoImpl extends AbstractDao implements PayoutEventDao {

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
        return fetchOne(query, getRowMapper(), getNamedParameterJdbcTemplate());
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

    public static RowMapper<PayoutEventStat> getRowMapper() {
        return (rs, i) -> {
            PayoutEventStat payoutEventStat = new PayoutEventStat();

            payoutEventStat.setId(rs.getLong("id"));
            payoutEventStat.setEventId(rs.getLong("event_id"));
            payoutEventStat.setEventCategory(PayoutEventCategory.valueOf(rs.getString("event_category")));
            payoutEventStat.setEventType(PayoutEventType.valueOf(rs.getString("event_type")));
            payoutEventStat.setEventCreatedAt(rs.getObject("event_created_at", LocalDateTime.class));
            payoutEventStat.setPartyId(rs.getString("party_id"));
            payoutEventStat.setPartyShopId(rs.getString("party_shop_id"));
            payoutEventStat.setPayoutId(rs.getString("payout_id"));
            payoutEventStat.setPayoutCreatedAt(rs.getObject("payout_created_at", LocalDateTime.class));
            payoutEventStat.setPayoutStatus(PayoutStatus.valueOf(rs.getString("payout_status")));
            payoutEventStat.setPayoutAmount(rs.getLong("payout_amount"));
            payoutEventStat.setPayoutFee(rs.getLong("payout_fee"));
            payoutEventStat.setPayoutCurrencyCode(rs.getString("payout_currency_code"));
            payoutEventStat.setPayoutType(PayoutType.valueOf(rs.getString("payout_type")));
            payoutEventStat.setPayoutPaidDetailsType(rs.getString("payout_paid_details_type"));
            payoutEventStat.setPayoutCardRequestId(rs.getString("payout_card_request_id"));
            payoutEventStat.setPayoutCardMaskPan(rs.getString("payout_card_mask_pan"));
            payoutEventStat.setPayoutCardProviderName(rs.getString("payout_card_provider_name"));
            payoutEventStat.setPayoutCardTransactionId(rs.getString("payout_card_transaction_id"));
            payoutEventStat.setPayoutAccountId(rs.getString("payout_account_id"));
            payoutEventStat.setPayoutAccountBankCorrId(rs.getString("payout_account_bank_corr_id"));
            payoutEventStat.setPayoutAccountBankBik(rs.getString("payout_account_bank_bik"));
            payoutEventStat.setPayoutAccountBankInn(rs.getString("payout_account_bank_inn"));
            payoutEventStat.setPayoutAccountPurpose(rs.getString("payout_account_purpose"));
            payoutEventStat.setPayoutCancelDetails(rs.getString("payout_cancel_details"));

            return payoutEventStat;
        };
    }
}
