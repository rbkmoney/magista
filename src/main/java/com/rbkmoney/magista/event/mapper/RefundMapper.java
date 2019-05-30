package com.rbkmoney.magista.event.mapper;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.tables.pojos.RefundData;

public interface RefundMapper extends Mapper<InvoiceChange, MachineEvent, RefundData> {
}
