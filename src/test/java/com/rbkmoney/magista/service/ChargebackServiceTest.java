package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.AbstractDaoTest;
import com.rbkmoney.magista.dao.ChargebackDao;
import com.rbkmoney.magista.dao.impl.ChargebackDaoImpl;
import com.rbkmoney.magista.dao.impl.RefundDaoImpl;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.ChargebackData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomStreamOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ContextConfiguration(classes = {PaymentChargebackService.class, ChargebackDaoImpl.class})
public class ChargebackServiceTest extends AbstractDaoTest {

    @Autowired
    public PaymentChargebackService paymentChargebackService;

    @MockBean
    public PaymentService paymentService;

    @SpyBean
    public ChargebackDao chargebackDao;

    @Before
    public void setup() {
        given(paymentService.getPaymentData(any(), any()))
                .willReturn(random(PaymentData.class));
    }

    @Test
    public void saveChargeback() {
        List<ChargebackData> chargebackList = randomStreamOf(10, ChargebackData.class)
                .peek(chargebackData -> {
                    chargebackData.setPaymentId("testPaymentId");
                    chargebackData.setInvoiceId("tetInvoiceId");
                    chargebackData.setChargebackId("testChargebackId");
                })
                .collect(Collectors.toList());
        chargebackList.get(0).setEventType(InvoiceEventType.INVOICE_PAYMENT_CHARGEBACK_CREATED);
        paymentChargebackService.saveChargeback(chargebackList);

        ArgumentCaptor<List<ChargebackData>> captor = ArgumentCaptor.forClass(List.class);
        verify(chargebackDao, times(1)).save(anyList());
        verify(chargebackDao).save(captor.capture());
        List<ChargebackData> enrichedChargebackData = captor.getValue();
        Assert.assertEquals(chargebackList.size(), enrichedChargebackData.size());

    }

}
