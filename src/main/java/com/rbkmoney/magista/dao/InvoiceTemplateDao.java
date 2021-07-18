package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.ChargebackData;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceTemplate;

import java.util.List;

public interface InvoiceTemplateDao {

    InvoiceTemplate get(String invoiceId, String invoiceTemplateId);

    void save(List<InvoiceTemplate> invoiceTemplates);
}
