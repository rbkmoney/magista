package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentEvent;
import com.rbkmoney.magista.exception.DaoException;

public interface PaymentDao {

    void savePaymentData(PaymentData invoiceData) throws DaoException;

    PaymentEvent getPaymentEvent(String invoiceId, String paymentId) throws DaoException;

    void savePaymentEvent(PaymentEvent paymentEvent) throws DaoException;

}
