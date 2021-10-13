package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.CommonSearchQueryParams;
import com.rbkmoney.magista.InvoicePaymentRefundStatus;
import com.rbkmoney.magista.RefundSearchQuery;
import com.rbkmoney.magista.StatRefund;
import com.rbkmoney.magista.config.PostgresqlSpringBootITest;
import com.rbkmoney.magista.config.testconfiguration.QueryProcessorConfig;
import com.rbkmoney.magista.query.QueryProcessor;
import com.rbkmoney.magista.util.DamselUtil;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@PostgresqlSpringBootITest
@Sql("classpath:data/sql/search/refund_search_data.sql")
public class RefundsSearchDaoImplTest {

    @Autowired
    private SearchDaoImpl searchDao;

    @Test
    public void testRefunds() {
        RefundSearchQuery refundSearchQuery = buildRefundSearchQuery();
        List<StatRefund> refunds = searchDao.getRefunds(refundSearchQuery);
        assertEquals(3, refunds.size());
    }

    @Test
    public void testExternalFailure() {
        RefundSearchQuery refundSearchQuery = buildRefundSearchQuery();
        refundSearchQuery.setRefundStatus(InvoicePaymentRefundStatus.failed);
        List<StatRefund> refunds = searchDao.getRefunds(refundSearchQuery);
        assertTrue(refunds.stream().allMatch(
                refund -> refund.getStatus().isSetFailed()
                        && refund.getStatus().getFailed().getFailure().isSetFailure()
        ));
    }

    @Test
    public void testSearchByInvoiceIds() {
        RefundSearchQuery refundSearchQuery = buildRefundSearchQuery();
        refundSearchQuery.setInvoiceIds(List.of("INVOICE_ID_1", "INVOICE_ID_2", "INVOICE_ID"));
        List<StatRefund> refunds = searchDao.getRefunds(refundSearchQuery);
        assertEquals(2, refunds.size());
    }


    private RefundSearchQuery buildRefundSearchQuery() {
        return new RefundSearchQuery()
                .setCommonSearchQueryParams(new CommonSearchQueryParams()
                        .setPartyId("PARTY_ID_1")
                        .setShopIds(List.of("SHOP_ID_1"))
                        .setFromTime("2016-10-25T15:45:20Z")
                        .setToTime("3018-10-25T18:10:10Z"));
    }

}
