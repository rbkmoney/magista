package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.magista.CommonSearchQueryParams;
import com.rbkmoney.magista.PayoutSearchQuery;
import com.rbkmoney.magista.PayoutToolType;
import com.rbkmoney.magista.StatPayout;
import com.rbkmoney.magista.config.PostgresqlSpringBootITest;
import com.rbkmoney.payout.manager.PayoutStatus;
import com.rbkmoney.payout.manager.PayoutUnpaid;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
@Sql("classpath:data/sql/search/payouts_search_data.sql")
public class PayoutsSearchDaoImplTest {

    @Autowired
    private SearchDaoImpl searchDao;

    @Test
    public void testPayouts() {
        PayoutSearchQuery payoutSearchQuery = buildPayoutSearchQuery();
        List<StatPayout> payouts = searchDao.getPayouts(payoutSearchQuery);
        assertEquals(4, payouts.size());
    }


    @Test
    public void shouldFilterByBankAccount() {
        PayoutSearchQuery payoutSearchQuery = buildPayoutSearchQuery();
        payoutSearchQuery.setPayoutType(PayoutToolType.payout_account);
        List<StatPayout> payouts = searchDao.getPayouts(payoutSearchQuery);
        assertEquals(2, payouts.size());
    }

    @Test
    public void shouldFilterByWalletInfo() {
        PayoutSearchQuery payoutSearchQuery = buildPayoutSearchQuery();
        payoutSearchQuery.setPayoutType(PayoutToolType.wallet);
        List<StatPayout> payouts = searchDao.getPayouts(payoutSearchQuery);
        assertEquals(1, payouts.size());
    }

    @Test
    public void shouldFilterByPaymentInstitutionAccount() {
        PayoutSearchQuery payoutSearchQuery = buildPayoutSearchQuery();
        payoutSearchQuery.setPayoutType(PayoutToolType.payment_institution_account);
        List<StatPayout> payouts = searchDao.getPayouts(payoutSearchQuery);
        assertEquals(1, payouts.size());
    }

    @Test
    public void shouldFilterByUnpaid() {
        PayoutSearchQuery payoutSearchQuery = buildPayoutSearchQuery();
        payoutSearchQuery.setPayoutStatuses(List.of(PayoutStatus.unpaid(new PayoutUnpaid())));
        List<StatPayout> payouts = searchDao.getPayouts(payoutSearchQuery);
        assertEquals(2, payouts.size());
    }

    private PayoutSearchQuery buildPayoutSearchQuery() {
        return new PayoutSearchQuery()
                .setCommonSearchQueryParams(new CommonSearchQueryParams()
                        .setPartyId("PARTY_ID_1")
                        .setShopIds(List.of("SHOP_ID_1"))
                        .setFromTime("2016-10-25T15:45:20Z")
                        .setToTime("3018-10-25T18:10:10Z"));
    }

}
