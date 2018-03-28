package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.domain.tables.pojos.Adjustment;
import com.rbkmoney.magista.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

public class AdjustmentDaoTest extends AbstractIntegrationTest {

    @Autowired
    AdjustmentDao adjustmentDao;

    @Test
    public void insertAndFindAdjustmentEventTest() throws DaoException {
        Adjustment adjustment = random(Adjustment.class);

        adjustmentDao.save(adjustment);

        assertEquals(adjustment, adjustmentDao.get(adjustment.getInvoiceId(), adjustment.getPaymentId(), adjustment.getAdjustmentId()));
    }

}
