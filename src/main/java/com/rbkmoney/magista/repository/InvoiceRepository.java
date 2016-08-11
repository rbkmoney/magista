package com.rbkmoney.magista.repository;

import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import org.jooq.exception.DataAccessException;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public interface InvoiceRepository {

    Invoice findById(String id) throws DaoException;

    void changeStatus(String invoiceId, InvoiceStatus status) throws DaoException;

    void save(Invoice invoice) throws DaoException;

}
