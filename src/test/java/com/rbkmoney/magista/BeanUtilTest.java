package com.rbkmoney.magista;

import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.tables.pojos.Adjustment;
import com.rbkmoney.magista.domain.tables.pojos.PaymentEvent;
import com.rbkmoney.magista.util.BeanUtil;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.*;

public class BeanUtilTest {

    @Test
    public void testTOStringMap() {
        Adjustment adjustment = new Adjustment();
        adjustment.setAdjustmentId("adj1");
        adjustment.setAdjustmentAmount(5000L);
        adjustment.setAdjustmentStatus(AdjustmentStatus.captured);
        adjustment.setEventCreatedAt(LocalDateTime.parse("2020-10-14T17:22:42.086188"));

        Map<String, String> adjustmentStringMap = BeanUtil.toStringMap(adjustment);
        assertEquals("adj1", adjustmentStringMap.get("adjustment_id"));
        assertEquals("2020-10-14T17:22:42.086188Z", adjustmentStringMap.get("event_created_at"));
        assertEquals("captured", adjustmentStringMap.get("adjustment_status"));
        assertEquals("5000", adjustmentStringMap.get("adjustment_amount"));
    }

    @Test
    public void testMerge() {
        PaymentEvent source = random(PaymentEvent.class);
        PaymentEvent target = new PaymentEvent();

        BeanUtil.merge(source, target);
        assertEquals(source, target);
    }

    @Test
    public void testMergeIgnore() {
        PaymentEvent source = random(PaymentEvent.class);
        PaymentEvent target = new PaymentEvent();

        BeanUtil.merge(source, target, "id");
        source.setId(null);
        assertEquals(source, target);
    }

    @Test
    public void testMergeWithTwoDifferentObjects() {
        PaymentEvent source = random(PaymentEvent.class);
        PaymentEvent target = random(PaymentEvent.class, "id", "paymentOperationFailureClass", "paymentExternalFailure");

        BeanUtil.merge(source, target, "id");
        assertNotEquals(source, target);

        assertNull(target.getId());
        assertNotEquals(source.getId(), target.getId());
        assertEquals(source.getPaymentOperationFailureClass(), target.getPaymentOperationFailureClass());
        assertEquals(source.getPaymentExternalFailure(), target.getPaymentExternalFailure());
    }


}
