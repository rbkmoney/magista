package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.msgpack.Value;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.config.PostgresqlSpringBootITest;
import com.rbkmoney.magista.domain.enums.InvoiceTemplateEventType;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.service.InvoiceTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.rbkmoney.magista.domain.tables.InvoiceTemplate.INVOICE_TEMPLATE;
import static com.rbkmoney.testcontainers.annotations.util.ThriftUtil.fillThriftObject;
import static com.rbkmoney.testcontainers.annotations.util.ThriftUtil.toByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@PostgresqlSpringBootITest
public class InvoiceTemplateServiceTest {

    private static final String TABLE_NAME = INVOICE_TEMPLATE.getSchema().getName() + "." + INVOICE_TEMPLATE.getName();

    @Autowired
    private InvoiceTemplateListener invoiceTemplateListener;

    @Autowired
    private InvoiceTemplateService invoiceTemplateService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void shouldHandleAndSave() throws Exception {
        String invoiceTemplateId = "invoiceTemplateId";
        MachineEvent message = getEvent(
                invoiceTemplateId,
                1,
                List.of(
                        getCreated(getInvoiceTemplate(getCart())),
                        getUpdated(getParams(getCart())),
                        getDeleted()));
        invoiceTemplateListener.handleMessages(List.of(message));
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE_NAME))
                .isEqualTo(1);
        assertThat(invoiceTemplateService.get(invoiceTemplateId).getEventType())
                .isEqualTo(InvoiceTemplateEventType.INVOICE_TEMPLATE_DELETED);
    }

    @Test
    public void shouldNotFound() {
        assertThrows(
                NotFoundException.class,
                () -> invoiceTemplateService.get("invoiceTemplateId"));
    }

    @Test
    public void shouldUpdateData() throws Exception {
        String savedId = "invoiceTemplateId1";
        invoiceTemplateListener.handleMessages(
                getEvents(savedId, 1, getCreated(getInvoiceTemplate(getCart()))));
        invoiceTemplateListener.handleMessages(
                getEvents(savedId, 2, getUpdated(getParams(getCart()))));
        invoiceTemplateListener.handleMessages(
                getEvents(savedId, 3, getUpdated(getParams(getCart()))));
        invoiceTemplateListener.handleMessages(
                getEvents(savedId, 4, getUpdated(getParams(getCart()))));
        InvoiceTemplateChange lastUpdated = getUpdated(getParams(getCart()));
        invoiceTemplateListener.handleMessages(
                getEvents(savedId, 5, lastUpdated));
        assertThat(invoiceTemplateService.get(savedId).getInvoiceContextType())
                .isEqualTo(lastUpdated.getInvoiceTemplateUpdated().getDiff().getContext().getType());
    }

    @Test
    public void shouldSkipDuplicatesWhenSequenceIdIsLessThanSequenceIdInStorage() throws Exception {
        String invoiceTemplateId = "invoiceTemplateId";
        MachineEvent message = getEvent(
                invoiceTemplateId,
                1,
                List.of(
                        getCreated(getInvoiceTemplate(getCart())),
                        getCreated(getInvoiceTemplate(getCart()))));
        invoiceTemplateListener.handleMessages(List.of(message));
        InvoiceTemplateChange lastUpdated = getUpdated(getParams(getCart()));
        invoiceTemplateListener.handleMessages(
                getEvents(invoiceTemplateId, 2, lastUpdated));
        // skip this
        invoiceTemplateListener.handleMessages(
                getEvents(invoiceTemplateId, 1, getUpdated(getParams(getCart()))));
        assertThat(invoiceTemplateService.get(invoiceTemplateId).getInvoiceContextType())
                .isEqualTo(lastUpdated.getInvoiceTemplateUpdated().getDiff().getContext().getType());
    }

    @Test
    public void shouldReWriteDuplicatesByProtocolOpportunityAndWillBeFine() throws Exception {
        String invoiceTemplateId = "invoiceTemplateId";
        InvoiceTemplateChange updated = getUpdated(getParams(getCart()));
        invoiceTemplateListener.handleMessages(
                List.of(
                        getEvent(
                                invoiceTemplateId,
                                1,
                                List.of(
                                        getCreated(getInvoiceTemplate(getCart())),
                                        getUpdated(getParams(getCart())))),
                        getEvent(
                                invoiceTemplateId,
                                1,
                                List.of(
                                        getCreated(getInvoiceTemplate(getCart())),
                                        updated))));
        assertThat(invoiceTemplateService.get(invoiceTemplateId).getInvoiceContextType())
                .isEqualTo(updated.getInvoiceTemplateUpdated().getDiff().getContext().getType());
    }

    private List<MachineEvent> getEvents(
            String invoiceTemplateId,
            long sequenceId,
            InvoiceTemplateChange invoiceTemplateChange) {
        return List.of(getEvent(invoiceTemplateId, sequenceId, List.of(invoiceTemplateChange)));
    }

    private MachineEvent getEvent(
            String invoiceTemplateId,
            long sequenceId,
            InvoiceTemplateChange invoiceTemplateChange) {
        return getEvent(invoiceTemplateId, sequenceId, List.of(invoiceTemplateChange));
    }

    private MachineEvent getEvent(
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

    private InvoiceTemplateChange getCreated(InvoiceTemplate invoiceTemplate) {
        return InvoiceTemplateChange.invoice_template_created(new InvoiceTemplateCreated(invoiceTemplate));
    }

    private InvoiceTemplateChange getUpdated(InvoiceTemplateUpdateParams invoiceTemplateUpdateParams) {
        return InvoiceTemplateChange.invoice_template_updated(new InvoiceTemplateUpdated(invoiceTemplateUpdateParams));
    }

    private InvoiceTemplateChange getDeleted() {
        return InvoiceTemplateChange.invoice_template_deleted(new InvoiceTemplateDeleted());
    }

    private InvoiceTemplate getInvoiceTemplate(InvoiceTemplateDetails details) {
        InvoiceTemplate invoiceTemplate = new InvoiceTemplate();
        invoiceTemplate = fillThriftObject(invoiceTemplate, InvoiceTemplate.class);
        invoiceTemplate.setId("setId");
        invoiceTemplate.setDescription("setDescription");
        short date = 12;
        invoiceTemplate.getInvoiceLifetime()
                .setDays(date)
                .setMinutes(date)
                .setSeconds(date);
        invoiceTemplate.setDetails(details);
        invoiceTemplate.setContext(getContent());
        return invoiceTemplate;
    }

    private InvoiceTemplateUpdateParams getParams(InvoiceTemplateDetails details) {
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

    private Content getContent() {
        Content context = new Content();
        context = fillThriftObject(context, Content.class);
        return context;
    }

    private InvoiceTemplateDetails getCart() {
        InvoiceLine invoiceLine = new InvoiceLine();
        invoiceLine = fillThriftObject(invoiceLine, InvoiceLine.class);
        invoiceLine.setMetadata(Map.of("meta", Value.str("data")));
        return InvoiceTemplateDetails.cart(new InvoiceCart(List.of(invoiceLine, invoiceLine)));
    }

    private InvoiceTemplateDetails getProduct() {
        InvoiceTemplateProduct invoiceTemplateProduct = new InvoiceTemplateProduct();
        invoiceTemplateProduct = fillThriftObject(invoiceTemplateProduct, InvoiceTemplateProduct.class);
        invoiceTemplateProduct.setMetadata(Map.of("meta", Value.str("data")));
        return InvoiceTemplateDetails.product(invoiceTemplateProduct);
    }
}
