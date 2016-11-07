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
    Instant changedAt;
    InvoicePaymentStatus status;

    public PaymentStatusChange() {
    }

    public PaymentStatusChange(long eventId, String invoiceId, String paymentId, Instant changedAt, InvoicePaymentStatus status) {
        this.eventId = eventId;
        this.invoiceId = invoiceId;
        this.paymentId = paymentId;
        this.changedAt = changedAt;
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

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
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
                ", changedAt=" + changedAt +
                ", status=" + status +
                '}';
    }
}
