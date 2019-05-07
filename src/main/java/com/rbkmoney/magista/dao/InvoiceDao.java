package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.exception.DaoException;

public interface InvoiceDao {

    InvoiceData get(String invoiceId) throws DaoException;

    void save(InvoiceData invoiceData) throws DaoException;

}
