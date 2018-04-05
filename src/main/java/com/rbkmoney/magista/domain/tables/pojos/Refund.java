/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.magista.domain.tables.pojos;


import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.RefundStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Refund implements Serializable {

    private static final long serialVersionUID = 213241229;

    private Long             id;
    private Long             eventId;
    private LocalDateTime    eventCreatedAt;
    private InvoiceEventType eventType;
    private String           invoiceId;
    private String           paymentId;
    private String           refundId;
    private String           partyId;
    private String           partyShopId;
    private String           partyContractId;
    private RefundStatus     refundStatus;
    private FailureClass     refundOperationFailureClass;
    private String           refundExternalFailure;
    private String           refundExternalFailureReason;
    private LocalDateTime    refundCreatedAt;
    private String           refundReason;
    private String           refundCurrencyCode;
    private Long             refundAmount;
    private Long             refundFee;
    private Long             refundProviderFee;
    private Long             refundExternalFee;

    public Refund() {}

    public Refund(Refund value) {
        this.id = value.id;
        this.eventId = value.eventId;
        this.eventCreatedAt = value.eventCreatedAt;
        this.eventType = value.eventType;
        this.invoiceId = value.invoiceId;
        this.paymentId = value.paymentId;
        this.refundId = value.refundId;
        this.partyId = value.partyId;
        this.partyShopId = value.partyShopId;
        this.partyContractId = value.partyContractId;
        this.refundStatus = value.refundStatus;
        this.refundOperationFailureClass = value.refundOperationFailureClass;
        this.refundExternalFailure = value.refundExternalFailure;
        this.refundExternalFailureReason = value.refundExternalFailureReason;
        this.refundCreatedAt = value.refundCreatedAt;
        this.refundReason = value.refundReason;
        this.refundCurrencyCode = value.refundCurrencyCode;
        this.refundAmount = value.refundAmount;
        this.refundFee = value.refundFee;
        this.refundProviderFee = value.refundProviderFee;
        this.refundExternalFee = value.refundExternalFee;
    }

    public Refund(
        Long             id,
        Long             eventId,
        LocalDateTime    eventCreatedAt,
        InvoiceEventType eventType,
        String           invoiceId,
        String           paymentId,
        String           refundId,
        String           partyId,
        String           partyShopId,
        String           partyContractId,
        RefundStatus     refundStatus,
        FailureClass     refundOperationFailureClass,
        String           refundExternalFailure,
        String           refundExternalFailureReason,
        LocalDateTime    refundCreatedAt,
        String           refundReason,
        String           refundCurrencyCode,
        Long             refundAmount,
        Long             refundFee,
        Long             refundProviderFee,
        Long             refundExternalFee
    ) {
        this.id = id;
        this.eventId = eventId;
        this.eventCreatedAt = eventCreatedAt;
        this.eventType = eventType;
        this.invoiceId = invoiceId;
        this.paymentId = paymentId;
        this.refundId = refundId;
        this.partyId = partyId;
        this.partyShopId = partyShopId;
        this.partyContractId = partyContractId;
        this.refundStatus = refundStatus;
        this.refundOperationFailureClass = refundOperationFailureClass;
        this.refundExternalFailure = refundExternalFailure;
        this.refundExternalFailureReason = refundExternalFailureReason;
        this.refundCreatedAt = refundCreatedAt;
        this.refundReason = refundReason;
        this.refundCurrencyCode = refundCurrencyCode;
        this.refundAmount = refundAmount;
        this.refundFee = refundFee;
        this.refundProviderFee = refundProviderFee;
        this.refundExternalFee = refundExternalFee;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return this.eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getEventCreatedAt() {
        return this.eventCreatedAt;
    }

    public void setEventCreatedAt(LocalDateTime eventCreatedAt) {
        this.eventCreatedAt = eventCreatedAt;
    }

    public InvoiceEventType getEventType() {
        return this.eventType;
    }

    public void setEventType(InvoiceEventType eventType) {
        this.eventType = eventType;
    }

    public String getInvoiceId() {
        return this.invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getPaymentId() {
        return this.paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getRefundId() {
        return this.refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getPartyId() {
        return this.partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getPartyShopId() {
        return this.partyShopId;
    }

    public void setPartyShopId(String partyShopId) {
        this.partyShopId = partyShopId;
    }

    public String getPartyContractId() {
        return this.partyContractId;
    }

    public void setPartyContractId(String partyContractId) {
        this.partyContractId = partyContractId;
    }

    public RefundStatus getRefundStatus() {
        return this.refundStatus;
    }

    public void setRefundStatus(RefundStatus refundStatus) {
        this.refundStatus = refundStatus;
    }

    public FailureClass getRefundOperationFailureClass() {
        return this.refundOperationFailureClass;
    }

    public void setRefundOperationFailureClass(FailureClass refundOperationFailureClass) {
        this.refundOperationFailureClass = refundOperationFailureClass;
    }

    public String getRefundExternalFailure() {
        return this.refundExternalFailure;
    }

    public void setRefundExternalFailure(String refundExternalFailure) {
        this.refundExternalFailure = refundExternalFailure;
    }

    public String getRefundExternalFailureReason() {
        return this.refundExternalFailureReason;
    }

    public void setRefundExternalFailureReason(String refundExternalFailureReason) {
        this.refundExternalFailureReason = refundExternalFailureReason;
    }

    public LocalDateTime getRefundCreatedAt() {
        return this.refundCreatedAt;
    }

    public void setRefundCreatedAt(LocalDateTime refundCreatedAt) {
        this.refundCreatedAt = refundCreatedAt;
    }

    public String getRefundReason() {
        return this.refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getRefundCurrencyCode() {
        return this.refundCurrencyCode;
    }

    public void setRefundCurrencyCode(String refundCurrencyCode) {
        this.refundCurrencyCode = refundCurrencyCode;
    }

    public Long getRefundAmount() {
        return this.refundAmount;
    }

    public void setRefundAmount(Long refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Long getRefundFee() {
        return this.refundFee;
    }

    public void setRefundFee(Long refundFee) {
        this.refundFee = refundFee;
    }

    public Long getRefundProviderFee() {
        return this.refundProviderFee;
    }

    public void setRefundProviderFee(Long refundProviderFee) {
        this.refundProviderFee = refundProviderFee;
    }

    public Long getRefundExternalFee() {
        return this.refundExternalFee;
    }

    public void setRefundExternalFee(Long refundExternalFee) {
        this.refundExternalFee = refundExternalFee;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Refund other = (Refund) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (eventId == null) {
            if (other.eventId != null)
                return false;
        }
        else if (!eventId.equals(other.eventId))
            return false;
        if (eventCreatedAt == null) {
            if (other.eventCreatedAt != null)
                return false;
        }
        else if (!eventCreatedAt.equals(other.eventCreatedAt))
            return false;
        if (eventType == null) {
            if (other.eventType != null)
                return false;
        }
        else if (!eventType.equals(other.eventType))
            return false;
        if (invoiceId == null) {
            if (other.invoiceId != null)
                return false;
        }
        else if (!invoiceId.equals(other.invoiceId))
            return false;
        if (paymentId == null) {
            if (other.paymentId != null)
                return false;
        }
        else if (!paymentId.equals(other.paymentId))
            return false;
        if (refundId == null) {
            if (other.refundId != null)
                return false;
        }
        else if (!refundId.equals(other.refundId))
            return false;
        if (partyId == null) {
            if (other.partyId != null)
                return false;
        }
        else if (!partyId.equals(other.partyId))
            return false;
        if (partyShopId == null) {
            if (other.partyShopId != null)
                return false;
        }
        else if (!partyShopId.equals(other.partyShopId))
            return false;
        if (partyContractId == null) {
            if (other.partyContractId != null)
                return false;
        }
        else if (!partyContractId.equals(other.partyContractId))
            return false;
        if (refundStatus == null) {
            if (other.refundStatus != null)
                return false;
        }
        else if (!refundStatus.equals(other.refundStatus))
            return false;
        if (refundOperationFailureClass == null) {
            if (other.refundOperationFailureClass != null)
                return false;
        }
        else if (!refundOperationFailureClass.equals(other.refundOperationFailureClass))
            return false;
        if (refundExternalFailure == null) {
            if (other.refundExternalFailure != null)
                return false;
        }
        else if (!refundExternalFailure.equals(other.refundExternalFailure))
            return false;
        if (refundExternalFailureReason == null) {
            if (other.refundExternalFailureReason != null)
                return false;
        }
        else if (!refundExternalFailureReason.equals(other.refundExternalFailureReason))
            return false;
        if (refundCreatedAt == null) {
            if (other.refundCreatedAt != null)
                return false;
        }
        else if (!refundCreatedAt.equals(other.refundCreatedAt))
            return false;
        if (refundReason == null) {
            if (other.refundReason != null)
                return false;
        }
        else if (!refundReason.equals(other.refundReason))
            return false;
        if (refundCurrencyCode == null) {
            if (other.refundCurrencyCode != null)
                return false;
        }
        else if (!refundCurrencyCode.equals(other.refundCurrencyCode))
            return false;
        if (refundAmount == null) {
            if (other.refundAmount != null)
                return false;
        }
        else if (!refundAmount.equals(other.refundAmount))
            return false;
        if (refundFee == null) {
            if (other.refundFee != null)
                return false;
        }
        else if (!refundFee.equals(other.refundFee))
            return false;
        if (refundProviderFee == null) {
            if (other.refundProviderFee != null)
                return false;
        }
        else if (!refundProviderFee.equals(other.refundProviderFee))
            return false;
        if (refundExternalFee == null) {
            if (other.refundExternalFee != null)
                return false;
        }
        else if (!refundExternalFee.equals(other.refundExternalFee))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.eventId == null) ? 0 : this.eventId.hashCode());
        result = prime * result + ((this.eventCreatedAt == null) ? 0 : this.eventCreatedAt.hashCode());
        result = prime * result + ((this.eventType == null) ? 0 : this.eventType.hashCode());
        result = prime * result + ((this.invoiceId == null) ? 0 : this.invoiceId.hashCode());
        result = prime * result + ((this.paymentId == null) ? 0 : this.paymentId.hashCode());
        result = prime * result + ((this.refundId == null) ? 0 : this.refundId.hashCode());
        result = prime * result + ((this.partyId == null) ? 0 : this.partyId.hashCode());
        result = prime * result + ((this.partyShopId == null) ? 0 : this.partyShopId.hashCode());
        result = prime * result + ((this.partyContractId == null) ? 0 : this.partyContractId.hashCode());
        result = prime * result + ((this.refundStatus == null) ? 0 : this.refundStatus.hashCode());
        result = prime * result + ((this.refundOperationFailureClass == null) ? 0 : this.refundOperationFailureClass.hashCode());
        result = prime * result + ((this.refundExternalFailure == null) ? 0 : this.refundExternalFailure.hashCode());
        result = prime * result + ((this.refundExternalFailureReason == null) ? 0 : this.refundExternalFailureReason.hashCode());
        result = prime * result + ((this.refundCreatedAt == null) ? 0 : this.refundCreatedAt.hashCode());
        result = prime * result + ((this.refundReason == null) ? 0 : this.refundReason.hashCode());
        result = prime * result + ((this.refundCurrencyCode == null) ? 0 : this.refundCurrencyCode.hashCode());
        result = prime * result + ((this.refundAmount == null) ? 0 : this.refundAmount.hashCode());
        result = prime * result + ((this.refundFee == null) ? 0 : this.refundFee.hashCode());
        result = prime * result + ((this.refundProviderFee == null) ? 0 : this.refundProviderFee.hashCode());
        result = prime * result + ((this.refundExternalFee == null) ? 0 : this.refundExternalFee.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Refund (");

        sb.append(id);
        sb.append(", ").append(eventId);
        sb.append(", ").append(eventCreatedAt);
        sb.append(", ").append(eventType);
        sb.append(", ").append(invoiceId);
        sb.append(", ").append(paymentId);
        sb.append(", ").append(refundId);
        sb.append(", ").append(partyId);
        sb.append(", ").append(partyShopId);
        sb.append(", ").append(partyContractId);
        sb.append(", ").append(refundStatus);
        sb.append(", ").append(refundOperationFailureClass);
        sb.append(", ").append(refundExternalFailure);
        sb.append(", ").append(refundExternalFailureReason);
        sb.append(", ").append(refundCreatedAt);
        sb.append(", ").append(refundReason);
        sb.append(", ").append(refundCurrencyCode);
        sb.append(", ").append(refundAmount);
        sb.append(", ").append(refundFee);
        sb.append(", ").append(refundProviderFee);
        sb.append(", ").append(refundExternalFee);

        sb.append(")");
        return sb.toString();
    }
}
