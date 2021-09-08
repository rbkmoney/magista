package com.rbkmoney.magista.service;

import com.rbkmoney.damsel.domain.InvoiceTemplate;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.CommonSearchQueryParams;
import com.rbkmoney.magista.InvoiceTemplateSearchQuery;
import com.rbkmoney.magista.StatInvoiceTemplateResponse;
import com.rbkmoney.magista.config.PostgresqlSpringBootITest;
import com.rbkmoney.magista.listener.InvoiceTemplateListener;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
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

    @BeforeEach
    @SneakyThrows
    public void setUp() {
        String invoiceTemplateId = "invoiceTemplateId";
        invoiceTemplate = getInvoiceTemplate(getCart());
        MachineEvent message = getEvent(
                invoiceTemplateId,
                1,
                getCreated(invoiceTemplate));
        invoiceTemplateListener.handleMessages(List.of(message));
    }

    @Test
    public void name() {
        var commonSearchQueryParams = new CommonSearchQueryParams();
        commonSearchQueryParams.setFromTime(TypeUtil.temporalToString(LocalDateTime.now().minusDays(1)));
        commonSearchQueryParams.setToTime(TypeUtil.temporalToString(LocalDateTime.now().plusDays(1)));
        commonSearchQueryParams.setPartyId(invoiceTemplate.getOwnerId());
        commonSearchQueryParams.setShopIds(List.of(invoiceTemplate.getShopId()));
        commonSearchQueryParams.setLimit(1000);
        commonSearchQueryParams.setContinuationToken(null);
        var searchQuery = new InvoiceTemplateSearchQuery();
        searchQuery.setCommonSearchQueryParams(commonSearchQueryParams);
//        searchQuery.setInvoiceTemplateId(invoiceTemplate.getId());
//        searchQuery.setInvoiceValidUntil(TypeUtil.temporalToString(getInvoiceValidUntil(
//                LocalDateTime.now().minusMinutes(1),
//                invoiceTemplate.getInvoiceLifetime())));
        searchQuery.setProduct(invoiceTemplate.getProduct());
        StatInvoiceTemplateResponse statInvoiceTemplateResponse = merchantStatisticsService.getInvoiceTemplates(searchQuery);
        assertEquals(1, statInvoiceTemplateResponse.getInvoiceTemplates().size());
    }
}
