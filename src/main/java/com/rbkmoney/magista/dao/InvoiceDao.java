package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.exception.DaoException;

import java.util.List;

public interface InvoiceDao {

    InvoiceData get(String invoiceId) throws DaoException;

    void save(List<InvoiceData> invoices) throws DaoException;

}
