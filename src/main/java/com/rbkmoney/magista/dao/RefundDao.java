package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.RefundData;
import com.rbkmoney.magista.exception.DaoException;

public interface RefundDao {

    RefundData get(String invoiceId, String paymentId, String refundId) throws DaoException;

    void save(RefundData refund) throws DaoException;

}
