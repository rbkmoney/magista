package com.rbkmoney.magista.service;

import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.pojos.RefundData;
import com.rbkmoney.testcontainers.annotations.postgresql.WithPostgresqlSingletonSpringBootITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.magista.util.RandomBeans.random;
import static com.rbkmoney.magista.util.RandomBeans.randomStreamOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WithPostgresqlSingletonSpringBootITest
public class RefundServiceTest {

    @Autowired
    public PaymentRefundService paymentRefundService;

    @MockBean
    public PaymentService paymentService;

    @BeforeEach
    public void setup() {
        given(paymentService.getPaymentData(any(), any()))
                .willReturn(random(PaymentData.class));
    }

    @Test
    public void testSaveRefund() {
        List<RefundData> refundDataList = randomStreamOf(10, RefundData.class)
                .peek(refundData -> {
                    refundData.setInvoiceId("invoiceId");
                    refundData.setPaymentId("paymentId");
                    refundData.setRefundId("refundId");
                })
                .collect(Collectors.toList());
        refundDataList.get(0).setEventType(InvoiceEventType.INVOICE_PAYMENT_REFUND_CREATED);

        paymentRefundService.saveRefunds(refundDataList);
    }
}
