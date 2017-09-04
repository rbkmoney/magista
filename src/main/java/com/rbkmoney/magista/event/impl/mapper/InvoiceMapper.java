package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.InvoiceDetails;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.util.DamselUtil;

import java.time.LocalDateTime;

/**
 * Created by tolkonepiu on 10/11/2016.
 */
public class InvoiceMapper implements Mapper<InvoiceEventContext> {

    @Override
    public InvoiceEventContext fill(InvoiceEventContext context) {

        InvoiceChange invoiceChange = context.getInvoiceChange();
        InvoiceEventStat invoiceEventStat = context.getInvoiceEventStat();

        Event event = context.getSource().getSourceEvent().getProcessingEvent();

        long eventId = event.getId();
        LocalDateTime eventCreatedAt = TypeUtil.stringToLocalDateTime(event.getCreatedAt());
        String invoiceId = event.getSource().getInvoiceId();
        invoiceEventStat.setEventId(eventId);
        invoiceEventStat.setEventCreatedAt(eventCreatedAt);
        invoiceEventStat.setInvoiceId(invoiceId);

        if (invoiceChange.isSetInvoiceCreated()) {
            invoiceEventStat = createInvoiceEvent(invoiceChange, invoiceEventStat);
        }

        return context.setInvoiceEventStat(invoiceEventStat);
    }

    private InvoiceEventStat createInvoiceEvent(InvoiceChange invoiceChange, InvoiceEventStat invoiceEventStat) {
        invoiceEventStat.setEventCategory(InvoiceEventCategory.INVOICE);
        invoiceEventStat.setEventType(InvoiceEventType.INVOICE_CREATED);

        com.rbkmoney.damsel.domain.Invoice invoice = invoiceChange
                .getInvoiceCreated().getInvoice();

        invoiceEventStat.setInvoiceId(invoice.getId());
        invoiceEventStat.setPartyShopId(invoice.getShopId());
        invoiceEventStat.setPartyId(invoice.getOwnerId());
        invoiceEventStat.setInvoiceTemplateId(invoice.getTemplateId());

        InvoiceDetails details = invoice.getDetails();
        invoiceEventStat.setInvoiceProduct(details.getProduct());
        invoiceEventStat.setInvoiceDescription(details.getDescription());

        if (details.isSetCart()) {
            invoiceEventStat.setInvoiceCart(DamselUtil.toJson(details.getCart()));
        }

        InvoiceStatus invoiceStatus = invoice.getStatus();
        invoiceEventStat.setInvoiceStatus(
                TBaseUtil.unionFieldToEnum(
                        invoiceStatus,
                        com.rbkmoney.magista.domain.enums.InvoiceStatus.class
                )
        );

        invoiceEventStat.setInvoiceStatusDetails(
                DamselUtil.getInvoiceStatusDetails(invoiceStatus)
        );

        invoiceEventStat.setInvoiceCreatedAt(
                TypeUtil.stringToLocalDateTime(invoice.getCreatedAt())
        );

        invoiceEventStat.setInvoiceDue(
                TypeUtil.stringToLocalDateTime(invoice.getDue())
        );

        invoiceEventStat.setInvoiceAmount(invoice.getCost().getAmount());
        invoiceEventStat.setInvoiceCurrencyCode(invoice.getCost().getCurrency().getSymbolicCode());

        if (invoice.isSetContext()) {
            invoiceEventStat.setInvoiceContext(invoice.getContext().getData());
        }

        return invoiceEventStat;
    }


}
