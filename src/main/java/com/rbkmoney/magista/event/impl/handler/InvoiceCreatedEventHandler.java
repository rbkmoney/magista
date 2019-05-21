package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.Invoice;
import com.rbkmoney.damsel.domain.InvoiceDetails;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.InvoiceService;
import com.rbkmoney.magista.util.DamselUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InvoiceCreatedEventHandler implements Handler<InvoiceChange, MachineEvent> {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceCreatedEventHandler(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    public Processor handle(InvoiceChange change, MachineEvent machineEvent) {
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setEventType(InvoiceEventType.INVOICE_CREATED);
        invoiceData.setEventId(machineEvent.getEventId());
        invoiceData.setInvoiceId(machineEvent.getSourceId());

        invoiceData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));

        Invoice invoice = change.getInvoiceCreated().getInvoice();

        invoiceData.setInvoiceId(invoice.getId());
        invoiceData.setPartyId(UUID.fromString(invoice.getOwnerId()));
        invoiceData.setPartyShopId(invoice.getShopId());
        invoiceData.setInvoiceTemplateId(invoice.getTemplateId());
        invoiceData.setInvoiceCreatedAt(TypeUtil.stringToLocalDateTime(invoice.getCreatedAt()));
        invoiceData.setInvoiceDue(TypeUtil.stringToLocalDateTime(invoice.getDue()));
        invoiceData.setInvoiceAmount(invoice.getCost().getAmount());
        invoiceData.setInvoiceCurrencyCode(invoice.getCost().getCurrency().getSymbolicCode());

        InvoiceDetails details = invoice.getDetails();
        invoiceData.setInvoiceProduct(details.getProduct());
        invoiceData.setInvoiceDescription(details.getDescription());
        if (details.isSetCart()) {
            invoiceData.setInvoiceCartJson(DamselUtil.toJsonString(details.getCart()));
        }

        if (invoice.isSetPartyRevision()) {
            invoiceData.setInvoicePartyRevision(invoice.getPartyRevision());
        }

        if (invoice.isSetContext()) {
            Content content = invoice.getContext();
            invoiceData.setInvoiceContextType(content.getType());
            invoiceData.setInvoiceContext(content.getData());
        }

        InvoiceStatus invoiceStatus = invoice.getStatus();
        invoiceData.setInvoiceStatus(
                TBaseUtil.unionFieldToEnum(
                        invoiceStatus,
                        com.rbkmoney.magista.domain.enums.InvoiceStatus.class
                )
        );
        invoiceData.setInvoiceStatusDetails(
                DamselUtil.getInvoiceStatusDetails(invoiceStatus)
        );

        return () -> invoiceService.saveInvoice(invoiceData);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_CREATED;
    }

}
