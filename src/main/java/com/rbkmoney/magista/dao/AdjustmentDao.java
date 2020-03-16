package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.AdjustmentData;

import java.util.List;

public interface AdjustmentDao {

    AdjustmentData get(String invoiceId, String paymentId, String adjustmentId);

    void save(List<AdjustmentData> adjustments);

}
