package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.dao.impl.AdjustmentDaoImpl;
import com.rbkmoney.magista.domain.tables.pojos.AdjustmentData;
import com.rbkmoney.magista.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = {AdjustmentDaoImpl.class})
public class AdjustmentDaoTest extends AbstractDaoTest {

    @Autowired
    AdjustmentDao adjustmentDao;

    @Test
    public void insertAndFindAdjustmentEventTest() throws DaoException {
        AdjustmentData adjustment = random(AdjustmentData.class);

        adjustmentDao.save(List.of(adjustment));

        assertEquals(adjustment, adjustmentDao.get(adjustment.getInvoiceId(), adjustment.getPaymentId(), adjustment.getAdjustmentId()));
    }

}
