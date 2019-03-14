package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.dao.impl.RefundDaoImpl;
import com.rbkmoney.magista.domain.tables.pojos.Refund;
import com.rbkmoney.magista.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = {RefundDaoImpl.class})
public class RefundDaoTest extends AbstractDaoTest {

    @Autowired
    RefundDao refundDao;

    @Test
    public void insertAndFindRefundEventTest() throws DaoException {
        Refund refund = random(Refund.class);

        refundDao.save(refund);

        assertEquals(refund, refundDao.get(refund.getInvoiceId(), refund.getPaymentId(), refund.getRefundId()));
    }
}
