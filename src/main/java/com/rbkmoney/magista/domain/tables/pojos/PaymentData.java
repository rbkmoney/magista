/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.magista.domain.tables.pojos;


import com.rbkmoney.magista.domain.enums.BankCardTokenProvider;
import com.rbkmoney.magista.domain.enums.OnHoldExpiration;
import com.rbkmoney.magista.domain.enums.PaymentFlow;
import com.rbkmoney.magista.domain.enums.PaymentTool;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

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
public class PaymentData implements Serializable {

    private static final long serialVersionUID = 432978083;

    private Long                  id;
    private String                invoiceId;
    private String                paymentId;
    private UUID                  partyId;
    private String                partyShopId;
    private String                paymentCurrencyCode;
    private Long                  paymentAmount;
    private String                paymentCustomerId;
    private PaymentTool           paymentTool;
    private String                paymentBankCardMaskedPan;
    private String                paymentBankCardBin;
    private String                paymentBankCardToken;
    private String                paymentBankCardSystem;
    private BankCardTokenProvider paymentBankCardTokenProvider;
    private String                paymentTerminalProvider;
    private String                paymentDigitalWalletId;
    private String                paymentDigitalWalletProvider;
    private PaymentFlow           paymentFlow;
    private OnHoldExpiration      paymentHoldOnExpiration;
    private LocalDateTime         paymentHoldUntil;
    private String                paymentSessionId;
    private String                paymentFingerprint;
    private String                paymentIp;
    private String                paymentPhoneNumber;
    private String                paymentEmail;
    private LocalDateTime         paymentCreatedAt;
    private Long                  paymentPartyRevision;
    private String                paymentContextType;
    private byte[]                paymentContext;
    private Boolean               paymentMakeRecurrentFlag;
    private String                paymentRecurrentPayerParentInvoiceId;
    private String                paymentRecurrentPayerParentPaymentId;

    public PaymentData() {}

    public PaymentData(PaymentData value) {
        this.id = value.id;
        this.invoiceId = value.invoiceId;
        this.paymentId = value.paymentId;
        this.partyId = value.partyId;
        this.partyShopId = value.partyShopId;
        this.paymentCurrencyCode = value.paymentCurrencyCode;
        this.paymentAmount = value.paymentAmount;
        this.paymentCustomerId = value.paymentCustomerId;
        this.paymentTool = value.paymentTool;
        this.paymentBankCardMaskedPan = value.paymentBankCardMaskedPan;
        this.paymentBankCardBin = value.paymentBankCardBin;
        this.paymentBankCardToken = value.paymentBankCardToken;
        this.paymentBankCardSystem = value.paymentBankCardSystem;
        this.paymentBankCardTokenProvider = value.paymentBankCardTokenProvider;
        this.paymentTerminalProvider = value.paymentTerminalProvider;
        this.paymentDigitalWalletId = value.paymentDigitalWalletId;
        this.paymentDigitalWalletProvider = value.paymentDigitalWalletProvider;
        this.paymentFlow = value.paymentFlow;
        this.paymentHoldOnExpiration = value.paymentHoldOnExpiration;
        this.paymentHoldUntil = value.paymentHoldUntil;
        this.paymentSessionId = value.paymentSessionId;
        this.paymentFingerprint = value.paymentFingerprint;
        this.paymentIp = value.paymentIp;
        this.paymentPhoneNumber = value.paymentPhoneNumber;
        this.paymentEmail = value.paymentEmail;
        this.paymentCreatedAt = value.paymentCreatedAt;
        this.paymentPartyRevision = value.paymentPartyRevision;
        this.paymentContextType = value.paymentContextType;
        this.paymentContext = value.paymentContext;
        this.paymentMakeRecurrentFlag = value.paymentMakeRecurrentFlag;
        this.paymentRecurrentPayerParentInvoiceId = value.paymentRecurrentPayerParentInvoiceId;
        this.paymentRecurrentPayerParentPaymentId = value.paymentRecurrentPayerParentPaymentId;
    }

    public PaymentData(
        Long                  id,
        String                invoiceId,
        String                paymentId,
        UUID                  partyId,
        String                partyShopId,
        String                paymentCurrencyCode,
        Long                  paymentAmount,
        String                paymentCustomerId,
        PaymentTool           paymentTool,
        String                paymentBankCardMaskedPan,
        String                paymentBankCardBin,
        String                paymentBankCardToken,
        String                paymentBankCardSystem,
        BankCardTokenProvider paymentBankCardTokenProvider,
        String                paymentTerminalProvider,
        String                paymentDigitalWalletId,
        String                paymentDigitalWalletProvider,
        PaymentFlow           paymentFlow,
        OnHoldExpiration      paymentHoldOnExpiration,
        LocalDateTime         paymentHoldUntil,
        String                paymentSessionId,
        String                paymentFingerprint,
        String                paymentIp,
        String                paymentPhoneNumber,
        String                paymentEmail,
        LocalDateTime         paymentCreatedAt,
        Long                  paymentPartyRevision,
        String                paymentContextType,
        byte[]                paymentContext,
        Boolean               paymentMakeRecurrentFlag,
        String                paymentRecurrentPayerParentInvoiceId,
        String                paymentRecurrentPayerParentPaymentId
    ) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.paymentId = paymentId;
        this.partyId = partyId;
        this.partyShopId = partyShopId;
        this.paymentCurrencyCode = paymentCurrencyCode;
        this.paymentAmount = paymentAmount;
        this.paymentCustomerId = paymentCustomerId;
        this.paymentTool = paymentTool;
        this.paymentBankCardMaskedPan = paymentBankCardMaskedPan;
        this.paymentBankCardBin = paymentBankCardBin;
        this.paymentBankCardToken = paymentBankCardToken;
        this.paymentBankCardSystem = paymentBankCardSystem;
        this.paymentBankCardTokenProvider = paymentBankCardTokenProvider;
        this.paymentTerminalProvider = paymentTerminalProvider;
        this.paymentDigitalWalletId = paymentDigitalWalletId;
        this.paymentDigitalWalletProvider = paymentDigitalWalletProvider;
        this.paymentFlow = paymentFlow;
        this.paymentHoldOnExpiration = paymentHoldOnExpiration;
        this.paymentHoldUntil = paymentHoldUntil;
        this.paymentSessionId = paymentSessionId;
        this.paymentFingerprint = paymentFingerprint;
        this.paymentIp = paymentIp;
        this.paymentPhoneNumber = paymentPhoneNumber;
        this.paymentEmail = paymentEmail;
        this.paymentCreatedAt = paymentCreatedAt;
        this.paymentPartyRevision = paymentPartyRevision;
        this.paymentContextType = paymentContextType;
        this.paymentContext = paymentContext;
        this.paymentMakeRecurrentFlag = paymentMakeRecurrentFlag;
        this.paymentRecurrentPayerParentInvoiceId = paymentRecurrentPayerParentInvoiceId;
        this.paymentRecurrentPayerParentPaymentId = paymentRecurrentPayerParentPaymentId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UUID getPartyId() {
        return this.partyId;
    }

    public void setPartyId(UUID partyId) {
        this.partyId = partyId;
    }

    public String getPartyShopId() {
        return this.partyShopId;
    }

    public void setPartyShopId(String partyShopId) {
        this.partyShopId = partyShopId;
    }

    public String getPaymentCurrencyCode() {
        return this.paymentCurrencyCode;
    }

    public void setPaymentCurrencyCode(String paymentCurrencyCode) {
        this.paymentCurrencyCode = paymentCurrencyCode;
    }

    public Long getPaymentAmount() {
        return this.paymentAmount;
    }

    public void setPaymentAmount(Long paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentCustomerId() {
        return this.paymentCustomerId;
    }

    public void setPaymentCustomerId(String paymentCustomerId) {
        this.paymentCustomerId = paymentCustomerId;
    }

    public PaymentTool getPaymentTool() {
        return this.paymentTool;
    }

    public void setPaymentTool(PaymentTool paymentTool) {
        this.paymentTool = paymentTool;
    }

    public String getPaymentBankCardMaskedPan() {
        return this.paymentBankCardMaskedPan;
    }

    public void setPaymentBankCardMaskedPan(String paymentBankCardMaskedPan) {
        this.paymentBankCardMaskedPan = paymentBankCardMaskedPan;
    }

    public String getPaymentBankCardBin() {
        return this.paymentBankCardBin;
    }

    public void setPaymentBankCardBin(String paymentBankCardBin) {
        this.paymentBankCardBin = paymentBankCardBin;
    }

    public String getPaymentBankCardToken() {
        return this.paymentBankCardToken;
    }

    public void setPaymentBankCardToken(String paymentBankCardToken) {
        this.paymentBankCardToken = paymentBankCardToken;
    }

    public String getPaymentBankCardSystem() {
        return this.paymentBankCardSystem;
    }

    public void setPaymentBankCardSystem(String paymentBankCardSystem) {
        this.paymentBankCardSystem = paymentBankCardSystem;
    }

    public BankCardTokenProvider getPaymentBankCardTokenProvider() {
        return this.paymentBankCardTokenProvider;
    }

    public void setPaymentBankCardTokenProvider(BankCardTokenProvider paymentBankCardTokenProvider) {
        this.paymentBankCardTokenProvider = paymentBankCardTokenProvider;
    }

    public String getPaymentTerminalProvider() {
        return this.paymentTerminalProvider;
    }

    public void setPaymentTerminalProvider(String paymentTerminalProvider) {
        this.paymentTerminalProvider = paymentTerminalProvider;
    }

    public String getPaymentDigitalWalletId() {
        return this.paymentDigitalWalletId;
    }

    public void setPaymentDigitalWalletId(String paymentDigitalWalletId) {
        this.paymentDigitalWalletId = paymentDigitalWalletId;
    }

    public String getPaymentDigitalWalletProvider() {
        return this.paymentDigitalWalletProvider;
    }

    public void setPaymentDigitalWalletProvider(String paymentDigitalWalletProvider) {
        this.paymentDigitalWalletProvider = paymentDigitalWalletProvider;
    }

    public PaymentFlow getPaymentFlow() {
        return this.paymentFlow;
    }

    public void setPaymentFlow(PaymentFlow paymentFlow) {
        this.paymentFlow = paymentFlow;
    }

    public OnHoldExpiration getPaymentHoldOnExpiration() {
        return this.paymentHoldOnExpiration;
    }

    public void setPaymentHoldOnExpiration(OnHoldExpiration paymentHoldOnExpiration) {
        this.paymentHoldOnExpiration = paymentHoldOnExpiration;
    }

    public LocalDateTime getPaymentHoldUntil() {
        return this.paymentHoldUntil;
    }

    public void setPaymentHoldUntil(LocalDateTime paymentHoldUntil) {
        this.paymentHoldUntil = paymentHoldUntil;
    }

    public String getPaymentSessionId() {
        return this.paymentSessionId;
    }

    public void setPaymentSessionId(String paymentSessionId) {
        this.paymentSessionId = paymentSessionId;
    }

    public String getPaymentFingerprint() {
        return this.paymentFingerprint;
    }

    public void setPaymentFingerprint(String paymentFingerprint) {
        this.paymentFingerprint = paymentFingerprint;
    }

    public String getPaymentIp() {
        return this.paymentIp;
    }

    public void setPaymentIp(String paymentIp) {
        this.paymentIp = paymentIp;
    }

    public String getPaymentPhoneNumber() {
        return this.paymentPhoneNumber;
    }

    public void setPaymentPhoneNumber(String paymentPhoneNumber) {
        this.paymentPhoneNumber = paymentPhoneNumber;
    }

    public String getPaymentEmail() {
        return this.paymentEmail;
    }

    public void setPaymentEmail(String paymentEmail) {
        this.paymentEmail = paymentEmail;
    }

    public LocalDateTime getPaymentCreatedAt() {
        return this.paymentCreatedAt;
    }

    public void setPaymentCreatedAt(LocalDateTime paymentCreatedAt) {
        this.paymentCreatedAt = paymentCreatedAt;
    }

    public Long getPaymentPartyRevision() {
        return this.paymentPartyRevision;
    }

    public void setPaymentPartyRevision(Long paymentPartyRevision) {
        this.paymentPartyRevision = paymentPartyRevision;
    }

    public String getPaymentContextType() {
        return this.paymentContextType;
    }

    public void setPaymentContextType(String paymentContextType) {
        this.paymentContextType = paymentContextType;
    }

    public byte[] getPaymentContext() {
        return this.paymentContext;
    }

    public void setPaymentContext(byte... paymentContext) {
        this.paymentContext = paymentContext;
    }

    public Boolean getPaymentMakeRecurrentFlag() {
        return this.paymentMakeRecurrentFlag;
    }

    public void setPaymentMakeRecurrentFlag(Boolean paymentMakeRecurrentFlag) {
        this.paymentMakeRecurrentFlag = paymentMakeRecurrentFlag;
    }

    public String getPaymentRecurrentPayerParentInvoiceId() {
        return this.paymentRecurrentPayerParentInvoiceId;
    }

    public void setPaymentRecurrentPayerParentInvoiceId(String paymentRecurrentPayerParentInvoiceId) {
        this.paymentRecurrentPayerParentInvoiceId = paymentRecurrentPayerParentInvoiceId;
    }

    public String getPaymentRecurrentPayerParentPaymentId() {
        return this.paymentRecurrentPayerParentPaymentId;
    }

    public void setPaymentRecurrentPayerParentPaymentId(String paymentRecurrentPayerParentPaymentId) {
        this.paymentRecurrentPayerParentPaymentId = paymentRecurrentPayerParentPaymentId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PaymentData other = (PaymentData) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
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
        if (paymentCurrencyCode == null) {
            if (other.paymentCurrencyCode != null)
                return false;
        }
        else if (!paymentCurrencyCode.equals(other.paymentCurrencyCode))
            return false;
        if (paymentAmount == null) {
            if (other.paymentAmount != null)
                return false;
        }
        else if (!paymentAmount.equals(other.paymentAmount))
            return false;
        if (paymentCustomerId == null) {
            if (other.paymentCustomerId != null)
                return false;
        }
        else if (!paymentCustomerId.equals(other.paymentCustomerId))
            return false;
        if (paymentTool == null) {
            if (other.paymentTool != null)
                return false;
        }
        else if (!paymentTool.equals(other.paymentTool))
            return false;
        if (paymentBankCardMaskedPan == null) {
            if (other.paymentBankCardMaskedPan != null)
                return false;
        }
        else if (!paymentBankCardMaskedPan.equals(other.paymentBankCardMaskedPan))
            return false;
        if (paymentBankCardBin == null) {
            if (other.paymentBankCardBin != null)
                return false;
        }
        else if (!paymentBankCardBin.equals(other.paymentBankCardBin))
            return false;
        if (paymentBankCardToken == null) {
            if (other.paymentBankCardToken != null)
                return false;
        }
        else if (!paymentBankCardToken.equals(other.paymentBankCardToken))
            return false;
        if (paymentBankCardSystem == null) {
            if (other.paymentBankCardSystem != null)
                return false;
        }
        else if (!paymentBankCardSystem.equals(other.paymentBankCardSystem))
            return false;
        if (paymentBankCardTokenProvider == null) {
            if (other.paymentBankCardTokenProvider != null)
                return false;
        }
        else if (!paymentBankCardTokenProvider.equals(other.paymentBankCardTokenProvider))
            return false;
        if (paymentTerminalProvider == null) {
            if (other.paymentTerminalProvider != null)
                return false;
        }
        else if (!paymentTerminalProvider.equals(other.paymentTerminalProvider))
            return false;
        if (paymentDigitalWalletId == null) {
            if (other.paymentDigitalWalletId != null)
                return false;
        }
        else if (!paymentDigitalWalletId.equals(other.paymentDigitalWalletId))
            return false;
        if (paymentDigitalWalletProvider == null) {
            if (other.paymentDigitalWalletProvider != null)
                return false;
        }
        else if (!paymentDigitalWalletProvider.equals(other.paymentDigitalWalletProvider))
            return false;
        if (paymentFlow == null) {
            if (other.paymentFlow != null)
                return false;
        }
        else if (!paymentFlow.equals(other.paymentFlow))
            return false;
        if (paymentHoldOnExpiration == null) {
            if (other.paymentHoldOnExpiration != null)
                return false;
        }
        else if (!paymentHoldOnExpiration.equals(other.paymentHoldOnExpiration))
            return false;
        if (paymentHoldUntil == null) {
            if (other.paymentHoldUntil != null)
                return false;
        }
        else if (!paymentHoldUntil.equals(other.paymentHoldUntil))
            return false;
        if (paymentSessionId == null) {
            if (other.paymentSessionId != null)
                return false;
        }
        else if (!paymentSessionId.equals(other.paymentSessionId))
            return false;
        if (paymentFingerprint == null) {
            if (other.paymentFingerprint != null)
                return false;
        }
        else if (!paymentFingerprint.equals(other.paymentFingerprint))
            return false;
        if (paymentIp == null) {
            if (other.paymentIp != null)
                return false;
        }
        else if (!paymentIp.equals(other.paymentIp))
            return false;
        if (paymentPhoneNumber == null) {
            if (other.paymentPhoneNumber != null)
                return false;
        }
        else if (!paymentPhoneNumber.equals(other.paymentPhoneNumber))
            return false;
        if (paymentEmail == null) {
            if (other.paymentEmail != null)
                return false;
        }
        else if (!paymentEmail.equals(other.paymentEmail))
            return false;
        if (paymentCreatedAt == null) {
            if (other.paymentCreatedAt != null)
                return false;
        }
        else if (!paymentCreatedAt.equals(other.paymentCreatedAt))
            return false;
        if (paymentPartyRevision == null) {
            if (other.paymentPartyRevision != null)
                return false;
        }
        else if (!paymentPartyRevision.equals(other.paymentPartyRevision))
            return false;
        if (paymentContextType == null) {
            if (other.paymentContextType != null)
                return false;
        }
        else if (!paymentContextType.equals(other.paymentContextType))
            return false;
        if (paymentContext == null) {
            if (other.paymentContext != null)
                return false;
        }
        else if (!Arrays.equals(paymentContext, other.paymentContext))
            return false;
        if (paymentMakeRecurrentFlag == null) {
            if (other.paymentMakeRecurrentFlag != null)
                return false;
        }
        else if (!paymentMakeRecurrentFlag.equals(other.paymentMakeRecurrentFlag))
            return false;
        if (paymentRecurrentPayerParentInvoiceId == null) {
            if (other.paymentRecurrentPayerParentInvoiceId != null)
                return false;
        }
        else if (!paymentRecurrentPayerParentInvoiceId.equals(other.paymentRecurrentPayerParentInvoiceId))
            return false;
        if (paymentRecurrentPayerParentPaymentId == null) {
            if (other.paymentRecurrentPayerParentPaymentId != null)
                return false;
        }
        else if (!paymentRecurrentPayerParentPaymentId.equals(other.paymentRecurrentPayerParentPaymentId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.invoiceId == null) ? 0 : this.invoiceId.hashCode());
        result = prime * result + ((this.paymentId == null) ? 0 : this.paymentId.hashCode());
        result = prime * result + ((this.partyId == null) ? 0 : this.partyId.hashCode());
        result = prime * result + ((this.partyShopId == null) ? 0 : this.partyShopId.hashCode());
        result = prime * result + ((this.paymentCurrencyCode == null) ? 0 : this.paymentCurrencyCode.hashCode());
        result = prime * result + ((this.paymentAmount == null) ? 0 : this.paymentAmount.hashCode());
        result = prime * result + ((this.paymentCustomerId == null) ? 0 : this.paymentCustomerId.hashCode());
        result = prime * result + ((this.paymentTool == null) ? 0 : this.paymentTool.hashCode());
        result = prime * result + ((this.paymentBankCardMaskedPan == null) ? 0 : this.paymentBankCardMaskedPan.hashCode());
        result = prime * result + ((this.paymentBankCardBin == null) ? 0 : this.paymentBankCardBin.hashCode());
        result = prime * result + ((this.paymentBankCardToken == null) ? 0 : this.paymentBankCardToken.hashCode());
        result = prime * result + ((this.paymentBankCardSystem == null) ? 0 : this.paymentBankCardSystem.hashCode());
        result = prime * result + ((this.paymentBankCardTokenProvider == null) ? 0 : this.paymentBankCardTokenProvider.hashCode());
        result = prime * result + ((this.paymentTerminalProvider == null) ? 0 : this.paymentTerminalProvider.hashCode());
        result = prime * result + ((this.paymentDigitalWalletId == null) ? 0 : this.paymentDigitalWalletId.hashCode());
        result = prime * result + ((this.paymentDigitalWalletProvider == null) ? 0 : this.paymentDigitalWalletProvider.hashCode());
        result = prime * result + ((this.paymentFlow == null) ? 0 : this.paymentFlow.hashCode());
        result = prime * result + ((this.paymentHoldOnExpiration == null) ? 0 : this.paymentHoldOnExpiration.hashCode());
        result = prime * result + ((this.paymentHoldUntil == null) ? 0 : this.paymentHoldUntil.hashCode());
        result = prime * result + ((this.paymentSessionId == null) ? 0 : this.paymentSessionId.hashCode());
        result = prime * result + ((this.paymentFingerprint == null) ? 0 : this.paymentFingerprint.hashCode());
        result = prime * result + ((this.paymentIp == null) ? 0 : this.paymentIp.hashCode());
        result = prime * result + ((this.paymentPhoneNumber == null) ? 0 : this.paymentPhoneNumber.hashCode());
        result = prime * result + ((this.paymentEmail == null) ? 0 : this.paymentEmail.hashCode());
        result = prime * result + ((this.paymentCreatedAt == null) ? 0 : this.paymentCreatedAt.hashCode());
        result = prime * result + ((this.paymentPartyRevision == null) ? 0 : this.paymentPartyRevision.hashCode());
        result = prime * result + ((this.paymentContextType == null) ? 0 : this.paymentContextType.hashCode());
        result = prime * result + ((this.paymentContext == null) ? 0 : Arrays.hashCode(this.paymentContext));
        result = prime * result + ((this.paymentMakeRecurrentFlag == null) ? 0 : this.paymentMakeRecurrentFlag.hashCode());
        result = prime * result + ((this.paymentRecurrentPayerParentInvoiceId == null) ? 0 : this.paymentRecurrentPayerParentInvoiceId.hashCode());
        result = prime * result + ((this.paymentRecurrentPayerParentPaymentId == null) ? 0 : this.paymentRecurrentPayerParentPaymentId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PaymentData (");

        sb.append(id);
        sb.append(", ").append(invoiceId);
        sb.append(", ").append(paymentId);
        sb.append(", ").append(partyId);
        sb.append(", ").append(partyShopId);
        sb.append(", ").append(paymentCurrencyCode);
        sb.append(", ").append(paymentAmount);
        sb.append(", ").append(paymentCustomerId);
        sb.append(", ").append(paymentTool);
        sb.append(", ").append(paymentBankCardMaskedPan);
        sb.append(", ").append(paymentBankCardBin);
        sb.append(", ").append(paymentBankCardToken);
        sb.append(", ").append(paymentBankCardSystem);
        sb.append(", ").append(paymentBankCardTokenProvider);
        sb.append(", ").append(paymentTerminalProvider);
        sb.append(", ").append(paymentDigitalWalletId);
        sb.append(", ").append(paymentDigitalWalletProvider);
        sb.append(", ").append(paymentFlow);
        sb.append(", ").append(paymentHoldOnExpiration);
        sb.append(", ").append(paymentHoldUntil);
        sb.append(", ").append(paymentSessionId);
        sb.append(", ").append(paymentFingerprint);
        sb.append(", ").append(paymentIp);
        sb.append(", ").append(paymentPhoneNumber);
        sb.append(", ").append(paymentEmail);
        sb.append(", ").append(paymentCreatedAt);
        sb.append(", ").append(paymentPartyRevision);
        sb.append(", ").append(paymentContextType);
        sb.append(", ").append("[binary...]");
        sb.append(", ").append(paymentMakeRecurrentFlag);
        sb.append(", ").append(paymentRecurrentPayerParentInvoiceId);
        sb.append(", ").append(paymentRecurrentPayerParentPaymentId);

        sb.append(")");
        return sb.toString();
    }
}
