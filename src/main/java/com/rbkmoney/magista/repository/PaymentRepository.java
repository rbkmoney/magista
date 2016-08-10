package com.rbkmoney.magista.repository;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.magista.model.Payment;
import org.jooq.exception.DataAccessException;

/**
 * Created by tolkonepiu on 04.08.16.
 */
public interface PaymentRepository {

    Payment findById(String id) throws DaoException;

    void changeStatus(String paymentId, InvoicePaymentStatus._Fields status) throws DaoException;

    void save(Payment payment) throws DaoException;

}
