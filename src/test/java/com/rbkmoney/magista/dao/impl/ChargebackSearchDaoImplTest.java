package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.ChargebackSearchQuery;
import com.rbkmoney.magista.CommonSearchQueryParams;
import com.rbkmoney.magista.StatChargeback;
import com.rbkmoney.magista.config.PostgresqlSpringBootITest;
import com.rbkmoney.magista.config.testconfiguration.QueryProcessorConfig;
import com.rbkmoney.magista.dao.ChargebackDao;
import com.rbkmoney.magista.query.QueryProcessor;
import com.rbkmoney.magista.util.DamselUtil;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class ChargebackSearchDaoImplTest {

    @Autowired
    private SearchDaoImpl searchDao;

    @Test
    @Sql("classpath:data/sql/search/chargeback_search_data.sql")
    public void chargebackSearchTest() {
        ChargebackSearchQuery chargebackSearchQuery = new ChargebackSearchQuery()
                .setCommonSearchQueryParams(new CommonSearchQueryParams()
                        .setPartyId("party_id_1")
                        .setShopIds(List.of("party_shop_id_1"))
                        .setFromTime("2016-10-25T15:45:20Z")
                        .setToTime("3018-10-25T18:10:10Z"));
        List<StatChargeback> chargebacks = searchDao.getChargebacks(chargebackSearchQuery);
        assertEquals(3, chargebacks.size());

        chargebackSearchQuery
                .setChargebackCategories(List.of(
                        InvoicePaymentChargebackCategory.fraud(new InvoicePaymentChargebackCategoryFraud()),
                        InvoicePaymentChargebackCategory.dispute(new InvoicePaymentChargebackCategoryDispute())));

        chargebacks = searchDao.getChargebacks(chargebackSearchQuery);
        assertEquals(2, chargebacks.size());

        chargebackSearchQuery.unsetChargebackCategories();
        chargebackSearchQuery.setChargebackStatuses(List.of(
                InvoicePaymentChargebackStatus.pending(new InvoicePaymentChargebackPending()),
                InvoicePaymentChargebackStatus.cancelled(new InvoicePaymentChargebackCancelled())
        ));

        chargebacks = searchDao.getChargebacks(chargebackSearchQuery);
        assertEquals(2, chargebacks.size());

        chargebackSearchQuery.unsetChargebackStatuses();
        chargebackSearchQuery.setChargebackStages(List.of(
                InvoicePaymentChargebackStage.chargeback(new InvoicePaymentChargebackStageChargeback()),
                InvoicePaymentChargebackStage.arbitration(new InvoicePaymentChargebackStageArbitration())
        ));

        chargebacks = searchDao.getChargebacks(chargebackSearchQuery);
        assertEquals(2, chargebacks.size());
    }
}
