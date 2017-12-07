package com.rbkmoney.magista.dao;

import com.rbkmoney.damsel.merch_stat.StatInvoice;
import com.rbkmoney.damsel.merch_stat.StatPayment;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.query.impl.QueryContextFactoryImpl;
import com.rbkmoney.magista.query.impl.QueryProcessorImpl;
import com.rbkmoney.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class OffsetTest extends AbstractIntegrationTest {

    private final int count = 1000;
    private final int size = 20;
    private final String partyId = "partyID";
    private final String shopId = "shopID";

    @Autowired
    private InvoiceEventDao invoiceEventDao;

    private QueryProcessorImpl queryProcessor;

    @Autowired
    StatisticsDao statisticsDao;

    @Before
    public void before() {
        QueryContextFactoryImpl contextFactory = new QueryContextFactoryImpl(statisticsDao);
        queryProcessor = new QueryProcessorImpl(JsonQueryParser.newWeakJsonQueryParser(), new QueryBuilderImpl(), contextFactory);
    }

    @Test
    public void testInvoicesOffset() {
        EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .build();

        LocalDateTime fromTime = LocalDateTime.now(ZoneOffset.UTC);

        List<InvoiceEventStat> invoiceEventStats = enhancedRandom.objects(InvoiceEventStat.class, count, "invoiceCart")
                .map(new Function<InvoiceEventStat, InvoiceEventStat>() {
                         AtomicLong nextId = new AtomicLong();
                         LocalDateTime start = fromTime;

                         @Override
                         public InvoiceEventStat apply(InvoiceEventStat invoiceEventStat) {
                             invoiceEventStat.setInvoiceId(String.valueOf(nextId.incrementAndGet()));
                             invoiceEventStat.setPartyId(partyId);
                             invoiceEventStat.setPartyShopId(shopId);
                             invoiceEventStat.setEventCategory(InvoiceEventCategory.INVOICE);
                             invoiceEventStat.setInvoiceCreatedAt(start = start.plusHours(1));
                             return invoiceEventStat;
                         }
                     }
                ).collect(Collectors.toList());

        LocalDateTime toTime = invoiceEventStats.stream()
                .map(invoice -> invoice.getInvoiceCreatedAt())
                .max(LocalDateTime::compareTo)
                .get().plusHours(1);

        invoiceEventStats.forEach(invoiceEventDao::insert);

        long currentStep = 0;
        while (currentStep < count) {
            String query = String.format("{\"query\":{\"invoices\":{\"merchant_id\":\"%s\",\"shop_id\":\"%s\",\"from_time\":\"%s\",\"to_time\":\"%s\",\"from\":%d,\"size\":%d}}}",
                    partyId, shopId, TypeUtil.temporalToString(fromTime), TypeUtil.temporalToString(toTime), currentStep, size);
            StatResponse statResponse = queryProcessor.processQuery(query);

            assertEquals(count, statResponse.getTotalCount());
            List<StatInvoice> invoices = statResponse.getData().getInvoices();
            assertEquals(size, invoices.size());
            assertEquals(count - currentStep, (long) Long.valueOf(invoices.get(0).getId()));
            assertEquals(count - (currentStep += size) + 1, (long) Long.valueOf(invoices.get(invoices.size() - 1).getId()));
        }

    }

    @Test
    public void testPaymentsOffset() {
        EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .build();

        LocalDateTime fromTime = LocalDateTime.now(ZoneOffset.UTC);

        List<InvoiceEventStat> invoiceEventStats = enhancedRandom.objects(InvoiceEventStat.class, count)
                .map(new Function<InvoiceEventStat, InvoiceEventStat>() {
                         AtomicLong nextId = new AtomicLong();
                         LocalDateTime start = fromTime;

                         @Override
                         public InvoiceEventStat apply(InvoiceEventStat invoiceEventStat) {
                             invoiceEventStat.setPaymentId(String.valueOf(nextId.incrementAndGet()));
                             invoiceEventStat.setPartyId(partyId);
                             invoiceEventStat.setPartyShopId(shopId);
                             invoiceEventStat.setEventCategory(InvoiceEventCategory.PAYMENT);
                             invoiceEventStat.setPaymentFailureClass("operation_timeout");
                             invoiceEventStat.setPaymentTool("bank_card");
                             invoiceEventStat.setPaymentSystem("mastercard");
                             invoiceEventStat.setPaymentFlow("instant");
                             invoiceEventStat.setPaymentCreatedAt(start = start.plusHours(1));
                             return invoiceEventStat;
                         }
                     }
                ).collect(Collectors.toList());

        LocalDateTime toTime = invoiceEventStats.stream()
                .map(invoice -> invoice.getInvoiceCreatedAt())
                .max(LocalDateTime::compareTo)
                .get().plusHours(1);

        invoiceEventStats.forEach(invoiceEventDao::insert);

        long currentStep = 0;
        while (currentStep < count) {
            String query = String.format("{\"query\":{\"payments\":{\"merchant_id\":\"%s\",\"shop_id\":\"%s\",\"from_time\":\"%s\",\"to_time\":\"%s\",\"from\":%d,\"size\":%d}}}",
                    partyId, shopId, TypeUtil.temporalToString(fromTime), TypeUtil.temporalToString(toTime), currentStep, size);
            StatResponse statResponse = queryProcessor.processQuery(query);

            assertEquals(count, statResponse.getTotalCount());
            List<StatPayment> payments = statResponse.getData().getPayments();
            assertEquals(size, payments.size());
            assertEquals(count - currentStep, (long) Long.valueOf(payments.get(0).getId()));
            assertEquals(count - (currentStep += size) + 1, (long) Long.valueOf(payments.get(payments.size() - 1).getId()));
        }
    }

}
