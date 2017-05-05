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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Invoice invoice = (Invoice) o;

        if (eventId != invoice.eventId) return false;
        if (shopId != invoice.shopId) return false;
        if (amount != invoice.amount) return false;
        if (id != null ? !id.equals(invoice.id) : invoice.id != null) return false;
        if (merchantId != null ? !merchantId.equals(invoice.merchantId) : invoice.merchantId != null) return false;
        if (status != invoice.status) return false;
        if (currencyCode != null ? !currencyCode.equals(invoice.currencyCode) : invoice.currencyCode != null)
            return false;
        if (createdAt != null ? !createdAt.equals(invoice.createdAt) : invoice.createdAt != null) return false;
        return model != null ? model.equals(invoice.model) : invoice.model == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (eventId ^ (eventId >>> 32));
        result = 31 * result + (merchantId != null ? merchantId.hashCode() : 0);
        result = 31 * result + shopId;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (int) (amount ^ (amount >>> 32));
        result = 31 * result + (currencyCode != null ? currencyCode.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (model != null ? model.hashCode() : 0);
        return result;
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
                ", model=" + model +
                '}';
    }
}
