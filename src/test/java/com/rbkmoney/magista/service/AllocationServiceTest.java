package com.rbkmoney.magista.service;

import com.rbkmoney.magista.config.PostgresqlSpringBootITest;
import com.rbkmoney.magista.dao.AllocationDao;
import com.rbkmoney.magista.domain.tables.pojos.AllocationTransactionData;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@PostgresqlSpringBootITest
public class AllocationServiceTest {

    private final EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandom();
    @Autowired
    public AllocationService allocationService;
    @Autowired
    public AllocationDao allocationDao;

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
        assertEquals(1, foundedAllocationTrx.size());
        assertEquals(modifiedAllocationTransaction, foundedAllocationTrx.get(0));
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
            assertEquals(allocationTransaction, allocation);
        }
    }

}
