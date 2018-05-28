package com.rbkmoney.magista;

import com.rbkmoney.damsel.domain.InvoiceCart;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.magista.util.DamselUtil;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class DamselUtilTest {

    @Test
    public void jsonTest() throws IOException {
        InvoiceCart cart = new InvoiceCart();
        MockTBaseProcessor mockTBaseProcessor = new MockTBaseProcessor();
        mockTBaseProcessor.addFieldHandler((t) -> {
            t.beginMap(0);
            t.endMap();
        }, "metadata");
        cart = mockTBaseProcessor.process(cart, new TBaseHandler<>(InvoiceCart.class));

        String jsonCart = DamselUtil.toJsonString(cart);
        assertEquals(cart, DamselUtil.fromJson(jsonCart, InvoiceCart.class));

    }

}
