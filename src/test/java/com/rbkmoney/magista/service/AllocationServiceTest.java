package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.AbstractDaoTest;
import com.rbkmoney.magista.dao.AllocationDao;
import com.rbkmoney.magista.dao.impl.AllocationDaoImpl;
import com.rbkmoney.magista.domain.tables.pojos.AllocationTransactionData;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = {AllocationService.class, AllocationDaoImpl.class})
public class AllocationServiceTest extends AbstractDaoTest {

    @Autowired
    public AllocationService allocationService;

    @Autowired
    public AllocationDao allocationDao;

    private final EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandom();

    @Test
    public void saveAllocations() {
        List<AllocationTransactionData> allocationTransactions =
                enhancedRandom.objects(AllocationTransactionData.class, 3)
                        .peek(allocationTransactionData -> allocationTransactionData.setInvoiceId("testInvoiceId"))
                        .collect(Collectors.toList());
        List<AllocationTransactionData> allocationTransactionSec = allocationTransactions.stream()
                .map(AllocationTransactionData::new)
                .peek(allocationTransactionData -> allocationTransactionData.setFeeAmount(1000L))
                .collect(Collectors.toList());

        List<AllocationTransactionData> allAllocationTransactions =
                Stream.concat(allocationTransactions.stream(), allocationTransactionSec.stream())
                        .collect(Collectors.toList());
        allocationService.saveAllocations(allAllocationTransactions);
        List<AllocationTransactionData> foundedAllocationTrx = allocationDao.get("testInvoiceId");
        assertTrue(allocationTransactionSec.size() == foundedAllocationTrx.size()
                && allocationTransactionSec.containsAll(foundedAllocationTrx)
                && foundedAllocationTrx.containsAll(allocationTransactionSec));
    }

}
