package com.rbkmoney.magista.event.mapper;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.tables.pojos.AdjustmentData;

public interface AdjustmentMapper extends Mapper<InvoiceChange, MachineEvent, AdjustmentData> {
}
