package com.rbkmoney.magista.model;

import com.rbkmoney.damsel.domain.InvoiceStatus;

import java.time.Instant;

/**
 * Created by tolkonepiu on 04.08.16.
 */
public class Invoice {

    private String id;

    private InvoiceStatus._Fields status;

    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InvoiceStatus._Fields getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus._Fields status) {
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
        return "Invoice{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
