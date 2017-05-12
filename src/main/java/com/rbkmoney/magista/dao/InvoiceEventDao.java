package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.InvoiceEvent;

/**
 * Created by tolkonepiu on 10/05/2017.
 */
public interface InvoiceEventDao {

    Long getLastEventId() throws DaoException;

    InvoiceEvent findPaymentByInvoiceAndPaymentId(String invoiceId, String paymentId) throws DaoException;

    InvoiceEvent findInvoiceById(String invoiceId) throws DaoException;

    void insert(InvoiceEvent invoiceEvent) throws DaoException;

}
