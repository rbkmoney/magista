package com.rbkmoney.magista.model;

import com.rbkmoney.damsel.domain.InvoiceStatus;

import java.time.Instant;
import java.util.Arrays;

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
        if (statusDetails != null ? !statusDetails.equals(invoice.statusDetails) : invoice.statusDetails != null)
            return false;
        if (product != null ? !product.equals(invoice.product) : invoice.product != null) return false;
        if (description != null ? !description.equals(invoice.description) : invoice.description != null) return false;
        if (currencyCode != null ? !currencyCode.equals(invoice.currencyCode) : invoice.currencyCode != null)
            return false;
        if (createdAt != null ? !createdAt.equals(invoice.createdAt) : invoice.createdAt != null) return false;
        if (due != null ? !due.equals(invoice.due) : invoice.due != null) return false;
        if (changedAt != null ? !changedAt.equals(invoice.changedAt) : invoice.changedAt != null) return false;
        return Arrays.equals(context, invoice.context);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (eventId ^ (eventId >>> 32));
        result = 31 * result + (merchantId != null ? merchantId.hashCode() : 0);
        result = 31 * result + shopId;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (statusDetails != null ? statusDetails.hashCode() : 0);
        result = 31 * result + (product != null ? product.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (amount ^ (amount >>> 32));
        result = 31 * result + (currencyCode != null ? currencyCode.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (due != null ? due.hashCode() : 0);
        result = 31 * result + (changedAt != null ? changedAt.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(context);
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
