package com.rbkmoney.magista.event.mapper;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.tables.pojos.AllocationTransactionData;

import java.util.List;

public interface AllocationMapper extends Mapper<InvoiceChange, MachineEvent, List<AllocationTransactionData>> {
}
