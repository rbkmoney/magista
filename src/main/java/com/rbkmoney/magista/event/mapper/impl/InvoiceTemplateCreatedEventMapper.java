package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.payment_processing.InvoiceTemplateChange;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceTemplateEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceTemplate;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.InvoiceTemplateMapper;
import com.rbkmoney.magista.util.DamselUtil;
import org.springframework.stereotype.Component;

import static com.rbkmoney.magista.util.LifetimeIntervalThriftUtil.getInvoiceValidUntil;

@Component
public class InvoiceTemplateCreatedEventMapper implements InvoiceTemplateMapper {

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_TEMPLATE_CREATED;
    }

    @Override
    public InvoiceTemplate map(InvoiceTemplateChange change, MachineEvent machineEvent) {
        InvoiceTemplate invoiceTemplate = new InvoiceTemplate();
        invoiceTemplate.setEventId(machineEvent.getEventId());
        var eventCreatedAt = TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt());
        invoiceTemplate.setEventCreatedAt(eventCreatedAt);
        invoiceTemplate.setEventType(InvoiceTemplateEventType.INVOICE_TEMPLATE_CREATED);
        invoiceTemplate.setInvoiceTemplateId(machineEvent.getSourceId());
        var invoiceTemplateThrift = change.getInvoiceTemplateCreated().getInvoiceTemplate();
        invoiceTemplate.setPartyId(invoiceTemplateThrift.getOwnerId());
        invoiceTemplate.setShopId(invoiceTemplateThrift.getShopId());
        invoiceTemplate.setInvoiceValidUntil(
                getInvoiceValidUntil(
                        eventCreatedAt,
                        invoiceTemplateThrift.getInvoiceLifetime()));
        invoiceTemplate.setProduct(invoiceTemplateThrift.getProduct());
        invoiceTemplate.setDescription(invoiceTemplateThrift.getDescription());
        var details = invoiceTemplateThrift.getDetails();
        switch (details.getSetField()) {
            case CART -> invoiceTemplate.setInvoiceDetailsCartJson(DamselUtil.toJsonString(details.getCart()));
            case PRODUCT -> invoiceTemplate.setInvoiceDetailsProductJson(DamselUtil.toJsonString(details.getProduct()));
            default -> throw new IllegalArgumentException("Unknown field parameter, details=" + details);
        }
        if (invoiceTemplateThrift.isSetContext()) {
            var content = invoiceTemplateThrift.getContext();
            invoiceTemplate.setInvoiceContextType(content.getType());
            invoiceTemplate.setInvoiceContextData(content.getData());
        }
        invoiceTemplate.setName(invoiceTemplateThrift.getName());
        invoiceTemplate.setInvoiceTemplateCreatedAt(
                TypeUtil.stringToLocalDateTime(invoiceTemplateThrift.getCreatedAt()));
        return invoiceTemplate;
    }
}
