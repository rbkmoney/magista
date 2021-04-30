package com.rbkmoney.magista;

import com.rbkmoney.damsel.base.Rational;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackChangePayload;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChargebackCreated;
import com.rbkmoney.geck.common.util.TypeUtil;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestData {

    private static final EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandom();

    public static InvoicePaymentChargebackChangePayload buildInvoiceChargebackChangePayload() {
        InvoicePaymentChargebackChangePayload invoicePaymentChargebackChangePayload =
                new InvoicePaymentChargebackChangePayload();
        InvoicePaymentChargebackCreated invoicePaymentChargebackCreated = new InvoicePaymentChargebackCreated();
        invoicePaymentChargebackCreated.setChargeback(TestData.buildChargebackCreate());
        invoicePaymentChargebackChangePayload.setInvoicePaymentChargebackCreated(invoicePaymentChargebackCreated);

        return invoicePaymentChargebackChangePayload;
    }

    public static InvoicePaymentChargeback buildChargebackCreate() {
        InvoicePaymentChargeback invoicePaymentChargeback = EnhancedRandom.random(
                InvoicePaymentChargeback.class, "status", "reason", "stage", "created_at", "context");
        invoicePaymentChargeback
                .setStatus(InvoicePaymentChargebackStatus.accepted(new InvoicePaymentChargebackAccepted()));
        invoicePaymentChargeback.setReason(
                new InvoicePaymentChargebackReason()
                        .setCode("653")
                        .setCategory(InvoicePaymentChargebackCategory.fraud(
                                new InvoicePaymentChargebackCategoryFraud()
                        ))
        );
        invoicePaymentChargeback.setStage(
                InvoicePaymentChargebackStage.pre_arbitration(new InvoicePaymentChargebackStagePreArbitration())
        );
        invoicePaymentChargeback.setCreatedAt(TypeUtil.temporalToString(Instant.now()));
        return invoicePaymentChargeback;
    }

    public static Allocation buildAllocation() {
        Allocation allocation = new Allocation();
        allocation.setTransactions(new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            AllocationTransaction allocationTransaction = new AllocationTransaction();
            allocationTransaction.setId(enhancedRandom.nextObject(String.class));

            AllocationTransactionTarget allocationTransactionTarget = new AllocationTransactionTarget();
            AllocationTransactionTargetShop allocationTransactionTargetShop =
                    enhancedRandom.nextObject(AllocationTransactionTargetShop.class);
            allocationTransactionTarget.setShop(allocationTransactionTargetShop);
            allocationTransaction.setTarget(allocationTransactionTarget);

            allocationTransaction.setAmount(new Cash(9000, new CurrencyRef("RUB")));

            AllocationTransactionBody allocationTransactionBody = new AllocationTransactionBody();
            AllocationTransactionTarget feeAllocationTransactionTarget = new AllocationTransactionTarget();
            AllocationTransactionTargetShop feeAllocationTransactionTargetShop =
                    enhancedRandom.nextObject(AllocationTransactionTargetShop.class);
            feeAllocationTransactionTarget.setShop(feeAllocationTransactionTargetShop);
            allocationTransactionBody.setFeeTarget(feeAllocationTransactionTarget);
            allocationTransactionBody.setFeeAmount(new Cash(1000, new CurrencyRef("RUB")));
            allocationTransactionBody.setTotal(new Cash(10000, new CurrencyRef("RUB")));
            allocationTransactionBody.setFee(new AllocationTransactionFee(new Rational(10, 1)));

            allocationTransaction.setBody(allocationTransactionBody);

            allocation.getTransactions().add(allocationTransaction);
        }

        return allocation;
    }

}
