package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.exception.DaoException;

import java.util.List;

public interface PaymentDao {

    PaymentData get(String invoiceId, String paymentId) throws DaoException;

    void save(List<PaymentData> payments) throws DaoException;

}
