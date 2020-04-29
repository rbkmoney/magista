package com.rbkmoney.magista.event.mapper;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.tables.pojos.ChargebackData;

public interface ChargebackMapper extends Mapper<InvoiceChange, MachineEvent, ChargebackData> {
}
