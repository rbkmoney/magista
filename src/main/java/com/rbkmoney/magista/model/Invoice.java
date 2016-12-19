package com.rbkmoney.magista.model;

import com.rbkmoney.damsel.domain.InvoiceStatus;

import java.time.Instant;

/**
 * Created by tolkonepiu on 04.08.16.
 */
public class Invoice {

    private String id;

    private long eventId;

    private String merchantId;

    private int shopId;

    private InvoiceStatus._Fields status;

    private long amount;

    private String currencyCode;

    private Instant createdAt;

    private Instant changedAt;

    private com.rbkmoney.damsel.domain.Invoice model;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public InvoiceStatus._Fields getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus._Fields status) {
        this.status = status;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    public com.rbkmoney.damsel.domain.Invoice getModel() {
        return model;
    }

    public void setModel(com.rbkmoney.damsel.domain.Invoice model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id='" + id + '\'' +
                ", eventId=" + eventId +
                ", merchantId='" + merchantId + '\'' +
                ", shopId=" + shopId +
                ", status=" + status +
                ", amount=" + amount +
                ", currencyCode='" + currencyCode + '\'' +
                ", createdAt=" + createdAt +
                ", changedAt=" + changedAt +
                '}';
    }
}
