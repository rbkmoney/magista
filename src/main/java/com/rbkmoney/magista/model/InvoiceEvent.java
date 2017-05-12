package com.rbkmoney.magista.model;

import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.magista.event.EventType;

import java.time.Instant;

/**
 * Created by tolkonepiu on 10/05/2017.
 */
public class InvoiceEvent {

    private long eventId;

    private String merchantId;

    private int shopId;

    private String invoiceId;

    private EventType eventType;

    private Instant eventCreatedAt;

    private InvoiceStatus._Fields invoiceStatus;

    private long invoiceAmount;

    private String invoiceCurrencyCode;

    private Instant invoiceCreatedAt;

    private String paymentId;

    private InvoicePaymentStatus._Fields paymentStatus;

    private long paymentAmount;

    private long paymentFee;

    private BankCardPaymentSystem paymentSystem;

    private int paymentCountryId;

    private int paymentCityId;

    private String paymentIp;

    private String paymentPhoneNumber;

    private String paymentEmail;

    private String paymentFingerprint;

    private Instant paymentCreatedAt;

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

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Instant getEventCreatedAt() {
        return eventCreatedAt;
    }

    public void setEventCreatedAt(Instant eventCreatedAt) {
        this.eventCreatedAt = eventCreatedAt;
    }

    public InvoiceStatus._Fields getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(InvoiceStatus._Fields invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public long getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(long invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceCurrencyCode() {
        return invoiceCurrencyCode;
    }

    public void setInvoiceCurrencyCode(String invoiceCurrencyCode) {
        this.invoiceCurrencyCode = invoiceCurrencyCode;
    }

    public Instant getInvoiceCreatedAt() {
        return invoiceCreatedAt;
    }

    public void setInvoiceCreatedAt(Instant invoiceCreatedAt) {
        this.invoiceCreatedAt = invoiceCreatedAt;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public InvoicePaymentStatus._Fields getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(InvoicePaymentStatus._Fields paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public long getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(long paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public long getPaymentFee() {
        return paymentFee;
    }

    public void setPaymentFee(long paymentFee) {
        this.paymentFee = paymentFee;
    }

    public BankCardPaymentSystem getPaymentSystem() {
        return paymentSystem;
    }

    public void setPaymentSystem(BankCardPaymentSystem paymentSystem) {
        this.paymentSystem = paymentSystem;
    }

    public int getPaymentCountryId() {
        return paymentCountryId;
    }

    public void setPaymentCountryId(int paymentCountryId) {
        this.paymentCountryId = paymentCountryId;
    }

    public int getPaymentCityId() {
        return paymentCityId;
    }

    public void setPaymentCityId(int paymentCityId) {
        this.paymentCityId = paymentCityId;
    }

    public String getPaymentIp() {
        return paymentIp;
    }

    public void setPaymentIp(String paymentIp) {
        this.paymentIp = paymentIp;
    }

    public String getPaymentPhoneNumber() {
        return paymentPhoneNumber;
    }

    public void setPaymentPhoneNumber(String paymentPhoneNumber) {
        this.paymentPhoneNumber = paymentPhoneNumber;
    }

    public String getPaymentEmail() {
        return paymentEmail;
    }

    public void setPaymentEmail(String paymentEmail) {
        this.paymentEmail = paymentEmail;
    }

    public String getPaymentFingerprint() {
        return paymentFingerprint;
    }

    public void setPaymentFingerprint(String paymentFingerprint) {
        this.paymentFingerprint = paymentFingerprint;
    }

    public Instant getPaymentCreatedAt() {
        return paymentCreatedAt;
    }

    public void setPaymentCreatedAt(Instant paymentCreatedAt) {
        this.paymentCreatedAt = paymentCreatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvoiceEvent that = (InvoiceEvent) o;

        if (eventId != that.eventId) return false;
        if (shopId != that.shopId) return false;
        if (invoiceAmount != that.invoiceAmount) return false;
        if (paymentAmount != that.paymentAmount) return false;
        if (paymentFee != that.paymentFee) return false;
        if (paymentCountryId != that.paymentCountryId) return false;
        if (paymentCityId != that.paymentCityId) return false;
        if (merchantId != null ? !merchantId.equals(that.merchantId) : that.merchantId != null) return false;
        if (invoiceId != null ? !invoiceId.equals(that.invoiceId) : that.invoiceId != null) return false;
        if (eventType != that.eventType) return false;
        if (eventCreatedAt != null ? !eventCreatedAt.equals(that.eventCreatedAt) : that.eventCreatedAt != null)
            return false;
        if (invoiceStatus != that.invoiceStatus) return false;
        if (invoiceCurrencyCode != null ? !invoiceCurrencyCode.equals(that.invoiceCurrencyCode) : that.invoiceCurrencyCode != null)
            return false;
        if (invoiceCreatedAt != null ? !invoiceCreatedAt.equals(that.invoiceCreatedAt) : that.invoiceCreatedAt != null)
            return false;
        if (paymentId != null ? !paymentId.equals(that.paymentId) : that.paymentId != null) return false;
        if (paymentStatus != that.paymentStatus) return false;
        if (paymentSystem != that.paymentSystem) return false;
        if (paymentIp != null ? !paymentIp.equals(that.paymentIp) : that.paymentIp != null) return false;
        if (paymentPhoneNumber != null ? !paymentPhoneNumber.equals(that.paymentPhoneNumber) : that.paymentPhoneNumber != null)
            return false;
        if (paymentEmail != null ? !paymentEmail.equals(that.paymentEmail) : that.paymentEmail != null) return false;
        if (paymentFingerprint != null ? !paymentFingerprint.equals(that.paymentFingerprint) : that.paymentFingerprint != null)
            return false;
        return paymentCreatedAt != null ? paymentCreatedAt.equals(that.paymentCreatedAt) : that.paymentCreatedAt == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (eventId ^ (eventId >>> 32));
        result = 31 * result + (merchantId != null ? merchantId.hashCode() : 0);
        result = 31 * result + shopId;
        result = 31 * result + (invoiceId != null ? invoiceId.hashCode() : 0);
        result = 31 * result + (eventType != null ? eventType.hashCode() : 0);
        result = 31 * result + (eventCreatedAt != null ? eventCreatedAt.hashCode() : 0);
        result = 31 * result + (invoiceStatus != null ? invoiceStatus.hashCode() : 0);
        result = 31 * result + (int) (invoiceAmount ^ (invoiceAmount >>> 32));
        result = 31 * result + (invoiceCurrencyCode != null ? invoiceCurrencyCode.hashCode() : 0);
        result = 31 * result + (invoiceCreatedAt != null ? invoiceCreatedAt.hashCode() : 0);
        result = 31 * result + (paymentId != null ? paymentId.hashCode() : 0);
        result = 31 * result + (paymentStatus != null ? paymentStatus.hashCode() : 0);
        result = 31 * result + (int) (paymentAmount ^ (paymentAmount >>> 32));
        result = 31 * result + (int) (paymentFee ^ (paymentFee >>> 32));
        result = 31 * result + (paymentSystem != null ? paymentSystem.hashCode() : 0);
        result = 31 * result + paymentCountryId;
        result = 31 * result + paymentCityId;
        result = 31 * result + (paymentIp != null ? paymentIp.hashCode() : 0);
        result = 31 * result + (paymentPhoneNumber != null ? paymentPhoneNumber.hashCode() : 0);
        result = 31 * result + (paymentEmail != null ? paymentEmail.hashCode() : 0);
        result = 31 * result + (paymentFingerprint != null ? paymentFingerprint.hashCode() : 0);
        result = 31 * result + (paymentCreatedAt != null ? paymentCreatedAt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InvoiceEvent{" +
                "eventId=" + eventId +
                ", merchantId='" + merchantId + '\'' +
                ", shopId=" + shopId +
                ", invoiceId='" + invoiceId + '\'' +
                ", eventType=" + eventType +
                ", eventCreatedAt=" + eventCreatedAt +
                ", invoiceStatus=" + invoiceStatus +
                ", invoiceAmount=" + invoiceAmount +
                ", invoiceCurrencyCode='" + invoiceCurrencyCode + '\'' +
                ", invoiceCreatedAt=" + invoiceCreatedAt +
                ", paymentId='" + paymentId + '\'' +
                ", paymentStatus=" + paymentStatus +
                ", paymentAmount=" + paymentAmount +
                ", paymentFee=" + paymentFee +
                ", paymentSystem=" + paymentSystem +
                ", paymentCountryId=" + paymentCountryId +
                ", paymentCityId=" + paymentCityId +
                ", paymentIp='" + paymentIp + '\'' +
                ", paymentPhoneNumber='" + paymentPhoneNumber + '\'' +
                ", paymentEmail='" + paymentEmail + '\'' +
                ", paymentFingerprint='" + paymentFingerprint + '\'' +
                ", paymentCreatedAt=" + paymentCreatedAt +
                '}';
    }
}
