package com.rbkmoney.magista.model;

import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.domain.PaymentTool;

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

    private String failureCode;

    private String failureDescription;

    private long amount;

    private long fee;

    private String currencyCode;

    private String token;

    private String sessionId;

    private String bin;

    private PaymentTool._Fields paymentTool;

    private BankCardPaymentSystem paymentSystem;

    private int cityId;

    private int countryId;

    private String email;

    private String phoneNumber;

    private String ip;

    private Instant createdAt;

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

    public String getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(String failureCode) {
        this.failureCode = failureCode;
    }

    public String getFailureDescription() {
        return failureDescription;
    }

    public void setFailureDescription(String failureDescription) {
        this.failureDescription = failureDescription;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public PaymentTool._Fields getPaymentTool() {
        return paymentTool;
    }

    public void setPaymentTool(PaymentTool._Fields paymentTool) {
        this.paymentTool = paymentTool;
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

    public byte[] getContext() {
        return context;
    }

    public void setContext(byte[] context) {
        this.context = context;
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
                ", token='" + token + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", bin='" + bin + '\'' +
                ", paymentTool=" + paymentTool +
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
