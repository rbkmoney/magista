package com.rbkmoney.magista;

import com.rbkmoney.magista.domain.tables.pojos.PaymentEvent;
import com.rbkmoney.magista.util.BeanUtil;
import org.junit.Test;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class BeanUtilTest {

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
