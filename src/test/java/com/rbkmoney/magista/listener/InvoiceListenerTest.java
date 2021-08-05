package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.domain.Invoice;
import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.damsel.domain.InvoicePaymentRefund;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.TestData;
import com.rbkmoney.magista.converter.BinaryConverterImpl;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.dao.impl.AllocationDaoImpl;
import com.rbkmoney.magista.dao.impl.mapper.AllocationRowMapper;
import com.rbkmoney.magista.event.handler.impl.*;
import com.rbkmoney.magista.event.mapper.impl.*;
import com.rbkmoney.magista.provider.GeoProvider;
import com.rbkmoney.magista.service.PaymentService;
import com.rbkmoney.magista.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.*;

import static com.rbkmoney.testcontainers.annotations.util.RandomBeans.randomThriftOnlyRequiredFields;
import static com.rbkmoney.testcontainers.annotations.util.ThriftUtil.toByteArray;
import static com.rbkmoney.testcontainers.annotations.util.ValuesGenerator.generateString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ContextConfiguration(classes = {
        InvoicingListener.class,
        HandlerManager.class,
        SourceEventParser.class,
        BinaryConverterImpl.class,
        InvoiceBatchHandler.class,
        InvoiceCreatedEventMapper.class,
        InvoiceStatusChangedEventMapper.class,
        PaymentBatchHandler.class,
        PaymentStartedEventMapper.class,
        PaymentStatusChangedEventMapper.class,
        PaymentTransactionBoundMapper.class,
        RefundBatchHandler.class,
        RefundCreatedMapper.class,
        RefundStatusChangedMapper.class,
        AdjustmentBatchHandler.class,
        AdjustmentCreatedMapper.class,
        AdjustmentStatusChangedMapper.class,
        ChargebackBatchHandler.class,
        ChargebackCreatedMapper.class,
        AllocationCreatedMapper.class,
        AllocationCapturedMapper.class,
        AllocationRefundCreateMapper.class,
        AllocationRowMapper.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class InvoiceListenerTest {

    private static final String SOURCE_ID = "source_id";
    private static final String SOURCE_NS = "source_ns";

    @Autowired
    private InvoicingListener invoicingListener;

    @MockBean
    private GeoProvider geoProvider;

    @MockBean
    private InvoiceService invoiceService;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private PaymentRefundService paymentRefundService;

    @MockBean
    private PaymentAdjustmentService paymentAdjustmentService;

    @MockBean
    private PaymentChargebackService paymentChargebackService;

    @MockBean
    private AllocationService allocationService;

    @BeforeEach
    public void setup() {
        given(geoProvider.getLocationInfo(any()))
                .willReturn(new LocationInfo(-1, -1));
    }

    @Test
    public void listenPaymentChanges() throws Exception {
        MachineEvent message = new MachineEvent();
        message.setCreatedAt(Instant.now().toString());
        message.setEventId(1L);
        message.setSourceNs(SOURCE_NS);
        message.setSourceId(SOURCE_ID);

        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setId("3542543");
        transactionInfo.setExtra(Collections.emptyMap());
        AdditionalTransactionInfo additionalTransactionInfo = new AdditionalTransactionInfo();
        additionalTransactionInfo.setRrn("5436");
        additionalTransactionInfo.setApprovalCode("4326");
        transactionInfo.setAdditionalInfo(additionalTransactionInfo);

        SessionTransactionBound sessionTransactionBound = new SessionTransactionBound();
        sessionTransactionBound.setTrx(transactionInfo);

        InvoicePaymentCaptureParams invoicePaymentCaptureParams = new InvoicePaymentCaptureParams();
        invoicePaymentCaptureParams.setReason("test reason");
        Cash invoiceCartCash = new Cash(10L, new CurrencyRef("RUB"));
        InvoiceCart invoiceCart = new InvoiceCart()
                .setLines(
                        Collections.singletonList(
                                new InvoiceLine("test prod", 1, invoiceCartCash, new HashMap<>())));
        invoicePaymentCaptureParams.setCart(invoiceCart);
        invoicePaymentCaptureParams.setCash(new Cash(5L, new CurrencyRef("USD")));

        Invoice invoice = randomThriftOnlyRequiredFields(Invoice.class)
                .setId(SOURCE_ID)
                .setOwnerId(UUID.randomUUID().toString())
                .setCreatedAt(Instant.now().toString())
                .setDue(Instant.now().toString());
        InvoicePayment payment = randomThriftOnlyRequiredFields(InvoicePayment.class)
                .setId(SOURCE_ID)
                .setOwnerId(UUID.randomUUID().toString())
                .setFlow(InvoicePaymentFlow.instant(new InvoicePaymentFlowInstant()))
                .setPayer(Payer.payment_resource(new PaymentResourcePayer()
                        .setResource(new DisposablePaymentResource()
                                .setPaymentTool(PaymentTool.bank_card(
                                        new BankCard(generateString(), generateString(), generateString()))))
                        .setContactInfo(new ContactInfo())))
                .setCreatedAt(Instant.now().toString());
        InvoicePaymentRefund invoicePaymentRefund = randomThriftOnlyRequiredFields(InvoicePaymentRefund.class)
                .setCreatedAt(Instant.now().toString());
        InvoicePaymentAdjustment invoicePaymentAdjustment =
                randomThriftOnlyRequiredFields(InvoicePaymentAdjustment.class)
                        .setCreatedAt(Instant.now().toString());
        EventPayload eventPayload = EventPayload.invoice_changes(
                Arrays.asList(
                        InvoiceChange.invoice_created(
                                new InvoiceCreated(invoice)),
                        InvoiceChange.invoice_status_changed(
                                new InvoiceStatusChanged(InvoiceStatus.paid(new InvoicePaid()))),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_started(
                                                new InvoicePaymentStarted(payment)))),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_status_changed(
                                                new InvoicePaymentStatusChanged(
                                                        InvoicePaymentStatus.captured(new InvoicePaymentCaptured()))))),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_refund_change(
                                                getInvoicePaymentRefundChange(invoicePaymentRefund)))),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_refund_change(
                                                new InvoicePaymentRefundChange(
                                                        invoicePaymentRefund.getId(),
                                                        InvoicePaymentRefundChangePayload
                                                                .invoice_payment_refund_status_changed(
                                                                        getInvoicePaymentRefundStatusChanged()))))),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_adjustment_change(
                                                new InvoicePaymentAdjustmentChange(
                                                        invoicePaymentAdjustment.getId(),
                                                        InvoicePaymentAdjustmentChangePayload
                                                                .invoice_payment_adjustment_created(
                                                                        new InvoicePaymentAdjustmentCreated(
                                                                                invoicePaymentAdjustment)))))),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_adjustment_change(
                                                new InvoicePaymentAdjustmentChange(
                                                        invoicePaymentAdjustment.getId(),
                                                        InvoicePaymentAdjustmentChangePayload
                                                                .invoice_payment_adjustment_status_changed(
                                                                        getInvoicePaymentAdjustmentStatusChanged()))))),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_session_change(
                                                new InvoicePaymentSessionChange(
                                                        TargetInvoicePaymentStatus
                                                                .processed(new InvoicePaymentProcessed()),
                                                        SessionChangePayload
                                                                .session_transaction_bound(sessionTransactionBound))))),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_capture_started(
                                                new InvoicePaymentCaptureStarted(
                                                        invoicePaymentCaptureParams)))),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_chargeback_change(
                                                new InvoicePaymentChargebackChange(
                                                        "testId",
                                                        TestData.buildInvoiceChargebackChangePayload()))))));
        message.setData(toByteArray(eventPayload));

        invoicingListener.handleMessages(Arrays.asList(message));

        verify(invoiceService).saveInvoices(any());
        verify(paymentService).savePayments(any());
        verify(paymentRefundService).saveRefunds(any());
        verify(paymentAdjustmentService).saveAdjustments(any());
        verify(paymentChargebackService).saveChargeback(any());
    }

    private InvoicePaymentRefundChange getInvoicePaymentRefundChange(InvoicePaymentRefund invoicePaymentRefund) {
        return new InvoicePaymentRefundChange(
                invoicePaymentRefund.getId(),
                InvoicePaymentRefundChangePayload
                        .invoice_payment_refund_created(
                                new InvoicePaymentRefundCreated(
                                        invoicePaymentRefund, new ArrayList<>())));
    }

    private InvoicePaymentRefundStatusChanged getInvoicePaymentRefundStatusChanged() {
        return new InvoicePaymentRefundStatusChanged(
                InvoicePaymentRefundStatus.succeeded(
                        new InvoicePaymentRefundSucceeded()));
    }

    private InvoicePaymentAdjustmentStatusChanged getInvoicePaymentAdjustmentStatusChanged() {
        return new InvoicePaymentAdjustmentStatusChanged(
                InvoicePaymentAdjustmentStatus.captured(
                        new InvoicePaymentAdjustmentCaptured(
                                Instant.now().toString())));
    }
}
