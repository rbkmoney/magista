package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.config.PostgresqlSpringBootITest;
import com.rbkmoney.magista.domain.tables.pojos.AllocationTransactionData;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@PostgresqlSpringBootITest
public class AllocationDaoTest {

    private final EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandom();
    @Autowired
    private AllocationDao allocationDao;

    @Test
    public void saveAllocationTest() {
        // Given
        AllocationTransactionData allocationTrxData =
                enhancedRandom.nextObject(AllocationTransactionData.class, "id");
        allocationDao.save(List.of(allocationTrxData));

        // When
        AllocationTransactionData foundedAllocationTrxData = allocationDao
                .get(allocationTrxData.getInvoiceId(), allocationTrxData.getAllocationId());

        // Then
        assertTrue(new ReflectionEquals(allocationTrxData, "id").matches(foundedAllocationTrxData));
    }

    @Test
    public void saveMultipleAllocationTest() {
        // Given
        List<AllocationTransactionData> allocationTransactions =
                enhancedRandom.objects(AllocationTransactionData.class, 5, "id")
                        .peek(allocationTransactionData -> {
                            allocationTransactionData.setInvoiceId("testInvoiceId");
                        }).collect(Collectors.toList());
        allocationDao.save(allocationTransactions);

        // When
        List<AllocationTransactionData> foundedAllocationTransactions = allocationDao.get("testInvoiceId");

        // Then
        assertEquals("Allocation count not equals", allocationTransactions.size(),
                foundedAllocationTransactions.size());
        for (AllocationTransactionData allocationTransaction : allocationTransactions) {
            AllocationTransactionData foundedAllocationTrx = foundedAllocationTransactions.stream()
                    .filter(allocationTransactionData -> allocationTransactionData.getAllocationId()
                            .equals(allocationTransaction.getAllocationId()))
                    .findFirst().orElseThrow();
            assertTrue(new ReflectionEquals(allocationTransaction, "id").matches(foundedAllocationTrx));
        }
    }

}
