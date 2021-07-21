package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.domain.Invoice;
import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.damsel.domain.InvoicePaymentRefund;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.serializer.kit.mock.FieldHandler;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.magista.TestData;
import com.rbkmoney.magista.converter.BinaryConverterImpl;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.event.handler.impl.*;
import com.rbkmoney.magista.event.mapper.impl.*;
import com.rbkmoney.magista.provider.GeoProvider;
import com.rbkmoney.magista.service.PaymentService;
import com.rbkmoney.magista.service.*;
import lombok.SneakyThrows;
import org.apache.thrift.TBase;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        InvoiceListener.class,
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
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class InvoiceListenerTest {

    public static final String SOURCE_ID = "source_id";
    public static final String SOURCE_NS = "source_ns";
    private static final Map.Entry<FieldHandler, String[]> timeFields = Map.entry(
            structHandler -> structHandler.value(Instant.now().toString()),
            new String[]{"created_at", "at", "due"}
    );

    @Autowired
    private InvoiceListener invoiceListener;

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

    private MockTBaseProcessor mockTBaseProcessor;

    @BeforeEach
    public void setup() {
        mockTBaseProcessor = new MockTBaseProcessor(MockMode.ALL, 20, 1);
        mockTBaseProcessor.addFieldHandler(timeFields.getKey(), timeFields.getValue());

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

        Invoice invoice = new Invoice()
                .setId(SOURCE_ID)
                .setOwnerId(UUID.randomUUID().toString())
                .setCreatedAt(Instant.now().toString())
                .setDue(Instant.now().toString());
        invoice = fillTBaseObject(invoice, Invoice.class);

        InvoicePayment payment = new InvoicePayment()
                .setId(SOURCE_ID)
                .setOwnerId(UUID.randomUUID().toString())
                .setFlow(InvoicePaymentFlow.instant(new InvoicePaymentFlowInstant()))
                .setPayer(
                        Payer.payment_resource(
                                new PaymentResourcePayer().setResource(
                                        new DisposablePaymentResource().setPaymentTool(
                                                PaymentTool.bank_card(new BankCard()
                                                )
                                        )
                                )
                        )
                )
                .setCreatedAt(Instant.now().toString());
        payment = fillTBaseObject(payment, InvoicePayment.class);

        InvoicePaymentRefund invoicePaymentRefund = new InvoicePaymentRefund()
                .setCreatedAt(Instant.now().toString());
        invoicePaymentRefund = fillTBaseObject(invoicePaymentRefund, InvoicePaymentRefund.class);

        InvoicePaymentAdjustment invoicePaymentAdjustment = new InvoicePaymentAdjustment()
                .setCreatedAt(Instant.now().toString());
        invoicePaymentAdjustment = fillTBaseObject(invoicePaymentAdjustment, InvoicePaymentAdjustment.class);

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
        message.setData(Value.bin(toByteArray(eventPayload)));

        invoiceListener.handleMessages(Arrays.asList(message));

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

    @SneakyThrows
    public <T extends TBase> T fillTBaseObject(T data, Class<T> type) {
        return mockTBaseProcessor.process(data, new TBaseHandler<>(type));
    }

    @SneakyThrows
    public byte[] toByteArray(TBase data) {
        return new TSerializer(new TBinaryProtocol.Factory()).serialize(data);
    }
}
