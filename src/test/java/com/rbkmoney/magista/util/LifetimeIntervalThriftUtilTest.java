package com.rbkmoney.magista.util;

import com.rbkmoney.damsel.domain.LifetimeInterval;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.rbkmoney.magista.util.LifetimeIntervalThriftUtil.getInvoiceValidUntil;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LifetimeIntervalThriftUtilTest {

    @Test
    public void testLifetimeInterval() {
        short expected = 1;
        LifetimeInterval lifetimeInterval = new LifetimeInterval()
                .setSeconds(expected)
                .setDays(expected)
                .setYears(expected);
        LocalDateTime expectedTime = LocalDateTime.MIN;
        LocalDateTime invoiceValidUntil = getInvoiceValidUntil(expectedTime, lifetimeInterval);

        assertThat(
                expectedTime
                        .plusSeconds(expected)
                        .plusHours(0)
                        .plusDays(expected)
                        .plusYears(expected))
                .isEqualTo(invoiceValidUntil);
    }
}
