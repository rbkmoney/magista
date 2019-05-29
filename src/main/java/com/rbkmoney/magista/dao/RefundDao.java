package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.RefundData;
import com.rbkmoney.magista.exception.DaoException;

import java.util.List;

public interface RefundDao {

    RefundData get(String invoiceId, String paymentId, String refundId) throws DaoException;

    void save(List<RefundData> refunds) throws DaoException;

}
