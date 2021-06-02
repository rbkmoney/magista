package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.AllocationDao;
import com.rbkmoney.magista.domain.tables.pojos.AllocationTransactionData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.util.BeanUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllocationService {

    private final AllocationDao allocationDao;

    public AllocationTransactionData getAllocation(String invoiceId, String allocationId) {
        try {
            return allocationDao.get(invoiceId, allocationId);
        } catch (DaoException ex) {
            throw new StorageException(
                    String.format("Failed to get invoice data, invoiceId='%s';allocationId='%s'",
                            invoiceId, allocationId), ex);
        }
    }

    public void saveAllocations(List<AllocationTransactionData> allocationDataList) {
        Map<AllocationCacheKey, AllocationTransactionData> allocationDataMap = new LinkedHashMap<>();
        List<AllocationTransactionData> enrichedAllocationData = allocationDataList.stream()
                .peek(allocationData -> {
                    AllocationCacheKey cacheKey = new AllocationCacheKey(
                            allocationData.getInvoiceId(),
                            allocationData.getAllocationId()
                    );
                    AllocationTransactionData previousAllocation =
                            allocationDataMap.computeIfAbsent(cacheKey, allocationCacheKey -> {
                                return getAllocation(
                                        allocationCacheKey.getInvoiceId(),
                                        allocationCacheKey.getAllocationId()
                                );
                            });
                    if (previousAllocation != null) {
                        BeanUtil.merge(previousAllocation, allocationData);
                    }
                })
                .peek(allocationData -> {
                    AllocationCacheKey cacheKey = new AllocationCacheKey(
                            allocationData.getInvoiceId(),
                            allocationData.getAllocationId()
                    );
                    allocationDataMap.put(cacheKey, allocationData);
                })
                .collect(Collectors.toList());
        try {
            allocationDao.save(enrichedAllocationData);
            log.info("Allocation events have been saved, batchSize={}, insertsCount={}",
                    allocationDataList.size(), enrichedAllocationData.size());
        } catch (DaoException e) {
            throw new StorageException(
                    String.format("Failed to save allocation events, size=%d", enrichedAllocationData.size()), e);
        }
    }

    @Data
    private static class AllocationCacheKey {
        private final String invoiceId;
        private final String allocationId;
    }

}
