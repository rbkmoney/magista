package com.rbkmoney.magista;

import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.util.BeanUtil;
import org.junit.jupiter.api.Test;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.*;

public class BeanUtilTest {

    @Test
    public void testMerge() {
        PaymentData source = random(PaymentData.class);
        PaymentData target = new PaymentData();

        BeanUtil.merge(source, target);
        assertEquals(source, target);
    }

    @Test
    public void testMergeIgnore() {
        PaymentData source = random(PaymentData.class);
        PaymentData target = new PaymentData();

        BeanUtil.merge(source, target, "id");
        source.setId(null);
        assertEquals(source, target);
    }

    @Test
    public void testMergeWithTwoDifferentObjects() {
        PaymentData source = random(PaymentData.class);
        PaymentData target = random(PaymentData.class, "id", "paymentOperationFailureClass", "paymentExternalFailure");

        BeanUtil.merge(source, target, "id");
        assertNotEquals(source, target);

        assertNull(target.getId());
        assertNotEquals(source.getId(), target.getId());
        assertEquals(source.getPaymentOperationFailureClass(), target.getPaymentOperationFailureClass());
        assertEquals(source.getPaymentExternalFailure(), target.getPaymentExternalFailure());
    }


}
