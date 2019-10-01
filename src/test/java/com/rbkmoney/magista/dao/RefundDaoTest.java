package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.dao.impl.RefundDaoImpl;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.pojos.RefundData;
import com.rbkmoney.magista.exception.DaoException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = {RefundDaoImpl.class})
public class RefundDaoTest extends AbstractDaoTest {

    @Autowired
    RefundDao refundDao;

    @Test
    public void insertAndFindRefundEventTest() throws DaoException {
        RefundData refund = random(RefundData.class);

        refundDao.save(List.of(refund));

        assertEquals(refund, refundDao.get(refund.getInvoiceId(), refund.getPaymentId(), refund.getRefundId()));
    }

    @Test
    public void updatePreviousEventTest() {
        RefundData refundData = random(RefundData.class);

        refundDao.save(List.of(refundData));
        refundDao.save(List.of(refundData));

        RefundData refundDataWithPreviousEventId = new RefundData(refundData);
        refundDataWithPreviousEventId.setEventId(refundData.getEventId() - 1);

        refundDao.save(List.of(refundDataWithPreviousEventId));
        assertEquals(refundData, refundDao.get(refundData.getInvoiceId(), refundData.getPaymentId(), refundData.getRefundId()));
    }
}
