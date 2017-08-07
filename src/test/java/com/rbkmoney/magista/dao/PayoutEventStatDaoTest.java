package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.domain.enums.PayoutEventCategory;
import com.rbkmoney.magista.domain.enums.PayoutEventType;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.PayoutType;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

public class PayoutEventStatDaoTest extends AbstractIntegrationTest {

    @Autowired
    PayoutEventDao payoutEventDao;

    @Test
    public void insertUpdateAndFindPayoutEventTest() throws IOException {
        PayoutEventStat payoutEventStat = random(PayoutEventStat.class);

        payoutEventDao.insert(payoutEventStat);

        assertEquals(payoutEventStat, payoutEventDao.findPayoutById(payoutEventStat.getPayoutId()));

        payoutEventStat.setPayoutStatus(PayoutStatus.cancelled);
        payoutEventStat.setPayoutCancelDetails("kek");

        payoutEventDao.update(payoutEventStat);

        assertEquals(payoutEventStat, payoutEventDao.findPayoutById(payoutEventStat.getPayoutId()));
    }

    @Test
    public void insertOnlyNotNullFields() throws DaoException {
        PayoutEventStat payoutEventStat = new PayoutEventStat();
        payoutEventStat.setPartyId(UUID.randomUUID().toString());
        payoutEventStat.setEventId(Long.MAX_VALUE);
        payoutEventStat.setEventType(PayoutEventType.PAYOUT_CREATED);
        payoutEventStat.setEventCategory(PayoutEventCategory.PAYOUT);
        payoutEventStat.setEventCreatedAt(LocalDateTime.now());
        payoutEventStat.setPartyId("\0");
        payoutEventStat.setPartyShopId("\000");
        payoutEventStat.setPayoutId("\\000\000\000");
        payoutEventStat.setPayoutCreatedAt(LocalDateTime.now());
        payoutEventStat.setPayoutStatus(PayoutStatus.paid);
        payoutEventStat.setPayoutAmount(Long.MAX_VALUE);
        payoutEventStat.setPayoutType(PayoutType.account_payout);

        payoutEventDao.insert(payoutEventStat);

        payoutEventDao.findPayoutById(payoutEventStat.getPayoutId());
    }

}
