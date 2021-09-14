package com.rbkmoney.magista.util;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.msgpack.Value;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.rbkmoney.testcontainers.annotations.util.RandomBeans.randomThriftOnlyRequiredFields;
import static com.rbkmoney.testcontainers.annotations.util.ThriftUtil.toByteArray;

public class InvoiceTemplateGenerator {

    public static List<MachineEvent> getEvents(
            String invoiceTemplateId,
            long sequenceId,
            InvoiceTemplateChange invoiceTemplateChange) {
        return List.of(getEvent(invoiceTemplateId, sequenceId, List.of(invoiceTemplateChange)));
    }

    public static MachineEvent getEvent(
            String invoiceTemplateId,
            long sequenceId,
            InvoiceTemplateChange invoiceTemplateChange) {
        return getEvent(invoiceTemplateId, sequenceId, List.of(invoiceTemplateChange));
    }

    public static MachineEvent getEvent(
            String invoiceTemplateId,
            long sequenceId,
            List<InvoiceTemplateChange> invoiceTemplateChanges) {
        return new MachineEvent()
                .setData(toByteArray(EventPayload.invoice_template_changes(invoiceTemplateChanges)))
                .setCreatedAt(Instant.now().toString())
                .setEventId(sequenceId)
                .setSourceNs("source_ns")
                .setSourceId(invoiceTemplateId);
    }

    public static InvoiceTemplateChange getCreated(InvoiceTemplate invoiceTemplate) {
        return InvoiceTemplateChange.invoice_template_created(new InvoiceTemplateCreated(invoiceTemplate));
    }

    public static InvoiceTemplateChange getUpdated(InvoiceTemplateUpdateParams invoiceTemplateUpdateParams) {
        return InvoiceTemplateChange.invoice_template_updated(new InvoiceTemplateUpdated(invoiceTemplateUpdateParams));
    }

    public static InvoiceTemplateChange getDeleted() {
        return InvoiceTemplateChange.invoice_template_deleted(new InvoiceTemplateDeleted());
    }

    public static InvoiceTemplate getInvoiceTemplate(InvoiceTemplateDetails details) {
        short date = 12;
        return randomThriftOnlyRequiredFields(InvoiceTemplate.class)
                .setId("setId")
                .setDescription("setDescription")
                .setName("setName")
                .setCreatedAt(Instant.now().toString())
                .setInvoiceLifetime(new LifetimeInterval()
                        .setDays(date)
                        .setMinutes(date)
                        .setSeconds(date))
                .setDetails(details)
                .setContext(getContent());
    }

    public static InvoiceTemplateUpdateParams getParams(InvoiceTemplateDetails details) {
        short date = 12;
        return new InvoiceTemplateUpdateParams()
                .setInvoiceLifetime(new LifetimeInterval()
                        .setDays(date)
                        .setMinutes(date)
                        .setSeconds(date))
                .setDescription("setDescription")
                .setProduct("setProduct")
                .setDetails(details)
                .setContext(getContent());
    }

    public static Content getContent() {
        return randomThriftOnlyRequiredFields(Content.class);
    }

    public static InvoiceTemplateDetails getCart() {
        InvoiceLine invoiceLine = randomThriftOnlyRequiredFields(InvoiceLine.class);
        invoiceLine.setMetadata(Map.of("meta", Value.str("data")));
        return InvoiceTemplateDetails.cart(new InvoiceCart(List.of(invoiceLine, invoiceLine)));
    }

    public static InvoiceTemplateDetails getProduct() {
        InvoiceTemplateProduct invoiceTemplateProduct = randomThriftOnlyRequiredFields(InvoiceTemplateProduct.class);
        invoiceTemplateProduct.setMetadata(Map.of("meta", Value.str("data")));
        return InvoiceTemplateDetails.product(invoiceTemplateProduct);
    }
}
