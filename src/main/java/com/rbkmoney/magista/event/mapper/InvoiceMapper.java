package com.rbkmoney.magista.event.mapper;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;

public interface InvoiceMapper extends Mapper<InvoiceChange, MachineEvent, InvoiceData> {
}
