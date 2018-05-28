package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEvent;
import com.rbkmoney.magista.exception.DaoException;

public interface InvoiceDao {

    void saveInvoiceData(InvoiceData invoiceData) throws DaoException;

    InvoiceData getInvoiceData(String invoiceId) throws DaoException;

    void saveInvoiceEvent(InvoiceEvent invoiceEvent) throws DaoException;

}
