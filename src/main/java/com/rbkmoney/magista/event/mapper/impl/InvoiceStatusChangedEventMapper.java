package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoiceStatusChanged;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.InvoiceStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.mapper.InvoiceMapper;
import com.rbkmoney.magista.util.DamselUtil;
import org.springframework.stereotype.Component;

@Component
public class InvoiceStatusChangedEventMapper implements InvoiceMapper {

    @Override
    public InvoiceData map(InvoiceChange change, MachineEvent machineEvent) {
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setEventType(InvoiceEventType.INVOICE_STATUS_CHANGED);
        invoiceData.setEventId(machineEvent.getEventId());
        invoiceData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        invoiceData.setInvoiceId(machineEvent.getSourceId());

        InvoiceStatusChanged invoiceStatusChanged = change.getInvoiceStatusChanged();
        invoiceData.setInvoiceStatus(
                TBaseUtil.unionFieldToEnum(invoiceStatusChanged.getStatus(), InvoiceStatus.class)
        );
        invoiceData.setInvoiceStatusDetails(
                DamselUtil.getInvoiceStatusDetails(invoiceStatusChanged.getStatus())
        );

        return invoiceData;
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_STATUS_CHANGED;
    }
}
