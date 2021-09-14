package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.payment_processing.InvoiceTemplateChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.config.PostgresqlSpringBootITest;
import com.rbkmoney.magista.domain.enums.InvoiceTemplateEventType;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.service.InvoiceTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;

import static com.rbkmoney.magista.domain.tables.InvoiceTemplate.INVOICE_TEMPLATE;
import static com.rbkmoney.magista.util.InvoiceTemplateGenerator.*;
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
}
