package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;

import java.util.List;

public interface InvoiceDao {

    InvoiceData get(String invoiceId);

    void insert(List<InvoiceData> invoices);

    void update(List<InvoiceData> invoices);

}
