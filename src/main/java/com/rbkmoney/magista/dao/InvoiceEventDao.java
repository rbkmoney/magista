package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.exception.DaoException;

/**
 * Created by tolkonepiu on 26/05/2017.
 */
public interface InvoiceEventDao {

    Long getLastEventId() throws DaoException;

    InvoiceEventStat findAdjustmentByIds(String invoiceId, String paymentId, String adjustmentId) throws DaoException;

    InvoiceEventStat findRefundByIds(String invoiceId, String paymentId, String refundId) throws DaoException;

    InvoiceEventStat findPaymentByIds(String invoiceId, String paymentId) throws DaoException;

    InvoiceEventStat findInvoiceById(String invoiceId) throws DaoException;

    void insert(InvoiceEventStat invoiceEvent) throws DaoException;

}
