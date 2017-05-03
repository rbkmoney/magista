package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Invoice;

/**
 * Created by tolkonepiu on 23.08.16.
 */
public interface InvoiceDao {

    Invoice findById(String id) throws DaoException;

    void insert(Invoice invoice) throws DaoException;

}
