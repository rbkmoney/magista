package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.magista.CommonSearchQueryParams;
import com.rbkmoney.magista.config.PostgresqlSpringBootITest;
import com.rbkmoney.magista.dark.messiah.EnrichedStatInvoice;
import com.rbkmoney.magista.dark.messiah.PaymentSearchQuery;
import com.rbkmoney.magista.dark.messiah.RefundSearchQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class EnrichedSearchDaoImplTest {

    @Autowired
    private SearchDaoImpl searchDao;

    @Test
    @Sql("classpath:data/sql/search/enriched_invoices_search_data.sql")
    public void testEnrichedPayments() {

        List<EnrichedStatInvoice> enrichedPaymentInvoices = searchDao.getEnrichedPaymentInvoices(
                new PaymentSearchQuery()
                        .setCommonSearchQueryParams(new CommonSearchQueryParams()
                                .setPartyId("DB79AD6C-A507-43ED-9ECF-3BBD88475B32")
                                .setShopIds(List.of("SHOP_ID"))
                                .setFromTime("2016-10-25T15:45:20Z")
                                .setToTime("3018-10-25T18:10:10Z")));


        assertEquals(3, enrichedPaymentInvoices.size());
        assertEquals(2L, enrichedPaymentInvoices.stream()
                .filter(enrichedStatInvoice -> enrichedStatInvoice.refunds.size() > 0).count());
    }

    @Test
    @Sql("classpath:data/sql/search/enriched_invoices_search_data.sql")
    public void testEnrichedRefunds() {

        List<EnrichedStatInvoice> enrichedRefundInvoices = searchDao.getEnrichedRefundInvoices(new RefundSearchQuery()
                .setCommonSearchQueryParams(new CommonSearchQueryParams()
                        .setPartyId("A25B27EE-BE91-4977-9DB5-CCF52CC83741")
                        .setShopIds(List.of("SHOP_ID"))
                        .setFromTime("2016-10-25T15:45:20Z")
                        .setToTime("3018-10-25T18:10:10Z")));

        assertEquals(3, enrichedRefundInvoices.size());
    }

    @Test
    @Sql("classpath:data/sql/search/enriched_invoices_time_test.sql")
    public void testNewTimeRanges() {
        List<EnrichedStatInvoice> enrichedPaymentInvoices = searchDao.getEnrichedPaymentInvoices(
                new PaymentSearchQuery()
                        .setCommonSearchQueryParams(new CommonSearchQueryParams()
                                .setPartyId("DB79AD6C-A507-43ED-9ECF-3BBD88475B32")
                                .setShopIds(List.of("SHOP_ID"))
                                .setFromTime("3000-01-02T00:00:00Z")
                                .setToTime("3000-01-02T02:00:00Z")));

        assertEquals(3, enrichedPaymentInvoices.size());
        assertEquals(2L, enrichedPaymentInvoices.stream()
                .filter(enrichedStatInvoice -> enrichedStatInvoice.refunds.size() > 0).count());
    }

}
