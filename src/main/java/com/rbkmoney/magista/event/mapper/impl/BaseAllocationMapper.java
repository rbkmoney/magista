package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.domain.AllocationTransaction;
import com.rbkmoney.damsel.domain.AllocationTransactionBodyTotal;
import com.rbkmoney.damsel.domain.AllocationTransactionDetails;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.AllocationTransactionData;
import com.rbkmoney.magista.event.mapper.AllocationMapper;
import com.rbkmoney.magista.util.DamselUtil;

public abstract class BaseAllocationMapper implements AllocationMapper {

    protected AllocationTransactionData mapAllocationTransaction(
            MachineEvent machineEvent,
            InvoiceEventType eventType,
            AllocationTransaction allocationTransaction,
            String externalId
    ) {
        AllocationTransactionData allocationData = new AllocationTransactionData();
        allocationData.setEventId(machineEvent.getEventId());
        allocationData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        allocationData.setEventType(eventType);
        allocationData.setInvoiceId(machineEvent.getSourceId());
        switch (eventType) {
            case INVOICE_PAYMENT_STATUS_CHANGED:
                allocationData.setPaymentId(externalId);
                break;
            case INVOICE_PAYMENT_REFUND_CREATED:
                allocationData.setRefundId(externalId);
                break;
            default:
                //no need to set any field
                break;
        }
        allocationData.setAllocationId(allocationTransaction.getId());
        if (allocationTransaction.getTarget().isSetShop()) {
            allocationData.setTargetOwnerId(allocationTransaction.getTarget().getShop().getOwnerId());
            allocationData.setTargetShopId(allocationTransaction.getTarget().getShop().getShopId());
        }
        allocationData.setAmount(allocationTransaction.getAmount().getAmount());
        allocationData.setCurrency(allocationTransaction.getAmount().getCurrency().getSymbolicCode());
        if (allocationTransaction.isSetBody()) {
            AllocationTransactionBodyTotal allocationTransactionBody = allocationTransaction.getBody();
            if (allocationTransactionBody.getFeeTarget().isSetShop()) {
                allocationData.setFeeTargetOwnerId(
                        allocationTransactionBody.getFeeTarget().getShop().getOwnerId()
                );
                allocationData.setFeeTargetShopId(allocationTransactionBody.getFeeTarget().getShop().getShopId());
            }
            allocationData.setFeeAmount(allocationTransactionBody.getFeeAmount().getAmount());
            allocationData.setFeeCurrency(allocationTransactionBody.getFeeAmount().getCurrency().getSymbolicCode());
            allocationData.setTotalAmount(allocationTransactionBody.getTotal().getAmount());
            allocationData.setTotalCurrency(allocationTransactionBody.getTotal().getCurrency().getSymbolicCode());
        }
        if (allocationTransaction.isSetDetails()) {
            AllocationTransactionDetails details = allocationTransaction.getDetails();
            allocationData.setInvoiceCartJson(DamselUtil.toJsonString(details.getCart()));
        }

        return allocationData;
    }

}
