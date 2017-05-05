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

    private String fingerprint;

    private String maskedPan;

    private InvoicePaymentStatus._Fields status;

    private long amount;

    private long fee;

    private String currencyCode;

    private BankCardPaymentSystem paymentSystem;

    private int cityId;

    private int countryId;

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

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Payment payment = (Payment) o;

        if (eventId != payment.eventId) return false;
        if (shopId != payment.shopId) return false;
        if (amount != payment.amount) return false;
        if (fee != payment.fee) return false;
        if (cityId != payment.cityId) return false;
        if (countryId != payment.countryId) return false;
        if (id != null ? !id.equals(payment.id) : payment.id != null) return false;
        if (invoiceId != null ? !invoiceId.equals(payment.invoiceId) : payment.invoiceId != null) return false;
        if (merchantId != null ? !merchantId.equals(payment.merchantId) : payment.merchantId != null) return false;
        if (fingerprint != null ? !fingerprint.equals(payment.fingerprint) : payment.fingerprint != null) return false;
        if (maskedPan != null ? !maskedPan.equals(payment.maskedPan) : payment.maskedPan != null) return false;
        if (status != payment.status) return false;
        if (currencyCode != null ? !currencyCode.equals(payment.currencyCode) : payment.currencyCode != null)
            return false;
        if (paymentSystem != payment.paymentSystem) return false;
        if (email != null ? !email.equals(payment.email) : payment.email != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(payment.phoneNumber) : payment.phoneNumber != null) return false;
        if (ip != null ? !ip.equals(payment.ip) : payment.ip != null) return false;
        if (createdAt != null ? !createdAt.equals(payment.createdAt) : payment.createdAt != null) return false;
        if (changedAt != null ? !changedAt.equals(payment.changedAt) : payment.changedAt != null) return false;
        return model != null ? model.equals(payment.model) : payment.model == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (eventId ^ (eventId >>> 32));
        result = 31 * result + (invoiceId != null ? invoiceId.hashCode() : 0);
        result = 31 * result + (merchantId != null ? merchantId.hashCode() : 0);
        result = 31 * result + shopId;
        result = 31 * result + (fingerprint != null ? fingerprint.hashCode() : 0);
        result = 31 * result + (maskedPan != null ? maskedPan.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (int) (amount ^ (amount >>> 32));
        result = 31 * result + (int) (fee ^ (fee >>> 32));
        result = 31 * result + (currencyCode != null ? currencyCode.hashCode() : 0);
        result = 31 * result + (paymentSystem != null ? paymentSystem.hashCode() : 0);
        result = 31 * result + cityId;
        result = 31 * result + countryId;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (changedAt != null ? changedAt.hashCode() : 0);
        result = 31 * result + (model != null ? model.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", eventId=" + eventId +
                ", invoiceId='" + invoiceId + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", shopId=" + shopId +
                ", fingerprint='" + fingerprint + '\'' +
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
                ", model=" + model +
                '}';
    }
}
