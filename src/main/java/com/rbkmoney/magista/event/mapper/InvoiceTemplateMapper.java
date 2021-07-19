package com.rbkmoney.magista.event.mapper;

import com.rbkmoney.damsel.payment_processing.InvoiceTemplateChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceTemplate;

public interface InvoiceTemplateMapper extends Mapper<InvoiceTemplateChange, MachineEvent, InvoiceTemplate> {
}
