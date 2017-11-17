package com.rbkmoney.magista;

import com.rbkmoney.damsel.merch_stat.MerchantStatisticsSrv;
import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.dao.InvoiceEventDao;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LongSummaryStatistics;
import java.util.function.Function;
import java.util.stream.IntStream;

@Ignore
public class OffsetPerformanceTest extends AbstractIntegrationTest {

    private MerchantStatisticsSrv.Iface client;

    @LocalServerPort
    private int localPort;

    @Autowired
    InvoiceEventDao invoiceEventDao;

    LocalDate start = LocalDate.of(2017, 01, 01);

    LocalDate end = LocalDate.of(2018, 01, 01);

    int count = 80_000;

    @Before
    public void setup() throws URISyntaxException {
        EnhancedRandom enhancedRandom = EnhancedRandomBuilder
                .aNewEnhancedRandomBuilder()
                .dateRange(start, end)
                .build();

        enhancedRandom.objects(InvoiceEventStat.class, count, "invoiceCart")
                .map(invoiceEventStat -> {
                    invoiceEventStat.setPaymentFailureClass("operation_timeout");
                    invoiceEventStat.setPaymentTool("bank_card");
                    invoiceEventStat.setPaymentSystem("mastercard");
                    invoiceEventStat.setPaymentFlow("instant");
                    return invoiceEventStat;
                })
                .parallel()
                .forEach(invoiceEventStat -> invoiceEventDao.insert(invoiceEventStat));

        client = new THSpawnClientBuilder()
                .withNetworkTimeout(-1)
                .withAddress(new URI("http://localhost:" + localPort + "/stat")).build(MerchantStatisticsSrv.Iface.class);
    }

    @Test
    public void offsetTest() throws TException {
        doOffset("invoices", statRequest -> {
            try {
                return client.getInvoices(statRequest);
            } catch (TException ex) {
                throw new RuntimeException(ex);
            }
        }, start.atStartOfDay(), end.atStartOfDay(), 1000, 1000);

        doOffset("payments", statRequest -> {
            try {
                return client.getPayments(statRequest);
            } catch (TException ex) {
                throw new RuntimeException(ex);
            }
        }, start.atStartOfDay(), end.atStartOfDay(), 1000, 1000);
    }

    private void doOffset(String category, Function<StatRequest, StatResponse> action, LocalDateTime fromTime, LocalDateTime toTime, int offsetStep, int size) throws TException {
        LongSummaryStatistics statistics = IntStream.rangeClosed(1, (count / 2) / offsetStep)
                .parallel()
                .mapToLong(
                        offset -> {
                            String dsl = String.format("{\"query\":{\"%s\":{\"from_time\":\"%s\",\"to_time\":\"%s\",\"from\":%d,\"size\":%d}}}",
                                    category, TypeUtil.temporalToString(fromTime), TypeUtil.temporalToString(toTime), offset * offsetStep, size);

                            long start = System.currentTimeMillis();
                            action.apply(new StatRequest(dsl));
                            return System.currentTimeMillis() - start;
                        }
                ).summaryStatistics();

        System.out.println(String.format("%s: requestCount=%d, averageTime=%f, minTime=%d, maxTime=%d", category, statistics.getCount(), statistics.getAverage(), statistics.getMin(), statistics.getMax()));
    }

}
