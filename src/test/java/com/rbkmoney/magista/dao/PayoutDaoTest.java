package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.dao.impl.PayoutDaoImpl;
import com.rbkmoney.magista.domain.enums.PayoutEventType;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.PayoutType;
import com.rbkmoney.magista.domain.tables.pojos.Payout;
import com.rbkmoney.magista.exception.DaoException;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ContextConfiguration(classes = {PayoutDaoImpl.class})
public class PayoutDaoTest extends AbstractDaoTest {

    @Autowired
    PayoutDao payoutDao;

    @Test
    public void insertUpdateAndFindPayoutEventTest() throws DaoException {
        Payout payoutData = random(Payout.class);

        payoutDao.save(payoutData);

        assertEquals(payoutData, payoutDao.get(payoutData.getPayoutId()));

        payoutData.setStatus(PayoutStatus.cancelled);
        payoutData.setCancelledDetails("kek");
        payoutDao.save(payoutData);

        assertEquals(payoutData, payoutDao.get(payoutData.getPayoutId()));
    }

    @Test
    public void insertOnlyNotNullFields() throws DaoException {
        Payout payoutData = new Payout();
        payoutData.setPartyId(UUID.randomUUID().toString());
        payoutData.setEventCreatedAt(LocalDateTime.now());
        payoutData.setShopId(random(String.class));
        payoutData.setPayoutId(random(String.class));
        payoutData.setPartyId(random(String.class));
        payoutData.setCurrencyCode("RUB");
        payoutData.setCreatedAt(LocalDateTime.now());
        payoutData.setStatus(PayoutStatus.paid);
        payoutData.setAmount(Long.MAX_VALUE);
        payoutDao.save(payoutData);
        payoutDao.get(payoutData.getPayoutId());
    }

    @Test
    public void testUpdate() {
        Payout payout = EnhancedRandomBuilder.aNewEnhancedRandom().nextObject(Payout.class);
        payoutDao.save(payout);
        payout.setSequenceId(payout.getSequenceId() + 1);
        payoutDao.update(payout);
        assertEquals(payout.getSequenceId(), payoutDao.get(payout.getPayoutId()).getSequenceId());
    }

    @Test
    public void testDuplicate() {
        Payout payout = EnhancedRandomBuilder.aNewEnhancedRandom().nextObject(Payout.class);
        payoutDao.save(payout);
        long newAmount = 123L;
        payout.setAmount(newAmount);
        payoutDao.save(payout);
        assertNotEquals(newAmount, payoutDao.get(payout.getPayoutId()).getAmount());
        payout.setSequenceId(payout.getSequenceId() - 1);
        payout.setCurrencyCode("USD");
        payoutDao.update(payout);
        assertNotEquals(payout.getCurrencyCode(), payoutDao.get(payout.getPayoutId()).getCurrencyCode());
    }
}
