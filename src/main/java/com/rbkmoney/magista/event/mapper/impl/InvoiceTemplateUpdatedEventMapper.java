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
public class InvoiceTemplateUpdatedEventMapper implements InvoiceTemplateMapper {

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_TEMPLATE_UPDATED;
    }

    @Override
    public InvoiceTemplate map(InvoiceTemplateChange change, MachineEvent machineEvent) {
        InvoiceTemplate invoiceTemplate = new InvoiceTemplate();
        invoiceTemplate.setEventId(machineEvent.getEventId());
        var eventCreatedAt = TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt());
        invoiceTemplate.setEventCreatedAt(eventCreatedAt);
        invoiceTemplate.setEventType(InvoiceTemplateEventType.INVOICE_TEMPLATE_UPDATED);
        var updateParams = change.getInvoiceTemplateUpdated().getDiff();
        if (updateParams.isSetInvoiceLifetime()) {
            invoiceTemplate.setInvoiceValidUntil(
                    getInvoiceValidUntil(
                            eventCreatedAt,
                            updateParams.getInvoiceLifetime()));
        }
        invoiceTemplate.setProduct(updateParams.getProduct());
        invoiceTemplate.setDescription(updateParams.getDescription());
        if (updateParams.isSetDetails()) {
            var details = updateParams.getDetails();
            switch (details.getSetField()) {
                case CART:
                    invoiceTemplate.setInvoiceDetailsCartJson(DamselUtil.toJsonString(details.getCart()));
                    break;
                case PRODUCT:
                    invoiceTemplate.setInvoiceDetailsProductJson(DamselUtil.toJsonString(details.getProduct()));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown field parameter, details=" + details);
            }
        }
        if (updateParams.isSetContext()) {
            var content = updateParams.getContext();
            invoiceTemplate.setInvoiceContextType(content.getType());
            invoiceTemplate.setInvoiceContextData(content.getData());
        }
        return invoiceTemplate;
    }
}
