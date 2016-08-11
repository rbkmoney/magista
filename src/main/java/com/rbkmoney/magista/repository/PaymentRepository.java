package com.rbkmoney.magista.repository;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.magista.model.Payment;

/**
 * Created by tolkonepiu on 04.08.16.
 */
public interface PaymentRepository {

    Payment findById(String id) throws DaoException;

    void changeStatus(String paymentId, InvoicePaymentStatus status) throws DaoException;

    void save(Payment payment) throws DaoException;

}
