package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.InvoiceDetails;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.util.DamselUtil;

/**
 * Created by tolkonepiu on 10/11/2016.
 */
public class InvoiceMapper implements Mapper<InvoiceEventContext> {

    @Override
    public InvoiceEventContext fill(InvoiceEventContext value) {

        InvoiceChange invoiceChange = value.getInvoiceChange();
        InvoiceEventStat invoiceEventStat = value.getInvoiceEventStat();
        invoiceEventStat = createInvoiceEvent(invoiceChange, invoiceEventStat);

        value.setInvoiceEventStat(invoiceEventStat);

        return value;
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
