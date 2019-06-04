package com.rbkmoney.magista.event.handler.impl;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.handler.BatchHandler;
import com.rbkmoney.magista.event.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class UnsupportedBatchHandler implements BatchHandler<InvoiceChange, MachineEvent> {

    @Override
    public Processor handle(List<Map.Entry<InvoiceChange, MachineEvent>> changes) {
        return () -> {
            if (log.isDebugEnabled()) {
                log.debug("Unsupported changes, events='{}'", changes.stream().map(Map.Entry::getKey).collect(Collectors.toList()));
            }
        };
    }

    @Override
    public List<? extends Mapper> getMappers() {
        return Collections.EMPTY_LIST;
    }

}
