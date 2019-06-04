package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.AbstractDaoTest;
import com.rbkmoney.magista.dao.impl.AdjustmentDaoImpl;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.AdjustmentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.pojos.RefundData;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.Collectors;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomStreamOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ContextConfiguration(classes = {PaymentAdjustmentService.class, AdjustmentDaoImpl.class})
public class AdjustmentServiceTest extends AbstractDaoTest {

    @Autowired
    public PaymentAdjustmentService paymentAdjustmentService;

    @MockBean
    public PaymentService paymentService;

    @Before
    public void setup() {
        given(paymentService.getPaymentData(any(), any()))
                .willReturn(random(PaymentData.class));
    }

    @Test
    public void testSaveAdjustment() {
        List<AdjustmentData> adjustmentDataList = randomStreamOf(10, AdjustmentData.class)
                .map(adjustmentData -> {
                    adjustmentData.setInvoiceId("invoiceId");
                    adjustmentData.setPaymentId("paymentId");
                    adjustmentData.setAdjustmentId("adjustmentId");
                    return adjustmentData;
                })
                .collect(Collectors.toList());
        adjustmentDataList.get(0).setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTMENT_CREATED);

        paymentAdjustmentService.saveAdjustments(adjustmentDataList);
    }

}
