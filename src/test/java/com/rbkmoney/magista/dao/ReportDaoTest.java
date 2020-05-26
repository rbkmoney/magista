package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ReportDaoTest extends AbstractIntegrationTest {

    @Autowired
    private ReportDao reportDao;

    @Test
    public void testGetPaymentAccountingDataWhenEmpty() {
        String partyId = UUID.randomUUID().toString();
        assertEquals(
                ImmutableMap.<String, String>builder()
                        .put("merchant_id", partyId)
                        .put("shop_id", "shop_id")
                        .put("currency_code", "RUB")
                        .put("funds_acquired", "0")
                        .put("fee_charged", "0")
                        .build(),
                reportDao.getPaymentAccountingData(partyId, "shop_id", "RUB", Optional.empty(), LocalDateTime.now())
        );
    }

    @Test
    public void testGetRefundAccountingDataWhenEmpty() {
        assertEquals(
                ImmutableMap.<String, String>builder()
                        .put("merchant_id", "party_id")
                        .put("shop_id", "shop_id")
                        .put("currency_code", "RUB")
                        .put("funds_refunded", "0")
                        .build(),
                reportDao.getRefundAccountingData("party_id", "shop_id", "RUB", Optional.empty(), LocalDateTime.now())
        );
    }

    @Test
    public void testGetAdjustmentAccountingDataWhenEmpty() {
        assertEquals(
                ImmutableMap.<String, String>builder()
                        .put("merchant_id", "party_id")
                        .put("shop_id", "shop_id")
                        .put("currency_code", "RUB")
                        .put("funds_adjusted", "0")
                        .build(),
                reportDao.getAdjustmentAccountingData("party_id", "shop_id", "RUB", Optional.empty(), LocalDateTime.now())
        );
    }

    @Test
    public void testGetPayoutAccountingDataWhenEmpty() {
        assertEquals(
                ImmutableMap.<String, String>builder()
                        .put("merchant_id", "party_id")
                        .put("shop_id", "shop_id")
                        .put("currency_code", "RUB")
                        .put("funds_paid_out", "0")
                        .build(),
                reportDao.getPayoutAccountingData("party_id", "shop_id", "RUB", Optional.empty(), LocalDateTime.now())
        );
    }

    @Test
    public void testGetChargebackAccountingDataWhenEmpty() {
        assertEquals(
                ImmutableMap.<String, String>builder()
                        .put("merchant_id", "party_id")
                        .put("shop_id", "shop_id")
                        .put("currency_code", "RUB")
                        .put("funds_returned", "0")
                        .build(),
                reportDao.getChargebackAccountingData("party_id", "shop_id", "RUB", LocalDateTime.now(), LocalDateTime.now())
        );
    }

}
