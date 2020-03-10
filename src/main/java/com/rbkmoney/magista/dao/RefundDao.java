package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.RefundData;

import java.util.List;

public interface RefundDao {

    RefundData get(String invoiceId, String paymentId, String refundId);

    void save(List<RefundData> refunds);

}
