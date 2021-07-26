package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.msgpack.Value;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.exception.InvoiceTemplateAlreadyCreatedException;
import com.rbkmoney.magista.exception.InvoiceTemplateAlreadyDeletedException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.service.InvoiceTemplateService;
import com.rbkmoney.testcontainers.annotations.postgresql.WithPostgresqlSingletonSpringBootITest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.rbkmoney.magista.domain.tables.InvoiceTemplate.INVOICE_TEMPLATE;
import static com.rbkmoney.magista.util.ThriftUtil.fillThriftObject;
import static com.rbkmoney.magista.util.ThriftUtil.toByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WithPostgresqlSingletonSpringBootITest
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
                List.of(
                        getCreated(getInvoiceTemplate(getCart())),
                        getUpdated(getParams(getCart())),
                        getDeleted()));
        invoiceTemplateListener.handleMessages(List.of(message));
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE_NAME))
                .isEqualTo(1);
        assertThrows(
                InvoiceTemplateAlreadyDeletedException.class,
                () -> invoiceTemplateService.get(invoiceTemplateId));
    }

    @Test
    public void shouldThrowNotFoundException() throws InterruptedException {
        String notSavedId = "invoiceTemplateId";
        assertThrows(
                NotFoundException.class,
                () -> invoiceTemplateService.get(notSavedId));
        String savedId = "invoiceTemplateId1";
        invoiceTemplateListener.handleMessages(
                getEvents(savedId, getCreated(getInvoiceTemplate(getCart()))));
        assertThat(invoiceTemplateService.get(savedId)).isNotNull();
        assertThrows(
                NotFoundException.class,
                () -> invoiceTemplateListener.handleMessages(
                        getEvents(notSavedId, getUpdated(getParams(getCart())))));
        invoiceTemplateListener.handleMessages(
                getEvents(savedId, getUpdated(getParams(getCart()))));
        assertThrows(
                NotFoundException.class,
                () -> invoiceTemplateListener.handleMessages(
                        getEvents(notSavedId, getDeleted())));
        invoiceTemplateListener.handleMessages(
                getEvents(savedId, getDeleted()));
        assertThrows(
                InvoiceTemplateAlreadyDeletedException.class,
                () -> invoiceTemplateService.get(savedId));
    }

    @Test
    public void shouldThrowExceptionWhenAlreadyCreated() throws Exception {
        String alreadyCreatedId = "invoiceTemplateId1";
        invoiceTemplateListener.handleMessages(List.of(
                getEvent(alreadyCreatedId, getCreated(getInvoiceTemplate(getCart()))),
                getEvent("invoiceTemplateId2", getCreated(getInvoiceTemplate(getCart()))),
                getEvent("invoiceTemplateId3", getCreated(getInvoiceTemplate(getCart())))));
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE_NAME))
                .isEqualTo(3);
        assertThat(invoiceTemplateService.get("invoiceTemplateId2")).isNotNull();
        assertThrows(
                InvoiceTemplateAlreadyCreatedException.class,
                () -> invoiceTemplateListener.handleMessages(
                        getEvents(alreadyCreatedId, getCreated(getInvoiceTemplate(getCart())))));
    }

    @Test
    public void shouldThrowExceptionWhenAlreadyUpdated() throws Exception {
        String alreadyDeletedId = "invoiceTemplateId2";
        invoiceTemplateListener.handleMessages(List.of(
                getEvent("invoiceTemplateId1", getCreated(getInvoiceTemplate(getCart()))),
                getEvent(alreadyDeletedId, getCreated(getInvoiceTemplate(getCart()))),
                getEvent("invoiceTemplateId3", getCreated(getInvoiceTemplate(getCart())))));
        assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE_NAME))
                .isEqualTo(3);
        assertThat(invoiceTemplateService.get("invoiceTemplateId2")).isNotNull();
        invoiceTemplateListener.handleMessages(
                getEvents(alreadyDeletedId, getDeleted()));
        assertThrows(
                InvoiceTemplateAlreadyCreatedException.class,
                () -> invoiceTemplateListener.handleMessages(
                        getEvents(alreadyDeletedId, getCreated(getInvoiceTemplate(getCart())))));
        assertThrows(
                InvoiceTemplateAlreadyDeletedException.class,
                () -> invoiceTemplateListener.handleMessages(
                        getEvents(alreadyDeletedId, getUpdated(getParams(getProduct())))));
        assertThrows(
                InvoiceTemplateAlreadyDeletedException.class,
                () -> invoiceTemplateListener.handleMessages(
                        getEvents(alreadyDeletedId, getDeleted())));
    }

    @Test
    public void shouldUpdateData() throws InterruptedException {
        String savedId = "invoiceTemplateId1";
        invoiceTemplateListener.handleMessages(
                getEvents(savedId, getCreated(getInvoiceTemplate(getCart()))));
        invoiceTemplateListener.handleMessages(
                getEvents(savedId, getUpdated(getParams(getCart()))));
        invoiceTemplateListener.handleMessages(
                getEvents(savedId, getUpdated(getParams(getCart()))));
        invoiceTemplateListener.handleMessages(
                getEvents(savedId, getUpdated(getParams(getCart()))));
        InvoiceTemplateChange lastUpdated = getUpdated(getParams(getCart()));
        invoiceTemplateListener.handleMessages(
                getEvents(savedId, lastUpdated));
        assertThat(invoiceTemplateService.get(savedId).getInvoiceContextType())
                .isEqualTo(lastUpdated.getInvoiceTemplateUpdated().getDiff().getContext().getType());
    }

    private List<MachineEvent> getEvents(String invoiceTemplateId, InvoiceTemplateChange invoiceTemplateChange) {
        return List.of(getEvent(invoiceTemplateId, List.of(invoiceTemplateChange)));
    }

    private MachineEvent getEvent(String invoiceTemplateId, InvoiceTemplateChange invoiceTemplateChange) {
        return getEvent(invoiceTemplateId, List.of(invoiceTemplateChange));
    }

    private MachineEvent getEvent(String invoiceTemplateId, List<InvoiceTemplateChange> invoiceTemplateChanges) {
        MachineEvent message = new MachineEvent();
        message.setData(toByteArray(EventPayload.invoice_template_changes(invoiceTemplateChanges)));
        message.setCreatedAt(Instant.now().toString());
        message.setEventId(1L);
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
