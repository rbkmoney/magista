package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.domain.InvoicePaymentAdjustmentPending;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.dao.AbstractDaoTest;
import com.rbkmoney.magista.dao.impl.AdjustmentDaoImpl;
import com.rbkmoney.magista.domain.tables.pojos.AdjustmentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.event.handler.impl.AdjustmentBatchHandler;
import com.rbkmoney.magista.event.mapper.impl.AdjustmentCreatedMapper;
import com.rbkmoney.magista.event.mapper.impl.AdjustmentStatusChangedMapper;
import com.rbkmoney.magista.service.PaymentAdjustmentService;
import com.rbkmoney.magista.service.PaymentService;
import org.assertj.core.util.Lists;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableConfigurationProperties
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {
        AdjustmentBatchHandler.class, AdjustmentCreatedMapper.class, AdjustmentStatusChangedMapper.class,
        PaymentAdjustmentService.class, AdjustmentDaoImpl.class
})
@TestPropertySource(properties = {"cache.invoiceData.size=10", "cache.paymentData.size=10"})
public class AdjustmentInvoiceListenerTest extends AbstractDaoTest {

    @Autowired
    private AdjustmentBatchHandler adjustmentBatchHandler;

    @Autowired
    private PaymentAdjustmentService paymentAdjustmentService;

    @MockBean
    private PaymentService paymentService;

    @Before
    public void setup() {
        given(paymentService.getPaymentData(any(), any())).willReturn(random(PaymentData.class));
        doAnswer(
                answer -> {
                    List<PaymentData> paymentEvents = answer.getArgument(0);
                    assertEquals(1, paymentEvents.size());
                    PaymentData adjPaymentData = paymentEvents.get(0);
                    assertEquals(0L, (long) adjPaymentData.getPaymentFee());

                    return null;
                }
        ).when(paymentService).savePayments(any());
    }

    @Test
    public void test() {
        List<Map.Entry<InvoiceChange, MachineEvent>> events = List.of(
                Map.entry(
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(
                                        "payment_id",
                                        InvoicePaymentChangePayload.invoice_payment_adjustment_change(
                                                new InvoicePaymentAdjustmentChange(
                                                        "adjustment_id",
                                                        InvoicePaymentAdjustmentChangePayload
                                                                .invoice_payment_adjustment_created(
                                                                        new InvoicePaymentAdjustmentCreated(
                                                                                new InvoicePaymentAdjustment()
                                                                                        .setId("adjustment_id")
                                                                                        .setCreatedAt(Instant.now()
                                                                                                .toString())
                                                                                        .setDomainRevision(1)
                                                                                        .setStatus(
                                                                                                InvoicePaymentAdjustmentStatus
                                                                                                        .pending(
                                                                                                                new InvoicePaymentAdjustmentPending()))
                                                                                        .setReason("reason")
                                                                                        .setPartyRevision(1)
                                                                                        .setNewCashFlow(
                                                                                                Lists.emptyList())
                                                                                        .setOldCashFlowInverse(
                                                                                                Lists.emptyList())
                                                                                        .setState(
                                                                                                buildInvoicePaymentAdjustmentState())
                                                                        )
                                                                )
                                                )
                                        )
                                )
                        ),
                        new MachineEvent()
                                .setSourceId("invoice_id")
                                .setCreatedAt(Instant.now().toString())
                ),
                Map.entry(
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(
                                        "payment_id",
                                        InvoicePaymentChangePayload.invoice_payment_adjustment_change(
                                                new InvoicePaymentAdjustmentChange(
                                                        "adjustment_id",
                                                        InvoicePaymentAdjustmentChangePayload
                                                                .invoice_payment_adjustment_status_changed(
                                                                        new InvoicePaymentAdjustmentStatusChanged(
                                                                                InvoicePaymentAdjustmentStatus.captured(
                                                                                        new InvoicePaymentAdjustmentCaptured(
                                                                                                Instant.now()
                                                                                                        .toString())
                                                                                )
                                                                        )
                                                                )
                                                )
                                        )
                                )
                        ),
                        new MachineEvent()
                                .setSourceId("invoice_id")
                                .setCreatedAt(Instant.now().toString())
                )
        );

        adjustmentBatchHandler.handle(events).execute();
        AdjustmentData adjustmentData =
                paymentAdjustmentService.getAdjustment("invoice_id", "payment_id", "adjustment_id");
        assertEquals(0L, (long) adjustmentData.getAdjustmentFee());

    }

    @NotNull
    private InvoicePaymentAdjustmentState buildInvoicePaymentAdjustmentState() {
        InvoicePaymentAdjustmentStatusChangeState invoicePaymentAdjustmentStatusChangeState =
                new InvoicePaymentAdjustmentStatusChangeState();
        InvoicePaymentAdjustmentStatusChange invoicePaymentAdjustmentStatusChange =
                new InvoicePaymentAdjustmentStatusChange();
        InvoicePaymentStatus invoicePaymentStatus = new InvoicePaymentStatus();
        invoicePaymentStatus.setFailed(
                new InvoicePaymentFailed(
                        OperationFailure.failure(
                                new Failure()
                                        .setCode("code")
                                        .setReason("reason")
                                        .setSub(
                                                new SubFailure()
                                                        .setCode("sub_failure_code")
                                        )
                        )
                )
        );
        invoicePaymentAdjustmentStatusChange.setTargetStatus(invoicePaymentStatus);
        invoicePaymentAdjustmentStatusChangeState.setScenario(invoicePaymentAdjustmentStatusChange);
        InvoicePaymentAdjustmentState invoicePaymentAdjustmentState = new InvoicePaymentAdjustmentState();
        invoicePaymentAdjustmentState.setStatusChange(invoicePaymentAdjustmentStatusChangeState);
        return invoicePaymentAdjustmentState;
    }

}
