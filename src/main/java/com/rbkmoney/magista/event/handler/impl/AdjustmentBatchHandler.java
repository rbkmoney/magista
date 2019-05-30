package com.rbkmoney.magista.event.handler.impl;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.AdjustmentData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.handler.BatchHandler;
import com.rbkmoney.magista.event.mapper.AdjustmentMapper;
import com.rbkmoney.magista.service.PaymentAdjustmentService;
import com.rbkmoney.magista.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AdjustmentBatchHandler implements BatchHandler<InvoiceChange, MachineEvent> {

    private final PaymentAdjustmentService paymentAdjustmentService;
    private final PaymentService paymentService;
    private final List<AdjustmentMapper> mappers;

    @Override
    public Processor handle(List<Map.Entry<InvoiceChange, MachineEvent>> changes) {
        List<AdjustmentData> adjustmentEvents = changes.stream()
                .map(changeWithParent -> {
                    InvoiceChange change = changeWithParent.getKey();
                    for (AdjustmentMapper adjustmentMapper : getMappers()) {
                        if (adjustmentMapper.accept(change)) {
                            return adjustmentMapper.map(changeWithParent.getKey(), changeWithParent.getValue());
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<PaymentData> adjustedPaymentEvents = adjustmentEvents.stream()
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

        return () -> {
            paymentAdjustmentService.saveAdjustments(adjustmentEvents);
            paymentService.savePayments(adjustedPaymentEvents);
        };
    }

    @Override
    public List<AdjustmentMapper> getMappers() {
        return mappers;
    }

}
