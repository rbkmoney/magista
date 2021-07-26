package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceTemplate;
import com.rbkmoney.magista.exception.DaoException;

import java.util.List;

public interface InvoiceTemplateDao {

    InvoiceTemplate get(String invoiceTemplateId) throws DaoException;

    void save(List<InvoiceTemplate> invoiceTemplates) throws DaoException;

}
