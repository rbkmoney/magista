package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.payment_processing.InvoiceTemplateChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceTemplateEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceTemplate;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.InvoiceTemplateMapper;
import org.springframework.stereotype.Component;

@Component
public class InvoiceTemplateCreatedEventMapper implements InvoiceTemplateMapper {

    @Override
    public InvoiceTemplate map(InvoiceTemplateChange change, MachineEvent machineEvent) {
        InvoiceTemplate invoiceTemplate = new InvoiceTemplate();
        invoiceTemplate.setEventId(machineEvent.getEventId());
        invoiceTemplate.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        invoiceTemplate.setEventType(InvoiceTemplateEventType.INVOICE_TEMPLATE_CREATED);
        var invoiceTemplateThrift = change.getInvoiceTemplateCreated().getInvoiceTemplate();
        invoiceTemplate.setInvoiceTemplateId(invoiceTemplateThrift.getId());
        invoiceTemplate.setInvoiceId(machineEvent.getSourceId());
        invoiceTemplate.setPartyId(invoiceTemplateThrift.getOwnerId());
        invoiceTemplate.setPartyShopId(invoiceTemplateThrift.getShopId());
        return invoiceTemplate;
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_TEMPLATE_CREATED;
    }
}
