package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.domain.Party;
import com.rbkmoney.damsel.domain.Shop;
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv;
import com.rbkmoney.damsel.payment_processing.PartyRevisionParam;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.service.PartyService;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class PartyServiceTest extends AbstractIntegrationTest {

    @Autowired
    PartyService partyService;

    @MockBean
    PartyManagementSrv.Iface partyManagementClient;

    final String shopId = UUID.randomUUID().toString();
    final long partyRevision = 1L;
    final Instant timestamp = Instant.now();
    final long timeout = 100L;

    @Before
    public void setup() throws TException {
        Answer<Party> answerWithParty = answer -> {
            Thread.sleep(timeout);
            Party party = new Party();
            Map<String, Shop> shops = new HashMap<>();
            shops.put(shopId, new Shop());
            party.setShops(shops);
            return party;
        };

        when(partyManagementClient.checkout(
                any(), any(), eq(PartyRevisionParam.revision(partyRevision)))
        ).then(answerWithParty);
        when(partyManagementClient.checkout(
                any(), any(), eq(PartyRevisionParam.timestamp(TypeUtil.temporalToString(timestamp))))
        ).then(answerWithParty);
    }

    @Test
    public void testGetPartyByRevisionFromCache() {
        String partyId = UUID.randomUUID().toString();
        executeWithTimeout(() -> partyService.getParty(partyId, partyRevision));
    }

    @Test
    public void testGetShopByRevisionFromCache() {
        String partyId = UUID.randomUUID().toString();
        executeWithTimeout(() -> partyService.getShop(partyId, shopId, partyRevision));
    }

    @Test
    public void testGetPartyByTimestampFromCache() {
        String partyId = UUID.randomUUID().toString();
        executeWithTimeout(() -> partyService.getParty(partyId, timestamp));
    }

    @Test
    public void testGetShopByTimestampFromCache() {
        String partyId = UUID.randomUUID().toString();
        executeWithTimeout(() -> partyService.getShop(partyId, shopId, timestamp));
    }

    public void executeWithTimeout(Runnable runnable) {
        long start = System.currentTimeMillis();
        runnable.run();
        assertTrue(System.currentTimeMillis() - start >= timeout);
        start = System.currentTimeMillis();
        runnable.run();
        assertTrue(System.currentTimeMillis() - start < timeout);
    }
}
