package com.rbkmoney.magista.model;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;

import java.time.Instant;

/**
 * Created by tolkonepiu on 27/10/2016.
 */
public class PaymentStatusChange {

    long eventId;
    String invoiceId;
    String paymentId;
    Instant createdAt;
    InvoicePaymentStatus status;

    public PaymentStatusChange() {
    }

    public PaymentStatusChange(long eventId, String invoiceId, String paymentId, Instant createdAt, InvoicePaymentStatus status) {
        this.eventId = eventId;
        this.invoiceId = invoiceId;
        this.paymentId = paymentId;
        this.createdAt = createdAt;
        this.status = status;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public InvoicePaymentStatus getStatus() {
        return status;
    }

    public void setStatus(InvoicePaymentStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PaymentStatusChange{" +
                "eventId=" + eventId +
                ", invoiceId='" + invoiceId + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", createdAt=" + createdAt +
                ", status=" + status +
                '}';
    }
}
