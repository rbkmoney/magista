package com.rbkmoney.magista.event.handler.impl;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.tables.pojos.ChargebackData;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.handler.BatchHandler;
import com.rbkmoney.magista.event.mapper.ChargebackMapper;
import com.rbkmoney.magista.service.PaymentChargebackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChargebackBatchHandler implements BatchHandler<InvoiceChange, MachineEvent> {

    private final PaymentChargebackService paymentChargebackService;

    private final List<ChargebackMapper> mappers;

    @Override
    public Processor handle(List<Map.Entry<InvoiceChange, MachineEvent>> changes) {
        List<ChargebackData> chargebackEvents = changes.stream()
                .map(changeWithParent -> {
                    InvoiceChange change = changeWithParent.getKey();
                    for (ChargebackMapper chargebackMapper : getMappers()) {
                        if (chargebackMapper.accept(change)) {
                            return chargebackMapper.map(changeWithParent.getKey(), changeWithParent.getValue());
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return () -> paymentChargebackService.saveChargeback(chargebackEvents);
    }

    @Override
    public List<ChargebackMapper> getMappers() {
        return mappers;
    }
}
