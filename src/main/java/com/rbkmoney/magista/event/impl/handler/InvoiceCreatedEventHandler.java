package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEvent;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.InvoiceService;
import com.rbkmoney.magista.service.PartyService;
import com.rbkmoney.magista.util.DamselUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Component
public class InvoiceCreatedEventHandler implements Handler<InvoiceChange, StockEvent> {

    private final InvoiceService invoiceService;

    private final PartyService partyService;

    @Autowired
    public InvoiceCreatedEventHandler(InvoiceService invoiceService, PartyService partyService) {
        this.invoiceService = invoiceService;
        this.partyService = partyService;
    }

    @Override
    public Processor handle(InvoiceChange change, StockEvent parent) {
        Event event = parent.getSourceEvent().getProcessingEvent();

        InvoiceEvent invoiceEvent = new InvoiceEvent();
        invoiceEvent.setEventType(InvoiceEventType.INVOICE_CREATED);
        invoiceEvent.setEventId(event.getId());
        invoiceEvent.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        invoiceEvent.setInvoiceId(event.getSource().getInvoiceId());

        Invoice invoice = change.getInvoiceCreated().getInvoice();

        InvoiceData invoiceData = new InvoiceData();
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
        invoiceEvent.setInvoiceStatus(
                TBaseUtil.unionFieldToEnum(
                        invoiceStatus,
                        com.rbkmoney.magista.domain.enums.InvoiceStatus.class
                )
        );
        invoiceEvent.setInvoiceStatusDetails(
                DamselUtil.getInvoiceStatusDetails(invoiceStatus)
        );

        Shop shop;
        if (invoiceData.getInvoicePartyRevision() != null) {
            shop = partyService.getShop(
                    invoiceData.getPartyId().toString(),
                    invoiceData.getPartyShopId(),
                    invoiceData.getInvoicePartyRevision()
            );
        } else {
            shop = partyService.getShop(
                    invoiceData.getPartyId().toString(),
                    invoiceData.getPartyShopId(),
                    invoiceData.getInvoiceCreatedAt().toInstant(ZoneOffset.UTC)
            );
        }
        invoiceData.setPartyContractId(shop.getContractId());

        return () -> invoiceService.saveInvoice(invoiceData, invoiceEvent);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_CREATED;
    }

}
