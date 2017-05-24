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

    private String statusDetails;

    private String product;

    private String description;

    private long amount;

    private String currencyCode;

    private Instant createdAt;

    private Instant due;

    private Instant changedAt;

    private byte[] context;

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

    public String getStatusDetails() {
        return statusDetails;
    }

    public void setStatusDetails(String statusDetails) {
        this.statusDetails = statusDetails;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Instant getDue() {
        return due;
    }

    public void setDue(Instant due) {
        this.due = due;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    public byte[] getContext() {
        return context;
    }

    public void setContext(byte[] context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id='" + id + '\'' +
                ", eventId=" + eventId +
                ", merchantId='" + merchantId + '\'' +
                ", shopId=" + shopId +
                ", status=" + status +
                ", statusDetails='" + statusDetails + '\'' +
                ", product='" + product + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", currencyCode='" + currencyCode + '\'' +
                ", createdAt=" + createdAt +
                ", due=" + due +
                ", changedAt=" + changedAt +
                '}';
    }
}
