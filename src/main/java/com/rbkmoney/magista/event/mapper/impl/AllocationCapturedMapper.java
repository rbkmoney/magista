package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.domain.Allocation;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentStatusChanged;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.AllocationTransactionData;
import com.rbkmoney.magista.event.ChangeType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AllocationCapturedMapper extends BaseAllocationMapper {

    @Override
    public List<AllocationTransactionData> map(InvoiceChange change, MachineEvent machineEvent) {
        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();
        InvoicePaymentStatusChanged invoicePaymentStatusChanged =
                invoicePaymentChange.getPayload().getInvoicePaymentStatusChanged();
        Allocation allocation = invoicePaymentStatusChanged.getStatus().getCaptured().getAllocation();
        String paymentId = invoicePaymentChange.getId();

        return allocation.getTransactions().stream()
                .map(allocationTransaction ->
                        mapAllocationTransaction(
                                machineEvent,
                                InvoiceEventType.INVOICE_PAYMENT_STATUS_CHANGED,
                                allocationTransaction,
                                paymentId))
                .collect(Collectors.toList());
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_ALLOCATION_PAYMENT_STATUS_CHANGED_CAPTURED;
    }
}
