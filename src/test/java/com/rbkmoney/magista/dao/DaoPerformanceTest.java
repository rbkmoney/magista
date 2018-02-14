package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.AbstractIntegrationTest;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoicePaymentRefundStatus;
import com.rbkmoney.magista.domain.enums.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.LongStream;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;

/**
 * Created by tolkonepiu on 16/06/2017.
 */
/*
Result insertion of 10000 events (without jooq), time = 139207 ms, average = 13.9207, min = 9, max = 425
Result updates of 10000 events (without jooq), time = 134775 ms, average = 13.4775, min = 9, max = 368
Result insertion of 10000 events (pojo), time = 137330 ms, average = 13.733, min = 9, max = 338
Result updates of 10000 events (pojo), time = 140430 ms, average = 14.043, min = 9, max = 1111
Result insertion of 10000 events (record), time = 147695 ms, average = 14.7695, min = 9, max = 452
Result updates of 10000 events (record), time = 136864 ms, average = 13.6864, min = 9, max = 45
 */
@Ignore
public class DaoPerformanceTest extends AbstractIntegrationTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InvoiceEventDao invoiceEventDao;

    //Because if more, then slower. Say thanks to Docker.
    public final int EVENTS_COUNT = 1000;

    @Test
    public void processManyRequestsWithStatTest() {
        List<InvoiceEventStat> invoiceEventStatList = randomListOf(EVENTS_COUNT, InvoiceEventStat.class, "id");

        speedTest(
                "insert-invoices",
                invoiceEventStatList,
                invoiceEventDao::insert,
                event -> {
                    event.setEventCategory(InvoiceEventCategory.INVOICE);
                    return event;
                }
        );

        speedTest(
                "insert-payments",
                invoiceEventStatList,
                invoiceEventDao::insert,
                event -> {
                    event.setEventCategory(InvoiceEventCategory.PAYMENT);
                    event.setPaymentStatus(InvoicePaymentStatus.captured);
                    event.setEventCreatedAt(LocalDateTime.now());
                    return event;
                }
        );

        speedTest(
                "insert-refunds",
                invoiceEventStatList,
                invoiceEventDao::insert,
                event -> {
                    event.setEventCategory(InvoiceEventCategory.REFUND);
                    event.setPaymentRefundStatus(InvoicePaymentRefundStatus.succeeded);
                    event.setEventCreatedAt(LocalDateTime.now());
                    return event;
                }
        );

        speedTest(
                "insert-adjustments",
                invoiceEventStatList,
                invoiceEventDao::insert,
                event -> {
                    event.setEventCategory(InvoiceEventCategory.ADJUSTMENT);
                    event.setPaymentAdjustmentStatus(AdjustmentStatus.captured);
                    event.setEventCreatedAt(LocalDateTime.now());
                    return event;
                }
        );

        speedTest(
                "select-invoices",
                invoiceEventStatList,
                event -> invoiceEventDao.findInvoiceById(event.getInvoiceId()),
                Function.identity()
        );

        speedTest(
                "select-payments",
                invoiceEventStatList,
                event -> invoiceEventDao.findPaymentByIds(event.getInvoiceId(), event.getPaymentId()),
                Function.identity()
        );

        speedTest(
                "select-refunds",
                invoiceEventStatList,
                event -> invoiceEventDao.findRefundByIds(event.getInvoiceId(), event.getPaymentId(), event.getPaymentRefundId()),
                Function.identity()
        );

        speedTest(
                "select-adjustments",
                invoiceEventStatList,
                event -> invoiceEventDao.findAdjustmentByIds(event.getInvoiceId(), event.getPaymentId(), event.getPaymentAdjustmentId()),
                Function.identity()
        );
    }

    private <T> void speedTest(String op, List<T> events, Consumer<T> action, Function<? super T, ? extends T> before) {
        LongSummaryStatistics statistics = events.stream()
                .map(before)
                .flatMapToLong(
                        event -> {
                            long currentTime = System.currentTimeMillis();
                            action.accept(event);
                            return LongStream.of(System.currentTimeMillis() - currentTime);
                        }
                ).summaryStatistics();
        log.info("Result of processing: {}, {} events, time = {} ms, average = {}, min = {}, max = {}",
                op,
                statistics.getCount(), statistics.getSum(), statistics.getAverage(),
                statistics.getMin(), statistics.getMax());
    }

}
