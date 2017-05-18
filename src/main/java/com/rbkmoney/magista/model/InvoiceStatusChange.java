package com.rbkmoney.magista.model;

import com.rbkmoney.damsel.domain.InvoiceStatus;

import java.time.Instant;

/**
 * Created by tolkonepiu on 27/10/2016.
 */
public class InvoiceStatusChange {

    long eventId;
    String invoiceId;
    Instant createdAt;
    InvoiceStatus status;

    public InvoiceStatusChange() {

    }

    public InvoiceStatusChange(long eventId, String invoiceId, Instant createdAt, InvoiceStatus status) {
        this.eventId = eventId;
        this.invoiceId = invoiceId;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "InvoiceStatusChange{" +
                "eventId=" + eventId +
                ", invoiceId='" + invoiceId + '\'' +
                ", createdAt=" + createdAt +
                ", status=" + status +
                '}';
    }
}
