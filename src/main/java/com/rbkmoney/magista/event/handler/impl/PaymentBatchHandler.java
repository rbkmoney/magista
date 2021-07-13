package com.rbkmoney.magista.event.handler.impl;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.handler.BatchHandler;
import com.rbkmoney.magista.event.mapper.PaymentMapper;
import com.rbkmoney.magista.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentBatchHandler implements BatchHandler<InvoiceChange, MachineEvent> {

    private final PaymentService paymentService;
    private final List<PaymentMapper> mappers;

    @Override
    public Processor handle(List<Map.Entry<InvoiceChange, MachineEvent>> changes) {
        List<PaymentData> paymentEvents = changes.stream()
                .map(changeWithParent -> {
                    InvoiceChange change = changeWithParent.getKey();
                    for (PaymentMapper paymentMapper : getMappers()) {
                        if (paymentMapper.accept(change)) {
                            return paymentMapper.map(changeWithParent.getKey(), changeWithParent.getValue());
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return () -> paymentService.savePayments(paymentEvents);
    }

    @Override
    public List<PaymentMapper> getMappers() {
        return mappers;
    }
}
