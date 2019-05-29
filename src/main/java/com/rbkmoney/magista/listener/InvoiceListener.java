package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.pojos.AdjustmentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.domain.tables.pojos.RefundData;
import com.rbkmoney.magista.event.*;
import com.rbkmoney.magista.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceListener implements MessageListener {

    private final HandlerManager handlerManager;
    private final SourceEventParser eventParser;

    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final PaymentRefundService paymentRefundService;
    private final PaymentAdjustmentService paymentAdjustmentService;

    @KafkaListener(topics = "${kafka.topics.invoicing}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(List<MachineEvent> messages, Acknowledgment ack) {
        handle(messages, ack);
        ack.acknowledge();
    }

    @Override
    public void handle(List<MachineEvent> machineEvents, Acknowledgment ack) {
        List<InvoiceData> invoiceDataList = new ArrayList<>();
        List<PaymentData> paymentDataList = new ArrayList<>();
        List<RefundData> refundDataList = new ArrayList<>();
        List<AdjustmentData> adjustmentDataList = new ArrayList<>();
        for (MachineEvent machineEvent : machineEvents) {
            EventPayload eventPayload = eventParser.parseEvent(machineEvent);
            if (eventPayload.isSetInvoiceChanges()) {
                List<InvoiceChange> invoiceChanges = eventPayload.getInvoiceChanges();
                for (InvoiceChange invoiceChange : invoiceChanges) {
                    Handler handler = handlerManager.getHandler(invoiceChange);
                    if (handler instanceof InvoiceHandler) {
                        InvoiceHandler invoiceHandler = (InvoiceHandler) handler;
                        InvoiceData invoiceData = invoiceHandler.handle(invoiceChange, machineEvent);
                        invoiceDataList.add(invoiceData);
                    }
                    if (handler instanceof PaymentHandler) {
                        PaymentHandler paymentHandler = (PaymentHandler) handler;
                        PaymentData paymentData = paymentHandler.handle(invoiceChange, machineEvent);
                        paymentDataList.add(paymentData);
                    }
                    if (handler instanceof RefundHandler) {
                        RefundHandler refundHandler = (RefundHandler) handler;
                        RefundData refundData = refundHandler.handle(invoiceChange, machineEvent);
                        refundDataList.add(refundData);
                    }
                    if (handler instanceof AdjustmentHandler) {
                        AdjustmentHandler adjustmentHandler = (AdjustmentHandler) handler;
                        AdjustmentData adjustmentData = adjustmentHandler.handle(invoiceChange, machineEvent);
                        adjustmentDataList.add(adjustmentData);
                    }
                }
            }
        }

        List<PaymentData> adjustedPaymentDataList = adjustmentDataList.stream()
                .filter(adjustmentData -> adjustmentData.getAdjustmentStatus() == AdjustmentStatus.captured)
                .map(adjustmentData -> {
                            PaymentData paymentData = new PaymentData();
                            paymentData.setEventType(InvoiceEventType.INVOICE_PAYMENT_ADJUSTED);
                            paymentData.setEventId(adjustmentData.getEventId());
                            paymentData.setEventCreatedAt(adjustmentData.getEventCreatedAt());
                            paymentData.setInvoiceId(adjustmentData.getInvoiceId());
                            paymentData.setPaymentId(adjustmentData.getPaymentId());
                            paymentData.setPaymentFee(adjustmentData.getAdjustmentFee());
                            paymentData.setPaymentProviderFee(adjustmentData.getAdjustmentProviderFee());
                            paymentData.setPaymentExternalFee(adjustmentData.getAdjustmentExternalFee());
                            paymentData.setPaymentDomainRevision(adjustmentData.getAdjustmentDomainRevision());
                            return paymentData;
                        }
                )
                .collect(Collectors.toList());

        invoiceService.saveInvoices(invoiceDataList);
        paymentService.savePayments(paymentDataList);
        paymentRefundService.saveRefunds(refundDataList);
        paymentAdjustmentService.saveAdjustments(adjustmentDataList);
        paymentService.savePayments(adjustedPaymentDataList);
    }
}
