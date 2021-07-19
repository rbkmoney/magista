package com.rbkmoney.magista.event.handler.impl;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoiceTemplateChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceData;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceTemplate;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.handler.BatchHandler;
import com.rbkmoney.magista.event.mapper.InvoiceMapper;
import com.rbkmoney.magista.event.mapper.InvoiceTemplateMapper;
import com.rbkmoney.magista.service.InvoiceTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InvoiceTemplateBatchHandler implements BatchHandler<InvoiceTemplateChange, MachineEvent> {

    private final InvoiceTemplateService invoiceTemplateService;
    private final List<InvoiceTemplateMapper> mappers;

    @Override
    public Processor handle(List<Map.Entry<InvoiceTemplateChange, MachineEvent>> changes) {
        List<InvoiceTemplate> invoiceTemplates = changes.stream()
                .map(changeWithParent -> {
                    InvoiceTemplateChange change = changeWithParent.getKey();
                    for (InvoiceTemplateMapper invoiceTemplateMapper : getMappers()) {
                        if (invoiceTemplateMapper.accept(change)) {
                            return invoiceTemplateMapper.map(changeWithParent.getKey(), changeWithParent.getValue());
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return () -> invoiceTemplateService.save(invoiceTemplates);
    }

    @Override
    public List<InvoiceTemplateMapper> getMappers() {
        return mappers;
    }
}
