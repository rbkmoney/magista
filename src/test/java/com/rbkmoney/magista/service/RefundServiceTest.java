package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.AbstractDaoTest;
import com.rbkmoney.magista.dao.impl.RefundDaoImpl;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
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


@ContextConfiguration(classes = {PaymentRefundService.class, RefundDaoImpl.class})
public class RefundServiceTest extends AbstractDaoTest {

    @Autowired
    public PaymentRefundService paymentRefundService;

    @MockBean
    public PaymentService paymentService;

    @Before
    public void setup() {
        given(paymentService.getPaymentData(any(), any()))
                .willReturn(random(PaymentData.class));
    }

    @Test
    public void testSaveRefund() {
        List<RefundData> refundDataList = randomStreamOf(10, RefundData.class)
                .map(refundData -> {
                    refundData.setInvoiceId("invoiceId");
                    refundData.setPaymentId("paymentId");
                    refundData.setRefundId("refundId");
                    return refundData;
                })
                .collect(Collectors.toList());
        refundDataList.get(0).setEventType(InvoiceEventType.INVOICE_PAYMENT_REFUND_CREATED);

        paymentRefundService.saveRefunds(refundDataList);
    }

}
