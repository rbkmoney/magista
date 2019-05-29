package com.rbkmoney.magista.event;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.tables.pojos.RefundData;

public interface RefundHandler extends Handler<InvoiceChange, MachineEvent, RefundData> {
}
