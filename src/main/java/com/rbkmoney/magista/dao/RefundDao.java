package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.Refund;
import com.rbkmoney.magista.exception.DaoException;

public interface RefundDao {

    Refund get(String invoiceId, String paymentId, String refundId) throws DaoException;

    void save(Refund refund) throws DaoException;

}
