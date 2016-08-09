package com.rbkmoney.magista.model;

import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;

import java.time.Instant;

/**
 * Created by tolkonepiu on 04.08.16.
 */
public class Payment {

    private String id;

    private long eventId;

    private String invoiceId;

    private String merchantId;

    private String shopId;

    private String customerId;

    private String maskedPan;

    private InvoicePaymentStatus._Fields status;

    private long amount;

    private String currencyCode;

    private BankCardPaymentSystem paymentSystem;

    private String cityName;

    private String ip;

    private Instant createdAt;

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

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }

    public InvoicePaymentStatus._Fields getStatus() {
        return status;
    }

    public void setStatus(InvoicePaymentStatus._Fields status) {
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

    public BankCardPaymentSystem getPaymentSystem() {
        return paymentSystem;
    }

    public void setPaymentSystem(BankCardPaymentSystem paymentSystem) {
        this.paymentSystem = paymentSystem;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
                ", eventId=" + eventId +
                ", invoiceId='" + invoiceId + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", shopId='" + shopId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", maskedPan='" + maskedPan + '\'' +
                ", status=" + status +
                ", amount=" + amount +
                ", currencyCode='" + currencyCode + '\'' +
                ", paymentSystem='" + paymentSystem + '\'' +
                ", cityName='" + cityName + '\'' +
                ", ip='" + ip + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
