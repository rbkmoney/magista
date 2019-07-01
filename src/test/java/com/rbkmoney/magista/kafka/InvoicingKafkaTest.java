package com.rbkmoney.magista.kafka;

import com.rbkmoney.damsel.domain.Invoice;
import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.magista.converter.BinaryConverterImpl;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.event.handler.impl.AdjustmentBatchHandler;
import com.rbkmoney.magista.event.handler.impl.InvoiceBatchHandler;
import com.rbkmoney.magista.event.handler.impl.PaymentBatchHandler;
import com.rbkmoney.magista.event.handler.impl.RefundBatchHandler;
import com.rbkmoney.magista.event.mapper.AdjustmentMapper;
import com.rbkmoney.magista.event.mapper.impl.*;
import com.rbkmoney.magista.listener.InvoiceListener;
import com.rbkmoney.magista.provider.GeoProvider;
import com.rbkmoney.magista.service.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TBase;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableConfigurationProperties
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
        AdjustmentMapper.class,
        AdjustmentStatusChangedMapper.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class InvoicingKafkaTest {

    public static final String SOURCE_ID = "source_id";
    public static final String SOURCE_NS = "source_ns";

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


    @Before
    public void setup() {
        given(geoProvider.getLocationInfo(any()))
                .willReturn(new LocationInfo(-1, -1));
    }

    @Test
    public void listenPaymentChanges() {
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

        EventPayload eventPayload = EventPayload.invoice_changes(
                Arrays.asList(
                        InvoiceChange.invoice_created(
                                new InvoiceCreated(invoice)
                        ),
                        InvoiceChange.invoice_status_changed(
                                new InvoiceStatusChanged(InvoiceStatus.paid(new InvoicePaid()))
                        ),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_started(
                                                new InvoicePaymentStarted(payment)
                                        )
                                )
                        ),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_status_changed(
                                                new InvoicePaymentStatusChanged(
                                                        InvoicePaymentStatus.captured(new InvoicePaymentCaptured())
                                                )
                                        )
                                )
                        ),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_refund_change(
                                                new InvoicePaymentRefundChange(
                                                        invoicePaymentRefund.getId(),
                                                        InvoicePaymentRefundChangePayload.invoice_payment_refund_created(
                                                                new InvoicePaymentRefundCreated(invoicePaymentRefund, new ArrayList<>())
                                                        )
                                                )
                                        )
                                )
                        ),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_refund_change(
                                                new InvoicePaymentRefundChange(
                                                        invoicePaymentRefund.getId(),
                                                        InvoicePaymentRefundChangePayload.invoice_payment_refund_status_changed(
                                                                new InvoicePaymentRefundStatusChanged(
                                                                        InvoicePaymentRefundStatus.succeeded(
                                                                                new InvoicePaymentRefundSucceeded()
                                                                        )
                                                                )
                                                        )
                                                )

                                        )
                                )
                        ),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_adjustment_change(
                                                new InvoicePaymentAdjustmentChange(
                                                        invoicePaymentAdjustment.getId(),
                                                        InvoicePaymentAdjustmentChangePayload.invoice_payment_adjustment_created(
                                                                new InvoicePaymentAdjustmentCreated(invoicePaymentAdjustment)
                                                        )
                                                )
                                        )
                                )
                        ),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_adjustment_change(
                                                new InvoicePaymentAdjustmentChange(
                                                        invoicePaymentAdjustment.getId(),
                                                        InvoicePaymentAdjustmentChangePayload.invoice_payment_adjustment_status_changed(
                                                                new InvoicePaymentAdjustmentStatusChanged(
                                                                        InvoicePaymentAdjustmentStatus.captured(new InvoicePaymentAdjustmentCaptured(
                                                                                        Instant.now().toString()
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        ),
                        InvoiceChange.invoice_payment_change(
                                new InvoicePaymentChange(payment.getId(),
                                        InvoicePaymentChangePayload.invoice_payment_session_change(
                                                new InvoicePaymentSessionChange(
                                                        TargetInvoicePaymentStatus.processed(new InvoicePaymentProcessed()),
                                                        SessionChangePayload.session_transaction_bound(sessionTransactionBound)
                                                )
                                        )
                                )
                        )
                )
        );
        message.setData(Value.bin(toByteArray(eventPayload)));

        invoiceListener.handle(Arrays.asList(message), null);

        verify(invoiceService).saveInvoices(any());
        verify(paymentService, times(2)).savePayments(any());
        verify(paymentRefundService).saveRefunds(any());
        verify(paymentAdjustmentService).saveAdjustments(any());
    }

    @SneakyThrows
    public <T extends TBase> T fillTBaseObject(T tBase, Class<T> type) {
        return new MockTBaseProcessor(MockMode.RANDOM, 15, 1)
                .process(tBase, new TBaseHandler<>(type));
    }

    @SneakyThrows
    public byte[] toByteArray(TBase tBase) {
        return new TSerializer(new TBinaryProtocol.Factory()).serialize(tBase);
    }


}
