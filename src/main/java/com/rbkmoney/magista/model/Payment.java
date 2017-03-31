package com.rbkmoney.magista.model;

import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.InvoicePayment;
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

    private int shopId;

    private String customerId;

    private String maskedPan;

    private InvoicePaymentStatus._Fields status;

    private long amount;

    private long fee;

    private String currencyCode;

    private BankCardPaymentSystem paymentSystem;

    private int cityId;

    private int  countryId;

    private String email;

    private String phoneNumber;

    private String ip;

    private Instant createdAt;

    private Instant changedAt;

    private InvoicePayment model;

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

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
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

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
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

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    public InvoicePayment getModel() {
        return model;
    }

    public void setModel(InvoicePayment model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", eventId=" + eventId +
                ", invoiceId='" + invoiceId + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", shopId=" + shopId +
                ", customerId='" + customerId + '\'' +
                ", maskedPan='" + maskedPan + '\'' +
                ", status=" + status +
                ", amount=" + amount +
                ", fee=" + fee +
                ", currencyCode='" + currencyCode + '\'' +
                ", paymentSystem=" + paymentSystem +
                ", cityId=" + cityId +
                ", countryId=" + countryId +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", ip='" + ip + '\'' +
                ", createdAt=" + createdAt +
                ", changedAt=" + changedAt +
                '}';
    }
}
