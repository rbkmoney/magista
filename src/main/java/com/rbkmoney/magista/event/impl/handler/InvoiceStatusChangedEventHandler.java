package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoiceStatusChanged;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.InvoiceStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.InvoiceService;
import com.rbkmoney.magista.util.DamselUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvoiceStatusChangedEventHandler implements Handler<InvoiceChange, MachineEvent> {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceStatusChangedEventHandler(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    public Processor handle(InvoiceChange change, MachineEvent machineEvent) {
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

        return () -> invoiceService.saveInvoice(invoiceData);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_STATUS_CHANGED;
    }
}
