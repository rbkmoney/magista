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
        MachineEvent message = new MachineEvent();
        message.setData(toByteArray(EventPayload.invoice_template_changes(invoiceTemplateChanges)));
        message.setCreatedAt(Instant.now().toString());
        message.setEventId(sequenceId);
        message.setSourceNs("source_ns");
        message.setSourceId(invoiceTemplateId);
        return message;
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
        InvoiceTemplate invoiceTemplate = randomThriftOnlyRequiredFields(InvoiceTemplate.class);
        invoiceTemplate.setId("setId");
        invoiceTemplate.setDescription("setDescription");
        invoiceTemplate.setName("setName");
        invoiceTemplate.setCreatedAt(Instant.now().toString());
        short date = 12;
        invoiceTemplate.getInvoiceLifetime()
                .setDays(date)
                .setMinutes(date)
                .setSeconds(date);
        invoiceTemplate.setDetails(details);
        invoiceTemplate.setContext(getContent());
        return invoiceTemplate;
    }

    public static InvoiceTemplateUpdateParams getParams(InvoiceTemplateDetails details) {
        InvoiceTemplateUpdateParams invoiceTemplateUpdateParams = new InvoiceTemplateUpdateParams();
        short date = 12;
        invoiceTemplateUpdateParams.setInvoiceLifetime(new LifetimeInterval()
                .setDays(date)
                .setMinutes(date)
                .setSeconds(date));
        invoiceTemplateUpdateParams.setDescription("setDescription");
        invoiceTemplateUpdateParams.setProduct("setProduct");
        invoiceTemplateUpdateParams.setDetails(details);
        invoiceTemplateUpdateParams.setContext(getContent());
        return invoiceTemplateUpdateParams;
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
