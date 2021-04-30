package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.AllocationTransactionData;

import java.util.List;

public interface AllocationDao {

    AllocationTransactionData get(String invoiceId, String allocationId);

    List<AllocationTransactionData> get(String invoiceId);

    void save(List<AllocationTransactionData> allocationDataList);

    void update(List<AllocationTransactionData> allocationDataList);

}
