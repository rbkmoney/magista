package com.rbkmoney.magista.model;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;

import java.time.Instant;

/**
 * Created by tolkonepiu on 04.08.16.
 */
public class Payment {

    private String id;

    private String invoiceId;

    private InvoicePaymentStatus._Fields status;

    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public InvoicePaymentStatus._Fields getStatus() {
        return status;
    }

    public void setStatus(InvoicePaymentStatus._Fields status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", invoiceId='" + invoiceId + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
