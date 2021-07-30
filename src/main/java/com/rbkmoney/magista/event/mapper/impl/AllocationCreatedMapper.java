package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.domain.Allocation;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.AllocationTransactionData;
import com.rbkmoney.magista.event.ChangeType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AllocationCreatedMapper extends BaseAllocationMapper {

    @Override
    public List<AllocationTransactionData> map(InvoiceChange change, MachineEvent machineEvent) {
        Allocation allocation = change.getInvoiceCreated().getInvoice().getAllocation();

        return allocation.getTransactions().stream()
                .map(allocationTransaction ->
                        mapAllocationTransaction(
                                machineEvent,
                                InvoiceEventType.INVOICE_CREATED,
                                allocationTransaction,
                                null
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_CREATED_ALLOCATION;
    }

}
