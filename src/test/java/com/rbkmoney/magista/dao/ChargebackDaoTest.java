package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.dao.impl.ChargebackDaoImpl;
import com.rbkmoney.magista.domain.tables.pojos.ChargebackData;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = {ChargebackDaoImpl.class})
public class ChargebackDaoTest extends AbstractDaoTest {

    @Autowired
    private ChargebackDao chargebackDao;

    @Test
    public void saveAndGet() {
        ChargebackData chargebackData = EnhancedRandom.random(ChargebackData.class);

        chargebackDao.save(Collections.singletonList(chargebackData));

        ChargebackData chargeback = chargebackDao.get(chargebackData.getInvoiceId(), chargebackData.getPaymentId(), chargebackData.getChargebackId());
        Assert.assertEquals(chargebackData, chargeback);
    }

    @Test
    public void updatePreviousEventTest() {
        ChargebackData chargebackData = random(ChargebackData.class);

        chargebackDao.save(List.of(chargebackData));
        chargebackDao.save(List.of(chargebackData));

        ChargebackData chargebackDataWithPreviousEventId = new ChargebackData(chargebackData);
        chargebackDataWithPreviousEventId.setEventId(chargebackData.getEventId() - 1);

        chargebackDao.save(List.of(chargebackDataWithPreviousEventId));
        ChargebackData expectedChargebackData = chargebackDao.get(
                chargebackData.getInvoiceId(), chargebackData.getPaymentId(), chargebackData.getChargebackId());
        assertEquals(chargebackData, expectedChargebackData);
    }

}
