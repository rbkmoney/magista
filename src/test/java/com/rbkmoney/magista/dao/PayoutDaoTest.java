package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.config.AbstractDaoConfig;
import com.rbkmoney.magista.domain.enums.PayoutEventType;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.PayoutType;
import com.rbkmoney.magista.domain.tables.pojos.PayoutData;
import com.rbkmoney.magista.exception.DaoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.rbkmoney.magista.util.RandomBeans.random;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PayoutDaoTest extends AbstractDaoConfig {

    @Autowired
    private PayoutDao payoutDao;

    @Test
    public void insertUpdateAndFindPayoutEventTest() throws DaoException {
        PayoutData payoutData = random(PayoutData.class);

        payoutDao.save(payoutData);

        assertEquals(payoutData, payoutDao.get(payoutData.getPayoutId()));

        payoutData.setPayoutStatus(PayoutStatus.cancelled);
        payoutData.setPayoutCancelDetails("kek");
        payoutDao.save(payoutData);

        assertEquals(payoutData, payoutDao.get(payoutData.getPayoutId()));
    }

    @Test
    public void insertOnlyNotNullFields() throws DaoException {
        PayoutData payoutData = new PayoutData();
        payoutData.setPartyId(UUID.randomUUID().toString());
        payoutData.setEventId(Long.MAX_VALUE);
        payoutData.setEventType(PayoutEventType.PAYOUT_CREATED);
        payoutData.setEventCreatedAt(LocalDateTime.now());
        payoutData.setPartyShopId(random(String.class));
        payoutData.setPayoutId(random(String.class));
        payoutData.setPartyId(random(String.class));
        payoutData.setPayoutCurrencyCode("RUB");
        payoutData.setPayoutCreatedAt(LocalDateTime.now());
        payoutData.setPayoutStatus(PayoutStatus.paid);
        payoutData.setPayoutAmount(Long.MAX_VALUE);
        payoutData.setPayoutType(PayoutType.bank_account);

        payoutDao.save(payoutData);

        payoutDao.get(payoutData.getPayoutId());
    }

}
