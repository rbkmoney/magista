package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Payment;

/**
 * Created by tolkonepiu on 23.08.16.
 */
public interface PaymentDao {

    Payment findById(String paymentId, String invoiceId) throws DaoException;

    void insert(Payment payment) throws DaoException;

    void update(Payment payment) throws DaoException;

}
