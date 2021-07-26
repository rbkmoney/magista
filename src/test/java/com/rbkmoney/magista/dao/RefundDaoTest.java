package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.RefundData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.testcontainers.annotations.postgresql.WithPostgresqlSingletonSpringBootITest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.rbkmoney.magista.util.RandomBeans.random;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WithPostgresqlSingletonSpringBootITest
public class RefundDaoTest {

    @Autowired
    private RefundDao refundDao;

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
        assertEquals(refundData,
                refundDao.get(refundData.getInvoiceId(), refundData.getPaymentId(), refundData.getRefundId()));
    }
}
