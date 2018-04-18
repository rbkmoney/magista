/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.magista.domain.tables.pojos;


import com.fasterxml.jackson.databind.JsonNode;

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
public class InvoiceData implements Serializable {

    private static final long serialVersionUID = -675748529;

    private UUID          partyId;
    private String        partyShopId;
    private String        partyContractId;
    private String        invoiceId;
    private String        invoiceProduct;
    private String        invoiceDescription;
    private Long          invoiceAmount;
    private String        invoiceCurrencyCode;
    private LocalDateTime invoiceDue;
    private LocalDateTime invoiceCreatedAt;
    private Long          invoicePartyRevision;
    private String        invoiceTemplateId;
    private JsonNode      invoiceCartJson;
    private String        invoiceContextType;
    private byte[]        invoiceContext;

    public InvoiceData() {}

    public InvoiceData(InvoiceData value) {
        this.partyId = value.partyId;
        this.partyShopId = value.partyShopId;
        this.partyContractId = value.partyContractId;
        this.invoiceId = value.invoiceId;
        this.invoiceProduct = value.invoiceProduct;
        this.invoiceDescription = value.invoiceDescription;
        this.invoiceAmount = value.invoiceAmount;
        this.invoiceCurrencyCode = value.invoiceCurrencyCode;
        this.invoiceDue = value.invoiceDue;
        this.invoiceCreatedAt = value.invoiceCreatedAt;
        this.invoicePartyRevision = value.invoicePartyRevision;
        this.invoiceTemplateId = value.invoiceTemplateId;
        this.invoiceCartJson = value.invoiceCartJson;
        this.invoiceContextType = value.invoiceContextType;
        this.invoiceContext = value.invoiceContext;
    }

    public InvoiceData(
        UUID          partyId,
        String        partyShopId,
        String        partyContractId,
        String        invoiceId,
        String        invoiceProduct,
        String        invoiceDescription,
        Long          invoiceAmount,
        String        invoiceCurrencyCode,
        LocalDateTime invoiceDue,
        LocalDateTime invoiceCreatedAt,
        Long          invoicePartyRevision,
        String        invoiceTemplateId,
        JsonNode      invoiceCartJson,
        String        invoiceContextType,
        byte[]        invoiceContext
    ) {
        this.partyId = partyId;
        this.partyShopId = partyShopId;
        this.partyContractId = partyContractId;
        this.invoiceId = invoiceId;
        this.invoiceProduct = invoiceProduct;
        this.invoiceDescription = invoiceDescription;
        this.invoiceAmount = invoiceAmount;
        this.invoiceCurrencyCode = invoiceCurrencyCode;
        this.invoiceDue = invoiceDue;
        this.invoiceCreatedAt = invoiceCreatedAt;
        this.invoicePartyRevision = invoicePartyRevision;
        this.invoiceTemplateId = invoiceTemplateId;
        this.invoiceCartJson = invoiceCartJson;
        this.invoiceContextType = invoiceContextType;
        this.invoiceContext = invoiceContext;
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

    public String getPartyContractId() {
        return this.partyContractId;
    }

    public void setPartyContractId(String partyContractId) {
        this.partyContractId = partyContractId;
    }

    public String getInvoiceId() {
        return this.invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceProduct() {
        return this.invoiceProduct;
    }

    public void setInvoiceProduct(String invoiceProduct) {
        this.invoiceProduct = invoiceProduct;
    }

    public String getInvoiceDescription() {
        return this.invoiceDescription;
    }

    public void setInvoiceDescription(String invoiceDescription) {
        this.invoiceDescription = invoiceDescription;
    }

    public Long getInvoiceAmount() {
        return this.invoiceAmount;
    }

    public void setInvoiceAmount(Long invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceCurrencyCode() {
        return this.invoiceCurrencyCode;
    }

    public void setInvoiceCurrencyCode(String invoiceCurrencyCode) {
        this.invoiceCurrencyCode = invoiceCurrencyCode;
    }

    public LocalDateTime getInvoiceDue() {
        return this.invoiceDue;
    }

    public void setInvoiceDue(LocalDateTime invoiceDue) {
        this.invoiceDue = invoiceDue;
    }

    public LocalDateTime getInvoiceCreatedAt() {
        return this.invoiceCreatedAt;
    }

    public void setInvoiceCreatedAt(LocalDateTime invoiceCreatedAt) {
        this.invoiceCreatedAt = invoiceCreatedAt;
    }

    public Long getInvoicePartyRevision() {
        return this.invoicePartyRevision;
    }

    public void setInvoicePartyRevision(Long invoicePartyRevision) {
        this.invoicePartyRevision = invoicePartyRevision;
    }

    public String getInvoiceTemplateId() {
        return this.invoiceTemplateId;
    }

    public void setInvoiceTemplateId(String invoiceTemplateId) {
        this.invoiceTemplateId = invoiceTemplateId;
    }

    public JsonNode getInvoiceCartJson() {
        return this.invoiceCartJson;
    }

    public void setInvoiceCartJson(JsonNode invoiceCartJson) {
        this.invoiceCartJson = invoiceCartJson;
    }

    public String getInvoiceContextType() {
        return this.invoiceContextType;
    }

    public void setInvoiceContextType(String invoiceContextType) {
        this.invoiceContextType = invoiceContextType;
    }

    public byte[] getInvoiceContext() {
        return this.invoiceContext;
    }

    public void setInvoiceContext(byte... invoiceContext) {
        this.invoiceContext = invoiceContext;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final InvoiceData other = (InvoiceData) obj;
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
        if (invoiceId == null) {
            if (other.invoiceId != null)
                return false;
        }
        else if (!invoiceId.equals(other.invoiceId))
            return false;
        if (invoiceProduct == null) {
            if (other.invoiceProduct != null)
                return false;
        }
        else if (!invoiceProduct.equals(other.invoiceProduct))
            return false;
        if (invoiceDescription == null) {
            if (other.invoiceDescription != null)
                return false;
        }
        else if (!invoiceDescription.equals(other.invoiceDescription))
            return false;
        if (invoiceAmount == null) {
            if (other.invoiceAmount != null)
                return false;
        }
        else if (!invoiceAmount.equals(other.invoiceAmount))
            return false;
        if (invoiceCurrencyCode == null) {
            if (other.invoiceCurrencyCode != null)
                return false;
        }
        else if (!invoiceCurrencyCode.equals(other.invoiceCurrencyCode))
            return false;
        if (invoiceDue == null) {
            if (other.invoiceDue != null)
                return false;
        }
        else if (!invoiceDue.equals(other.invoiceDue))
            return false;
        if (invoiceCreatedAt == null) {
            if (other.invoiceCreatedAt != null)
                return false;
        }
        else if (!invoiceCreatedAt.equals(other.invoiceCreatedAt))
            return false;
        if (invoicePartyRevision == null) {
            if (other.invoicePartyRevision != null)
                return false;
        }
        else if (!invoicePartyRevision.equals(other.invoicePartyRevision))
            return false;
        if (invoiceTemplateId == null) {
            if (other.invoiceTemplateId != null)
                return false;
        }
        else if (!invoiceTemplateId.equals(other.invoiceTemplateId))
            return false;
        if (invoiceCartJson == null) {
            if (other.invoiceCartJson != null)
                return false;
        }
        else if (!invoiceCartJson.equals(other.invoiceCartJson))
            return false;
        if (invoiceContextType == null) {
            if (other.invoiceContextType != null)
                return false;
        }
        else if (!invoiceContextType.equals(other.invoiceContextType))
            return false;
        if (invoiceContext == null) {
            if (other.invoiceContext != null)
                return false;
        }
        else if (!Arrays.equals(invoiceContext, other.invoiceContext))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.partyId == null) ? 0 : this.partyId.hashCode());
        result = prime * result + ((this.partyShopId == null) ? 0 : this.partyShopId.hashCode());
        result = prime * result + ((this.partyContractId == null) ? 0 : this.partyContractId.hashCode());
        result = prime * result + ((this.invoiceId == null) ? 0 : this.invoiceId.hashCode());
        result = prime * result + ((this.invoiceProduct == null) ? 0 : this.invoiceProduct.hashCode());
        result = prime * result + ((this.invoiceDescription == null) ? 0 : this.invoiceDescription.hashCode());
        result = prime * result + ((this.invoiceAmount == null) ? 0 : this.invoiceAmount.hashCode());
        result = prime * result + ((this.invoiceCurrencyCode == null) ? 0 : this.invoiceCurrencyCode.hashCode());
        result = prime * result + ((this.invoiceDue == null) ? 0 : this.invoiceDue.hashCode());
        result = prime * result + ((this.invoiceCreatedAt == null) ? 0 : this.invoiceCreatedAt.hashCode());
        result = prime * result + ((this.invoicePartyRevision == null) ? 0 : this.invoicePartyRevision.hashCode());
        result = prime * result + ((this.invoiceTemplateId == null) ? 0 : this.invoiceTemplateId.hashCode());
        result = prime * result + ((this.invoiceCartJson == null) ? 0 : this.invoiceCartJson.hashCode());
        result = prime * result + ((this.invoiceContextType == null) ? 0 : this.invoiceContextType.hashCode());
        result = prime * result + ((this.invoiceContext == null) ? 0 : Arrays.hashCode(this.invoiceContext));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("InvoiceData (");

        sb.append(partyId);
        sb.append(", ").append(partyShopId);
        sb.append(", ").append(partyContractId);
        sb.append(", ").append(invoiceId);
        sb.append(", ").append(invoiceProduct);
        sb.append(", ").append(invoiceDescription);
        sb.append(", ").append(invoiceAmount);
        sb.append(", ").append(invoiceCurrencyCode);
        sb.append(", ").append(invoiceDue);
        sb.append(", ").append(invoiceCreatedAt);
        sb.append(", ").append(invoicePartyRevision);
        sb.append(", ").append(invoiceTemplateId);
        sb.append(", ").append(invoiceCartJson);
        sb.append(", ").append(invoiceContextType);
        sb.append(", ").append("[binary...]");

        sb.append(")");
        return sb.toString();
    }
}
