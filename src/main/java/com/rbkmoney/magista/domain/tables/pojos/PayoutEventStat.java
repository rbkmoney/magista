/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.magista.domain.tables.pojos;


import com.rbkmoney.magista.domain.enums.PayoutEventCategory;
import com.rbkmoney.magista.domain.enums.PayoutEventType;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.PayoutType;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PayoutEventStat implements Serializable {

    private static final long serialVersionUID = 1241801447;

    private Long                id;
    private Long                eventId;
    private PayoutEventCategory eventCategory;
    private PayoutEventType     eventType;
    private LocalDateTime       eventCreatedAt;
    private String              partyId;
    private String              partyShopId;
    private String              payoutId;
    private LocalDateTime       payoutCreatedAt;
    private PayoutStatus        payoutStatus;
    private Long                payoutAmount;
    private Long                payoutFee;
    private String              payoutCurrencyCode;
    private PayoutType          payoutType;
    private String              payoutCardToken;
    private String              payoutCardMaskedPan;
    private String              payoutCardBin;
    private String              payoutCardPaymentSystem;
    private String              payoutAccountBankId;
    private String              payoutAccountBankCorrId;
    private String              payoutAccountBankBik;
    private String              payoutAccountBankName;
    private String              payoutAccountInn;
    private String              payoutAccountLegalAgreementId;
    private LocalDateTime       payoutAccountLegalAgreementSignedAt;
    private String              payoutAccountPurpose;
    private String              payoutCancelDetails;

    public PayoutEventStat() {}

    public PayoutEventStat(PayoutEventStat value) {
        this.id = value.id;
        this.eventId = value.eventId;
        this.eventCategory = value.eventCategory;
        this.eventType = value.eventType;
        this.eventCreatedAt = value.eventCreatedAt;
        this.partyId = value.partyId;
        this.partyShopId = value.partyShopId;
        this.payoutId = value.payoutId;
        this.payoutCreatedAt = value.payoutCreatedAt;
        this.payoutStatus = value.payoutStatus;
        this.payoutAmount = value.payoutAmount;
        this.payoutFee = value.payoutFee;
        this.payoutCurrencyCode = value.payoutCurrencyCode;
        this.payoutType = value.payoutType;
        this.payoutCardToken = value.payoutCardToken;
        this.payoutCardMaskedPan = value.payoutCardMaskedPan;
        this.payoutCardBin = value.payoutCardBin;
        this.payoutCardPaymentSystem = value.payoutCardPaymentSystem;
        this.payoutAccountBankId = value.payoutAccountBankId;
        this.payoutAccountBankCorrId = value.payoutAccountBankCorrId;
        this.payoutAccountBankBik = value.payoutAccountBankBik;
        this.payoutAccountBankName = value.payoutAccountBankName;
        this.payoutAccountInn = value.payoutAccountInn;
        this.payoutAccountLegalAgreementId = value.payoutAccountLegalAgreementId;
        this.payoutAccountLegalAgreementSignedAt = value.payoutAccountLegalAgreementSignedAt;
        this.payoutAccountPurpose = value.payoutAccountPurpose;
        this.payoutCancelDetails = value.payoutCancelDetails;
    }

    public PayoutEventStat(
        Long                id,
        Long                eventId,
        PayoutEventCategory eventCategory,
        PayoutEventType     eventType,
        LocalDateTime       eventCreatedAt,
        String              partyId,
        String              partyShopId,
        String              payoutId,
        LocalDateTime       payoutCreatedAt,
        PayoutStatus        payoutStatus,
        Long                payoutAmount,
        Long                payoutFee,
        String              payoutCurrencyCode,
        PayoutType          payoutType,
        String              payoutCardToken,
        String              payoutCardMaskedPan,
        String              payoutCardBin,
        String              payoutCardPaymentSystem,
        String              payoutAccountBankId,
        String              payoutAccountBankCorrId,
        String              payoutAccountBankBik,
        String              payoutAccountBankName,
        String              payoutAccountInn,
        String              payoutAccountLegalAgreementId,
        LocalDateTime       payoutAccountLegalAgreementSignedAt,
        String              payoutAccountPurpose,
        String              payoutCancelDetails
    ) {
        this.id = id;
        this.eventId = eventId;
        this.eventCategory = eventCategory;
        this.eventType = eventType;
        this.eventCreatedAt = eventCreatedAt;
        this.partyId = partyId;
        this.partyShopId = partyShopId;
        this.payoutId = payoutId;
        this.payoutCreatedAt = payoutCreatedAt;
        this.payoutStatus = payoutStatus;
        this.payoutAmount = payoutAmount;
        this.payoutFee = payoutFee;
        this.payoutCurrencyCode = payoutCurrencyCode;
        this.payoutType = payoutType;
        this.payoutCardToken = payoutCardToken;
        this.payoutCardMaskedPan = payoutCardMaskedPan;
        this.payoutCardBin = payoutCardBin;
        this.payoutCardPaymentSystem = payoutCardPaymentSystem;
        this.payoutAccountBankId = payoutAccountBankId;
        this.payoutAccountBankCorrId = payoutAccountBankCorrId;
        this.payoutAccountBankBik = payoutAccountBankBik;
        this.payoutAccountBankName = payoutAccountBankName;
        this.payoutAccountInn = payoutAccountInn;
        this.payoutAccountLegalAgreementId = payoutAccountLegalAgreementId;
        this.payoutAccountLegalAgreementSignedAt = payoutAccountLegalAgreementSignedAt;
        this.payoutAccountPurpose = payoutAccountPurpose;
        this.payoutCancelDetails = payoutCancelDetails;
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

    public PayoutEventCategory getEventCategory() {
        return this.eventCategory;
    }

    public void setEventCategory(PayoutEventCategory eventCategory) {
        this.eventCategory = eventCategory;
    }

    public PayoutEventType getEventType() {
        return this.eventType;
    }

    public void setEventType(PayoutEventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getEventCreatedAt() {
        return this.eventCreatedAt;
    }

    public void setEventCreatedAt(LocalDateTime eventCreatedAt) {
        this.eventCreatedAt = eventCreatedAt;
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

    public String getPayoutId() {
        return this.payoutId;
    }

    public void setPayoutId(String payoutId) {
        this.payoutId = payoutId;
    }

    public LocalDateTime getPayoutCreatedAt() {
        return this.payoutCreatedAt;
    }

    public void setPayoutCreatedAt(LocalDateTime payoutCreatedAt) {
        this.payoutCreatedAt = payoutCreatedAt;
    }

    public PayoutStatus getPayoutStatus() {
        return this.payoutStatus;
    }

    public void setPayoutStatus(PayoutStatus payoutStatus) {
        this.payoutStatus = payoutStatus;
    }

    public Long getPayoutAmount() {
        return this.payoutAmount;
    }

    public void setPayoutAmount(Long payoutAmount) {
        this.payoutAmount = payoutAmount;
    }

    public Long getPayoutFee() {
        return this.payoutFee;
    }

    public void setPayoutFee(Long payoutFee) {
        this.payoutFee = payoutFee;
    }

    public String getPayoutCurrencyCode() {
        return this.payoutCurrencyCode;
    }

    public void setPayoutCurrencyCode(String payoutCurrencyCode) {
        this.payoutCurrencyCode = payoutCurrencyCode;
    }

    public PayoutType getPayoutType() {
        return this.payoutType;
    }

    public void setPayoutType(PayoutType payoutType) {
        this.payoutType = payoutType;
    }

    public String getPayoutCardToken() {
        return this.payoutCardToken;
    }

    public void setPayoutCardToken(String payoutCardToken) {
        this.payoutCardToken = payoutCardToken;
    }

    public String getPayoutCardMaskedPan() {
        return this.payoutCardMaskedPan;
    }

    public void setPayoutCardMaskedPan(String payoutCardMaskedPan) {
        this.payoutCardMaskedPan = payoutCardMaskedPan;
    }

    public String getPayoutCardBin() {
        return this.payoutCardBin;
    }

    public void setPayoutCardBin(String payoutCardBin) {
        this.payoutCardBin = payoutCardBin;
    }

    public String getPayoutCardPaymentSystem() {
        return this.payoutCardPaymentSystem;
    }

    public void setPayoutCardPaymentSystem(String payoutCardPaymentSystem) {
        this.payoutCardPaymentSystem = payoutCardPaymentSystem;
    }

    public String getPayoutAccountBankId() {
        return this.payoutAccountBankId;
    }

    public void setPayoutAccountBankId(String payoutAccountBankId) {
        this.payoutAccountBankId = payoutAccountBankId;
    }

    public String getPayoutAccountBankCorrId() {
        return this.payoutAccountBankCorrId;
    }

    public void setPayoutAccountBankCorrId(String payoutAccountBankCorrId) {
        this.payoutAccountBankCorrId = payoutAccountBankCorrId;
    }

    public String getPayoutAccountBankBik() {
        return this.payoutAccountBankBik;
    }

    public void setPayoutAccountBankBik(String payoutAccountBankBik) {
        this.payoutAccountBankBik = payoutAccountBankBik;
    }

    public String getPayoutAccountBankName() {
        return this.payoutAccountBankName;
    }

    public void setPayoutAccountBankName(String payoutAccountBankName) {
        this.payoutAccountBankName = payoutAccountBankName;
    }

    public String getPayoutAccountInn() {
        return this.payoutAccountInn;
    }

    public void setPayoutAccountInn(String payoutAccountInn) {
        this.payoutAccountInn = payoutAccountInn;
    }

    public String getPayoutAccountLegalAgreementId() {
        return this.payoutAccountLegalAgreementId;
    }

    public void setPayoutAccountLegalAgreementId(String payoutAccountLegalAgreementId) {
        this.payoutAccountLegalAgreementId = payoutAccountLegalAgreementId;
    }

    public LocalDateTime getPayoutAccountLegalAgreementSignedAt() {
        return this.payoutAccountLegalAgreementSignedAt;
    }

    public void setPayoutAccountLegalAgreementSignedAt(LocalDateTime payoutAccountLegalAgreementSignedAt) {
        this.payoutAccountLegalAgreementSignedAt = payoutAccountLegalAgreementSignedAt;
    }

    public String getPayoutAccountPurpose() {
        return this.payoutAccountPurpose;
    }

    public void setPayoutAccountPurpose(String payoutAccountPurpose) {
        this.payoutAccountPurpose = payoutAccountPurpose;
    }

    public String getPayoutCancelDetails() {
        return this.payoutCancelDetails;
    }

    public void setPayoutCancelDetails(String payoutCancelDetails) {
        this.payoutCancelDetails = payoutCancelDetails;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PayoutEventStat other = (PayoutEventStat) obj;
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
        if (eventCategory == null) {
            if (other.eventCategory != null)
                return false;
        }
        else if (!eventCategory.equals(other.eventCategory))
            return false;
        if (eventType == null) {
            if (other.eventType != null)
                return false;
        }
        else if (!eventType.equals(other.eventType))
            return false;
        if (eventCreatedAt == null) {
            if (other.eventCreatedAt != null)
                return false;
        }
        else if (!eventCreatedAt.equals(other.eventCreatedAt))
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
        if (payoutId == null) {
            if (other.payoutId != null)
                return false;
        }
        else if (!payoutId.equals(other.payoutId))
            return false;
        if (payoutCreatedAt == null) {
            if (other.payoutCreatedAt != null)
                return false;
        }
        else if (!payoutCreatedAt.equals(other.payoutCreatedAt))
            return false;
        if (payoutStatus == null) {
            if (other.payoutStatus != null)
                return false;
        }
        else if (!payoutStatus.equals(other.payoutStatus))
            return false;
        if (payoutAmount == null) {
            if (other.payoutAmount != null)
                return false;
        }
        else if (!payoutAmount.equals(other.payoutAmount))
            return false;
        if (payoutFee == null) {
            if (other.payoutFee != null)
                return false;
        }
        else if (!payoutFee.equals(other.payoutFee))
            return false;
        if (payoutCurrencyCode == null) {
            if (other.payoutCurrencyCode != null)
                return false;
        }
        else if (!payoutCurrencyCode.equals(other.payoutCurrencyCode))
            return false;
        if (payoutType == null) {
            if (other.payoutType != null)
                return false;
        }
        else if (!payoutType.equals(other.payoutType))
            return false;
        if (payoutCardToken == null) {
            if (other.payoutCardToken != null)
                return false;
        }
        else if (!payoutCardToken.equals(other.payoutCardToken))
            return false;
        if (payoutCardMaskedPan == null) {
            if (other.payoutCardMaskedPan != null)
                return false;
        }
        else if (!payoutCardMaskedPan.equals(other.payoutCardMaskedPan))
            return false;
        if (payoutCardBin == null) {
            if (other.payoutCardBin != null)
                return false;
        }
        else if (!payoutCardBin.equals(other.payoutCardBin))
            return false;
        if (payoutCardPaymentSystem == null) {
            if (other.payoutCardPaymentSystem != null)
                return false;
        }
        else if (!payoutCardPaymentSystem.equals(other.payoutCardPaymentSystem))
            return false;
        if (payoutAccountBankId == null) {
            if (other.payoutAccountBankId != null)
                return false;
        }
        else if (!payoutAccountBankId.equals(other.payoutAccountBankId))
            return false;
        if (payoutAccountBankCorrId == null) {
            if (other.payoutAccountBankCorrId != null)
                return false;
        }
        else if (!payoutAccountBankCorrId.equals(other.payoutAccountBankCorrId))
            return false;
        if (payoutAccountBankBik == null) {
            if (other.payoutAccountBankBik != null)
                return false;
        }
        else if (!payoutAccountBankBik.equals(other.payoutAccountBankBik))
            return false;
        if (payoutAccountBankName == null) {
            if (other.payoutAccountBankName != null)
                return false;
        }
        else if (!payoutAccountBankName.equals(other.payoutAccountBankName))
            return false;
        if (payoutAccountInn == null) {
            if (other.payoutAccountInn != null)
                return false;
        }
        else if (!payoutAccountInn.equals(other.payoutAccountInn))
            return false;
        if (payoutAccountLegalAgreementId == null) {
            if (other.payoutAccountLegalAgreementId != null)
                return false;
        }
        else if (!payoutAccountLegalAgreementId.equals(other.payoutAccountLegalAgreementId))
            return false;
        if (payoutAccountLegalAgreementSignedAt == null) {
            if (other.payoutAccountLegalAgreementSignedAt != null)
                return false;
        }
        else if (!payoutAccountLegalAgreementSignedAt.equals(other.payoutAccountLegalAgreementSignedAt))
            return false;
        if (payoutAccountPurpose == null) {
            if (other.payoutAccountPurpose != null)
                return false;
        }
        else if (!payoutAccountPurpose.equals(other.payoutAccountPurpose))
            return false;
        if (payoutCancelDetails == null) {
            if (other.payoutCancelDetails != null)
                return false;
        }
        else if (!payoutCancelDetails.equals(other.payoutCancelDetails))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
        result = prime * result + ((eventCategory == null) ? 0 : eventCategory.hashCode());
        result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
        result = prime * result + ((eventCreatedAt == null) ? 0 : eventCreatedAt.hashCode());
        result = prime * result + ((partyId == null) ? 0 : partyId.hashCode());
        result = prime * result + ((partyShopId == null) ? 0 : partyShopId.hashCode());
        result = prime * result + ((payoutId == null) ? 0 : payoutId.hashCode());
        result = prime * result + ((payoutCreatedAt == null) ? 0 : payoutCreatedAt.hashCode());
        result = prime * result + ((payoutStatus == null) ? 0 : payoutStatus.hashCode());
        result = prime * result + ((payoutAmount == null) ? 0 : payoutAmount.hashCode());
        result = prime * result + ((payoutFee == null) ? 0 : payoutFee.hashCode());
        result = prime * result + ((payoutCurrencyCode == null) ? 0 : payoutCurrencyCode.hashCode());
        result = prime * result + ((payoutType == null) ? 0 : payoutType.hashCode());
        result = prime * result + ((payoutCardToken == null) ? 0 : payoutCardToken.hashCode());
        result = prime * result + ((payoutCardMaskedPan == null) ? 0 : payoutCardMaskedPan.hashCode());
        result = prime * result + ((payoutCardBin == null) ? 0 : payoutCardBin.hashCode());
        result = prime * result + ((payoutCardPaymentSystem == null) ? 0 : payoutCardPaymentSystem.hashCode());
        result = prime * result + ((payoutAccountBankId == null) ? 0 : payoutAccountBankId.hashCode());
        result = prime * result + ((payoutAccountBankCorrId == null) ? 0 : payoutAccountBankCorrId.hashCode());
        result = prime * result + ((payoutAccountBankBik == null) ? 0 : payoutAccountBankBik.hashCode());
        result = prime * result + ((payoutAccountBankName == null) ? 0 : payoutAccountBankName.hashCode());
        result = prime * result + ((payoutAccountInn == null) ? 0 : payoutAccountInn.hashCode());
        result = prime * result + ((payoutAccountLegalAgreementId == null) ? 0 : payoutAccountLegalAgreementId.hashCode());
        result = prime * result + ((payoutAccountLegalAgreementSignedAt == null) ? 0 : payoutAccountLegalAgreementSignedAt.hashCode());
        result = prime * result + ((payoutAccountPurpose == null) ? 0 : payoutAccountPurpose.hashCode());
        result = prime * result + ((payoutCancelDetails == null) ? 0 : payoutCancelDetails.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PayoutEventStat (");

        sb.append(id);
        sb.append(", ").append(eventId);
        sb.append(", ").append(eventCategory);
        sb.append(", ").append(eventType);
        sb.append(", ").append(eventCreatedAt);
        sb.append(", ").append(partyId);
        sb.append(", ").append(partyShopId);
        sb.append(", ").append(payoutId);
        sb.append(", ").append(payoutCreatedAt);
        sb.append(", ").append(payoutStatus);
        sb.append(", ").append(payoutAmount);
        sb.append(", ").append(payoutFee);
        sb.append(", ").append(payoutCurrencyCode);
        sb.append(", ").append(payoutType);
        sb.append(", ").append(payoutCardToken);
        sb.append(", ").append(payoutCardMaskedPan);
        sb.append(", ").append(payoutCardBin);
        sb.append(", ").append(payoutCardPaymentSystem);
        sb.append(", ").append(payoutAccountBankId);
        sb.append(", ").append(payoutAccountBankCorrId);
        sb.append(", ").append(payoutAccountBankBik);
        sb.append(", ").append(payoutAccountBankName);
        sb.append(", ").append(payoutAccountInn);
        sb.append(", ").append(payoutAccountLegalAgreementId);
        sb.append(", ").append(payoutAccountLegalAgreementSignedAt);
        sb.append(", ").append(payoutAccountPurpose);
        sb.append(", ").append(payoutCancelDetails);

        sb.append(")");
        return sb.toString();
    }
}
