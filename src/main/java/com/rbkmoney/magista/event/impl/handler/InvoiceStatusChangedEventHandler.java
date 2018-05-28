package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoiceStatusChanged;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.InvoiceStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEvent;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.InvoiceService;
import com.rbkmoney.magista.util.DamselUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvoiceStatusChangedEventHandler implements Handler<InvoiceChange, StockEvent> {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceStatusChangedEventHandler(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    public Processor handle(InvoiceChange change, StockEvent parent) {
        Event event = parent.getSourceEvent().getProcessingEvent();

        InvoiceEvent invoiceEvent = new InvoiceEvent();
        invoiceEvent.setEventType(InvoiceEventType.INVOICE_STATUS_CHANGED);
        invoiceEvent.setEventId(event.getId());
        invoiceEvent.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        invoiceEvent.setInvoiceId(event.getSource().getInvoiceId());

        InvoiceStatusChanged invoiceStatusChanged = change.getInvoiceStatusChanged();
        invoiceEvent.setInvoiceStatus(
                TBaseUtil.unionFieldToEnum(invoiceStatusChanged.getStatus(), InvoiceStatus.class)
        );
        invoiceEvent.setInvoiceStatusDetails(
                DamselUtil.getInvoiceStatusDetails(invoiceStatusChanged.getStatus())
        );

        return () -> invoiceService.saveInvoiceChange(invoiceEvent);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_STATUS_CHANGED;
    }
}
