package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.InvoiceTemplate;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.CommonSearchQueryParams;
import com.rbkmoney.magista.InvoiceTemplateSearchQuery;
import com.rbkmoney.magista.config.PostgresqlSpringBootITest;
import com.rbkmoney.magista.listener.InvoiceTemplateListener;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static com.rbkmoney.magista.util.InvoiceTemplateGenerator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PostgresqlSpringBootITest
public class MerchantStatisticsServiceTest {

    @Autowired
    private InvoiceTemplateListener invoiceTemplateListener;
    @Autowired
    private MerchantStatisticsService merchantStatisticsService;

    private InvoiceTemplate invoiceTemplate;
    private String invoiceTemplateId;

    @BeforeEach
    @SneakyThrows
    public void setUp() {
        invoiceTemplateId = "invoiceTemplateId";
        invoiceTemplate = getInvoiceTemplate(getCart());
        MachineEvent message = getEvent(
                invoiceTemplateId,
                1,
                getCreated(invoiceTemplate));
        invoiceTemplateListener.handleMessages(List.of(message));
    }

    @Test
    public void shouldSearchInvoiceTemplates() {
        var commonSearchQueryParams = new CommonSearchQueryParams();
        commonSearchQueryParams.setFromTime(Instant.now().minusSeconds(60).toString());
        commonSearchQueryParams.setToTime(Instant.now().plusSeconds(60).toString());
        commonSearchQueryParams.setPartyId(invoiceTemplate.getOwnerId());
        commonSearchQueryParams.setShopIds(List.of(invoiceTemplate.getShopId()));
        commonSearchQueryParams.setLimit(1000);
        commonSearchQueryParams.setContinuationToken(null);
        var searchQuery = new InvoiceTemplateSearchQuery();
        searchQuery.setCommonSearchQueryParams(commonSearchQueryParams);
        searchQuery.setInvoiceTemplateId(invoiceTemplateId);
        searchQuery.setInvoiceValidUntil(Instant.now().plusSeconds(30).toString());
        searchQuery.setProduct(invoiceTemplate.getProduct());
        var statInvoiceTemplateResponse = merchantStatisticsService.getInvoiceTemplates(searchQuery);
        assertEquals(1, statInvoiceTemplateResponse.getInvoiceTemplates().size());
    }
}
