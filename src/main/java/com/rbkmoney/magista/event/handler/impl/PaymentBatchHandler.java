package com.rbkmoney.magista.event.handler.impl;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.tables.pojos.AllocationTransactionData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.handler.BatchHandler;
import com.rbkmoney.magista.event.mapper.AllocationMapper;
import com.rbkmoney.magista.event.mapper.Mapper;
import com.rbkmoney.magista.event.mapper.PaymentMapper;
import com.rbkmoney.magista.event.mapper.impl.AllocationCapturedMapper;
import com.rbkmoney.magista.service.AllocationService;
import com.rbkmoney.magista.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentBatchHandler implements BatchHandler<InvoiceChange, MachineEvent> {

    private final PaymentService paymentService;
    private final List<PaymentMapper> mappers;
    private final AllocationService allocationService;
    private final AllocationCapturedMapper allocationCapturedMapper;

    @Override
    @Transactional
    public Processor handle(List<Map.Entry<InvoiceChange, MachineEvent>> changes) {
        List<PaymentData> paymentEvents = changes.stream()
                .map(changeWithParent -> {
                    InvoiceChange change = changeWithParent.getKey();
                    for (PaymentMapper paymentMapper : mappers) {
                        if (paymentMapper.accept(change)) {
                            return paymentMapper.map(changeWithParent.getKey(), changeWithParent.getValue());
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<AllocationTransactionData> allocationTransactions = changes.stream()
                .map(invoiceChangeMachineEventEntry -> {
                    InvoiceChange invoiceChange = invoiceChangeMachineEventEntry.getKey();
                    if (allocationCapturedMapper.accept(invoiceChange)) {
                        return allocationCapturedMapper.map(
                                invoiceChangeMachineEventEntry.getKey(),
                                invoiceChangeMachineEventEntry.getValue()
                        );
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());


        return () -> {
            paymentService.savePayments(paymentEvents);
            if (!allocationTransactions.isEmpty()) {
                allocationService.saveAllocations(allocationTransactions);
            }
        };
    }

    @Override
    public List<? extends Mapper> getMappers() {
        List<Mapper> mappers = new ArrayList<>(this.mappers);
        mappers.add(this.allocationCapturedMapper);
        return mappers;
    }
}
