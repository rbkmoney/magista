package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.AbstractDaoTest;
import com.rbkmoney.magista.dao.AllocationDao;
import com.rbkmoney.magista.dao.impl.AllocationDaoImpl;
import com.rbkmoney.magista.domain.tables.pojos.AllocationTransactionData;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.Collectors;

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
        // Given
        String invoiceId = "testInvoiceId";
        List<AllocationTransactionData> allocationTransactions =
                enhancedRandom.objects(AllocationTransactionData.class, 3)
                        .peek(allocationTransactionData -> allocationTransactionData.setInvoiceId(invoiceId))
                        .collect(Collectors.toList());

        // When
        allocationService.saveAllocations(allocationTransactions);

        // Then
        List<AllocationTransactionData> foundedAllocationTrx = allocationDao.get(invoiceId);
        assertTrue(allocationTransactions.size() == foundedAllocationTrx.size()
                && allocationTransactions.containsAll(foundedAllocationTrx)
                && foundedAllocationTrx.containsAll(allocationTransactions));
    }

    @Test
    public void testPreviousAllocationModify() {
        // Given
        String invoiceId = "testInvoiceModId";
        String allocationId = "testAllocationId";
        AllocationTransactionData allocationTransaction =
                enhancedRandom.nextObject(AllocationTransactionData.class);
        allocationTransaction.setInvoiceId(invoiceId);
        allocationTransaction.setAllocationId(allocationId);
        AllocationTransactionData modifiedAllocationTransaction =
                enhancedRandom.nextObject(AllocationTransactionData.class);
        modifiedAllocationTransaction.setInvoiceId(invoiceId);
        modifiedAllocationTransaction.setAllocationId(allocationId);

        // When
        List<AllocationTransactionData> allocationTransactions =
                List.of(allocationTransaction, modifiedAllocationTransaction);
        allocationService.saveAllocations(allocationTransactions);

        // Then
        List<AllocationTransactionData> foundedAllocationTrx = allocationDao.get(invoiceId);
        Assert.assertEquals(1, foundedAllocationTrx.size());
        Assert.assertEquals(modifiedAllocationTransaction, foundedAllocationTrx.get(0));
    }

    @Test
    public void getAllocation() {
        // Given
        List<AllocationTransactionData> allocationTransactions =
                enhancedRandom.objects(AllocationTransactionData.class, 3)
                        .collect(Collectors.toList());
        // When
        allocationService.saveAllocations(allocationTransactions);

        // Then
        for (AllocationTransactionData allocationTransaction : allocationTransactions) {
            AllocationTransactionData allocation = allocationService
                    .getAllocation(allocationTransaction.getInvoiceId(), allocationTransaction.getAllocationId());
            Assert.assertEquals(allocationTransaction, allocation);
        }
    }

}
