/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.magista.domain.tables.records;


import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.BankCardTokenProvider;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.InvoicePaymentRefundStatus;
import com.rbkmoney.magista.domain.enums.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceStatus;
import com.rbkmoney.magista.domain.tables.InvoiceEventStat;

import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


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
public class InvoiceEventStatRecord extends UpdatableRecordImpl<InvoiceEventStatRecord> {

    private static final long serialVersionUID = 1503071798;

    /**
     * Setter for <code>mst.invoice_event_stat.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.event_id</code>.
     */
    public void setEventId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.event_id</code>.
     */
    public Long getEventId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.event_category</code>.
     */
    public void setEventCategory(InvoiceEventCategory value) {
        set(2, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.event_category</code>.
     */
    public InvoiceEventCategory getEventCategory() {
        return (InvoiceEventCategory) get(2);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.event_type</code>.
     */
    public void setEventType(InvoiceEventType value) {
        set(3, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.event_type</code>.
     */
    public InvoiceEventType getEventType() {
        return (InvoiceEventType) get(3);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.event_created_at</code>.
     */
    public void setEventCreatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.event_created_at</code>.
     */
    public LocalDateTime getEventCreatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.party_id</code>.
     */
    public void setPartyId(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.party_id</code>.
     */
    public String getPartyId() {
        return (String) get(5);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.party_email</code>.
     */
    public void setPartyEmail(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.party_email</code>.
     */
    public String getPartyEmail() {
        return (String) get(6);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.party_shop_id</code>.
     */
    public void setPartyShopId(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.party_shop_id</code>.
     */
    public String getPartyShopId() {
        return (String) get(7);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.party_shop_name</code>.
     */
    public void setPartyShopName(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.party_shop_name</code>.
     */
    public String getPartyShopName() {
        return (String) get(8);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.party_shop_description</code>.
     */
    public void setPartyShopDescription(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.party_shop_description</code>.
     */
    public String getPartyShopDescription() {
        return (String) get(9);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.party_shop_url</code>.
     */
    public void setPartyShopUrl(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.party_shop_url</code>.
     */
    public String getPartyShopUrl() {
        return (String) get(10);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.party_shop_category_id</code>.
     */
    public void setPartyShopCategoryId(Integer value) {
        set(11, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.party_shop_category_id</code>.
     */
    public Integer getPartyShopCategoryId() {
        return (Integer) get(11);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.party_shop_payout_tool_id</code>.
     */
    public void setPartyShopPayoutToolId(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.party_shop_payout_tool_id</code>.
     */
    public String getPartyShopPayoutToolId() {
        return (String) get(12);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.party_contract_id</code>.
     */
    public void setPartyContractId(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.party_contract_id</code>.
     */
    public String getPartyContractId() {
        return (String) get(13);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.party_contract_registered_number</code>.
     */
    public void setPartyContractRegisteredNumber(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.party_contract_registered_number</code>.
     */
    public String getPartyContractRegisteredNumber() {
        return (String) get(14);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.party_contract_inn</code>.
     */
    public void setPartyContractInn(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.party_contract_inn</code>.
     */
    public String getPartyContractInn() {
        return (String) get(15);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.invoice_id</code>.
     */
    public void setInvoiceId(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.invoice_id</code>.
     */
    public String getInvoiceId() {
        return (String) get(16);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.invoice_status</code>.
     */
    public void setInvoiceStatus(InvoiceStatus value) {
        set(17, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.invoice_status</code>.
     */
    public InvoiceStatus getInvoiceStatus() {
        return (InvoiceStatus) get(17);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.invoice_status_details</code>.
     */
    public void setInvoiceStatusDetails(String value) {
        set(18, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.invoice_status_details</code>.
     */
    public String getInvoiceStatusDetails() {
        return (String) get(18);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.invoice_product</code>.
     */
    public void setInvoiceProduct(String value) {
        set(19, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.invoice_product</code>.
     */
    public String getInvoiceProduct() {
        return (String) get(19);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.invoice_description</code>.
     */
    public void setInvoiceDescription(String value) {
        set(20, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.invoice_description</code>.
     */
    public String getInvoiceDescription() {
        return (String) get(20);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.invoice_amount</code>.
     */
    public void setInvoiceAmount(Long value) {
        set(21, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.invoice_amount</code>.
     */
    public Long getInvoiceAmount() {
        return (Long) get(21);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.invoice_currency_code</code>.
     */
    public void setInvoiceCurrencyCode(String value) {
        set(22, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.invoice_currency_code</code>.
     */
    public String getInvoiceCurrencyCode() {
        return (String) get(22);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.invoice_due</code>.
     */
    public void setInvoiceDue(LocalDateTime value) {
        set(23, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.invoice_due</code>.
     */
    public LocalDateTime getInvoiceDue() {
        return (LocalDateTime) get(23);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.invoice_created_at</code>.
     */
    public void setInvoiceCreatedAt(LocalDateTime value) {
        set(24, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.invoice_created_at</code>.
     */
    public LocalDateTime getInvoiceCreatedAt() {
        return (LocalDateTime) get(24);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.invoice_context</code>.
     */
    public void setInvoiceContext(byte... value) {
        set(25, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.invoice_context</code>.
     */
    public byte[] getInvoiceContext() {
        return (byte[]) get(25);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_id</code>.
     */
    public void setPaymentId(String value) {
        set(26, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_id</code>.
     */
    public String getPaymentId() {
        return (String) get(26);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_status</code>.
     */
    public void setPaymentStatus(InvoicePaymentStatus value) {
        set(27, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_status</code>.
     */
    public InvoicePaymentStatus getPaymentStatus() {
        return (InvoicePaymentStatus) get(27);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_external_failure_code</code>.
     */
    public void setPaymentExternalFailureCode(String value) {
        set(28, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_external_failure_code</code>.
     */
    public String getPaymentExternalFailureCode() {
        return (String) get(28);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_external_failure_description</code>.
     */
    public void setPaymentExternalFailureDescription(String value) {
        set(29, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_external_failure_description</code>.
     */
    public String getPaymentExternalFailureDescription() {
        return (String) get(29);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_amount</code>.
     */
    public void setPaymentAmount(Long value) {
        set(30, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_amount</code>.
     */
    public Long getPaymentAmount() {
        return (Long) get(30);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_currency_code</code>.
     */
    public void setPaymentCurrencyCode(String value) {
        set(31, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_currency_code</code>.
     */
    public String getPaymentCurrencyCode() {
        return (String) get(31);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_fee</code>.
     */
    public void setPaymentFee(Long value) {
        set(32, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_fee</code>.
     */
    public Long getPaymentFee() {
        return (Long) get(32);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_provider_fee</code>.
     */
    public void setPaymentProviderFee(Long value) {
        set(33, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_provider_fee</code>.
     */
    public Long getPaymentProviderFee() {
        return (Long) get(33);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_external_fee</code>.
     */
    public void setPaymentExternalFee(Long value) {
        set(34, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_external_fee</code>.
     */
    public Long getPaymentExternalFee() {
        return (Long) get(34);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_tool</code>.
     */
    public void setPaymentTool(String value) {
        set(35, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_tool</code>.
     */
    public String getPaymentTool() {
        return (String) get(35);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_masked_pan</code>.
     */
    public void setPaymentMaskedPan(String value) {
        set(36, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_masked_pan</code>.
     */
    public String getPaymentMaskedPan() {
        return (String) get(36);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_bin</code>.
     */
    public void setPaymentBin(String value) {
        set(37, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_bin</code>.
     */
    public String getPaymentBin() {
        return (String) get(37);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_token</code>.
     */
    public void setPaymentToken(String value) {
        set(38, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_token</code>.
     */
    public String getPaymentToken() {
        return (String) get(38);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_system</code>.
     */
    public void setPaymentSystem(String value) {
        set(39, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_system</code>.
     */
    public String getPaymentSystem() {
        return (String) get(39);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_session_id</code>.
     */
    public void setPaymentSessionId(String value) {
        set(40, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_session_id</code>.
     */
    public String getPaymentSessionId() {
        return (String) get(40);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_country_id</code>.
     */
    public void setPaymentCountryId(Integer value) {
        set(41, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_country_id</code>.
     */
    public Integer getPaymentCountryId() {
        return (Integer) get(41);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_city_id</code>.
     */
    public void setPaymentCityId(Integer value) {
        set(42, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_city_id</code>.
     */
    public Integer getPaymentCityId() {
        return (Integer) get(42);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_ip</code>.
     */
    public void setPaymentIp(String value) {
        set(43, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_ip</code>.
     */
    public String getPaymentIp() {
        return (String) get(43);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_phone_number</code>.
     */
    public void setPaymentPhoneNumber(String value) {
        set(44, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_phone_number</code>.
     */
    public String getPaymentPhoneNumber() {
        return (String) get(44);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_email</code>.
     */
    public void setPaymentEmail(String value) {
        set(45, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_email</code>.
     */
    public String getPaymentEmail() {
        return (String) get(45);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_fingerprint</code>.
     */
    public void setPaymentFingerprint(String value) {
        set(46, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_fingerprint</code>.
     */
    public String getPaymentFingerprint() {
        return (String) get(46);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_created_at</code>.
     */
    public void setPaymentCreatedAt(LocalDateTime value) {
        set(47, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_created_at</code>.
     */
    public LocalDateTime getPaymentCreatedAt() {
        return (LocalDateTime) get(47);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_context</code>.
     */
    public void setPaymentContext(byte... value) {
        set(48, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_context</code>.
     */
    public byte[] getPaymentContext() {
        return (byte[]) get(48);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_adjustment_id</code>.
     */
    public void setPaymentAdjustmentId(String value) {
        set(49, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_adjustment_id</code>.
     */
    public String getPaymentAdjustmentId() {
        return (String) get(49);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_adjustment_status</code>.
     */
    public void setPaymentAdjustmentStatus(AdjustmentStatus value) {
        set(50, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_adjustment_status</code>.
     */
    public AdjustmentStatus getPaymentAdjustmentStatus() {
        return (AdjustmentStatus) get(50);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_adjustment_status_created_at</code>.
     */
    public void setPaymentAdjustmentStatusCreatedAt(LocalDateTime value) {
        set(51, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_adjustment_status_created_at</code>.
     */
    public LocalDateTime getPaymentAdjustmentStatusCreatedAt() {
        return (LocalDateTime) get(51);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_adjustment_created_at</code>.
     */
    public void setPaymentAdjustmentCreatedAt(LocalDateTime value) {
        set(52, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_adjustment_created_at</code>.
     */
    public LocalDateTime getPaymentAdjustmentCreatedAt() {
        return (LocalDateTime) get(52);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_adjustment_reason</code>.
     */
    public void setPaymentAdjustmentReason(String value) {
        set(53, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_adjustment_reason</code>.
     */
    public String getPaymentAdjustmentReason() {
        return (String) get(53);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_adjustment_fee</code>.
     */
    public void setPaymentAdjustmentFee(Long value) {
        set(54, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_adjustment_fee</code>.
     */
    public Long getPaymentAdjustmentFee() {
        return (Long) get(54);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_adjustment_provider_fee</code>.
     */
    public void setPaymentAdjustmentProviderFee(Long value) {
        set(55, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_adjustment_provider_fee</code>.
     */
    public Long getPaymentAdjustmentProviderFee() {
        return (Long) get(55);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_adjustment_external_fee</code>.
     */
    public void setPaymentAdjustmentExternalFee(Long value) {
        set(56, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_adjustment_external_fee</code>.
     */
    public Long getPaymentAdjustmentExternalFee() {
        return (Long) get(56);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_failure_class</code>.
     */
    public void setPaymentFailureClass(String value) {
        set(57, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_failure_class</code>.
     */
    public String getPaymentFailureClass() {
        return (String) get(57);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.invoice_template_id</code>.
     */
    public void setInvoiceTemplateId(String value) {
        set(58, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.invoice_template_id</code>.
     */
    public String getInvoiceTemplateId() {
        return (String) get(58);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.invoice_cart</code>.
     */
    public void setInvoiceCart(String value) {
        set(59, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.invoice_cart</code>.
     */
    public String getInvoiceCart() {
        return (String) get(59);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_flow</code>.
     */
    public void setPaymentFlow(String value) {
        set(60, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_flow</code>.
     */
    public String getPaymentFlow() {
        return (String) get(60);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_hold_on_expiration</code>.
     */
    public void setPaymentHoldOnExpiration(String value) {
        set(61, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_hold_on_expiration</code>.
     */
    public String getPaymentHoldOnExpiration() {
        return (String) get(61);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_hold_until</code>.
     */
    public void setPaymentHoldUntil(LocalDateTime value) {
        set(62, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_hold_until</code>.
     */
    public LocalDateTime getPaymentHoldUntil() {
        return (LocalDateTime) get(62);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_terminal_provider</code>.
     */
    public void setPaymentTerminalProvider(String value) {
        set(63, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_terminal_provider</code>.
     */
    public String getPaymentTerminalProvider() {
        return (String) get(63);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_refund_id</code>.
     */
    public void setPaymentRefundId(String value) {
        set(64, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_refund_id</code>.
     */
    public String getPaymentRefundId() {
        return (String) get(64);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_refund_status</code>.
     */
    public void setPaymentRefundStatus(InvoicePaymentRefundStatus value) {
        set(65, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_refund_status</code>.
     */
    public InvoicePaymentRefundStatus getPaymentRefundStatus() {
        return (InvoicePaymentRefundStatus) get(65);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_refund_created_at</code>.
     */
    public void setPaymentRefundCreatedAt(LocalDateTime value) {
        set(66, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_refund_created_at</code>.
     */
    public LocalDateTime getPaymentRefundCreatedAt() {
        return (LocalDateTime) get(66);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_refund_reason</code>.
     */
    public void setPaymentRefundReason(String value) {
        set(67, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_refund_reason</code>.
     */
    public String getPaymentRefundReason() {
        return (String) get(67);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_refund_fee</code>.
     */
    public void setPaymentRefundFee(Long value) {
        set(68, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_refund_fee</code>.
     */
    public Long getPaymentRefundFee() {
        return (Long) get(68);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_refund_provider_fee</code>.
     */
    public void setPaymentRefundProviderFee(Long value) {
        set(69, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_refund_provider_fee</code>.
     */
    public Long getPaymentRefundProviderFee() {
        return (Long) get(69);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_refund_external_fee</code>.
     */
    public void setPaymentRefundExternalFee(Long value) {
        set(70, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_refund_external_fee</code>.
     */
    public Long getPaymentRefundExternalFee() {
        return (Long) get(70);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_customer_id</code>.
     */
    public void setPaymentCustomerId(String value) {
        set(71, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_customer_id</code>.
     */
    public String getPaymentCustomerId() {
        return (String) get(71);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_provider_id</code>.
     */
    public void setPaymentProviderId(Integer value) {
        set(72, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_provider_id</code>.
     */
    public Integer getPaymentProviderId() {
        return (Integer) get(72);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_terminal_id</code>.
     */
    public void setPaymentTerminalId(Integer value) {
        set(73, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_terminal_id</code>.
     */
    public Integer getPaymentTerminalId() {
        return (Integer) get(73);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_domain_revision</code>.
     */
    public void setPaymentDomainRevision(Long value) {
        set(74, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_domain_revision</code>.
     */
    public Long getPaymentDomainRevision() {
        return (Long) get(74);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.invoice_context_type</code>.
     */
    public void setInvoiceContextType(String value) {
        set(75, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.invoice_context_type</code>.
     */
    public String getInvoiceContextType() {
        return (String) get(75);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_context_type</code>.
     */
    public void setPaymentContextType(String value) {
        set(76, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_context_type</code>.
     */
    public String getPaymentContextType() {
        return (String) get(76);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_adjustment_amount</code>.
     */
    public void setPaymentAdjustmentAmount(Long value) {
        set(77, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_adjustment_amount</code>.
     */
    public Long getPaymentAdjustmentAmount() {
        return (Long) get(77);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.invoice_party_revision</code>.
     */
    public void setInvoicePartyRevision(Long value) {
        set(78, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.invoice_party_revision</code>.
     */
    public Long getInvoicePartyRevision() {
        return (Long) get(78);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_party_revision</code>.
     */
    public void setPaymentPartyRevision(Long value) {
        set(79, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_party_revision</code>.
     */
    public Long getPaymentPartyRevision() {
        return (Long) get(79);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_digital_wallet_id</code>.
     */
    public void setPaymentDigitalWalletId(String value) {
        set(80, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_digital_wallet_id</code>.
     */
    public String getPaymentDigitalWalletId() {
        return (String) get(80);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_digital_wallet_provider</code>.
     */
    public void setPaymentDigitalWalletProvider(String value) {
        set(81, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_digital_wallet_provider</code>.
     */
    public String getPaymentDigitalWalletProvider() {
        return (String) get(81);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_refund_amount</code>.
     */
    public void setPaymentRefundAmount(Long value) {
        set(82, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_refund_amount</code>.
     */
    public Long getPaymentRefundAmount() {
        return (Long) get(82);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_refund_currency_code</code>.
     */
    public void setPaymentRefundCurrencyCode(String value) {
        set(83, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_refund_currency_code</code>.
     */
    public String getPaymentRefundCurrencyCode() {
        return (String) get(83);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_bank_card_token_provider</code>.
     */
    public void setPaymentBankCardTokenProvider(BankCardTokenProvider value) {
        set(84, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_bank_card_token_provider</code>.
     */
    public BankCardTokenProvider getPaymentBankCardTokenProvider() {
        return (BankCardTokenProvider) get(84);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_short_id</code>.
     */
    public void setPaymentShortId(String value) {
        set(85, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_short_id</code>.
     */
    public String getPaymentShortId() {
        return (String) get(85);
    }

    /**
     * Setter for <code>mst.invoice_event_stat.payment_institution_id</code>.
     */
    public void setPaymentInstitutionId(Integer value) {
        set(86, value);
    }

    /**
     * Getter for <code>mst.invoice_event_stat.payment_institution_id</code>.
     */
    public Integer getPaymentInstitutionId() {
        return (Integer) get(86);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached InvoiceEventStatRecord
     */
    public InvoiceEventStatRecord() {
        super(InvoiceEventStat.INVOICE_EVENT_STAT);
    }

    /**
     * Create a detached, initialised InvoiceEventStatRecord
     */
    public InvoiceEventStatRecord(Long id, Long eventId, InvoiceEventCategory eventCategory, InvoiceEventType eventType, LocalDateTime eventCreatedAt, String partyId, String partyEmail, String partyShopId, String partyShopName, String partyShopDescription, String partyShopUrl, Integer partyShopCategoryId, String partyShopPayoutToolId, String partyContractId, String partyContractRegisteredNumber, String partyContractInn, String invoiceId, InvoiceStatus invoiceStatus, String invoiceStatusDetails, String invoiceProduct, String invoiceDescription, Long invoiceAmount, String invoiceCurrencyCode, LocalDateTime invoiceDue, LocalDateTime invoiceCreatedAt, byte[] invoiceContext, String paymentId, InvoicePaymentStatus paymentStatus, String paymentExternalFailureCode, String paymentExternalFailureDescription, Long paymentAmount, String paymentCurrencyCode, Long paymentFee, Long paymentProviderFee, Long paymentExternalFee, String paymentTool, String paymentMaskedPan, String paymentBin, String paymentToken, String paymentSystem, String paymentSessionId, Integer paymentCountryId, Integer paymentCityId, String paymentIp, String paymentPhoneNumber, String paymentEmail, String paymentFingerprint, LocalDateTime paymentCreatedAt, byte[] paymentContext, String paymentAdjustmentId, AdjustmentStatus paymentAdjustmentStatus, LocalDateTime paymentAdjustmentStatusCreatedAt, LocalDateTime paymentAdjustmentCreatedAt, String paymentAdjustmentReason, Long paymentAdjustmentFee, Long paymentAdjustmentProviderFee, Long paymentAdjustmentExternalFee, String paymentFailureClass, String invoiceTemplateId, String invoiceCart, String paymentFlow, String paymentHoldOnExpiration, LocalDateTime paymentHoldUntil, String paymentTerminalProvider, String paymentRefundId, InvoicePaymentRefundStatus paymentRefundStatus, LocalDateTime paymentRefundCreatedAt, String paymentRefundReason, Long paymentRefundFee, Long paymentRefundProviderFee, Long paymentRefundExternalFee, String paymentCustomerId, Integer paymentProviderId, Integer paymentTerminalId, Long paymentDomainRevision, String invoiceContextType, String paymentContextType, Long paymentAdjustmentAmount, Long invoicePartyRevision, Long paymentPartyRevision, String paymentDigitalWalletId, String paymentDigitalWalletProvider, Long paymentRefundAmount, String paymentRefundCurrencyCode, BankCardTokenProvider paymentBankCardTokenProvider, String paymentShortId, Integer paymentInstitutionId) {
        super(InvoiceEventStat.INVOICE_EVENT_STAT);

        set(0, id);
        set(1, eventId);
        set(2, eventCategory);
        set(3, eventType);
        set(4, eventCreatedAt);
        set(5, partyId);
        set(6, partyEmail);
        set(7, partyShopId);
        set(8, partyShopName);
        set(9, partyShopDescription);
        set(10, partyShopUrl);
        set(11, partyShopCategoryId);
        set(12, partyShopPayoutToolId);
        set(13, partyContractId);
        set(14, partyContractRegisteredNumber);
        set(15, partyContractInn);
        set(16, invoiceId);
        set(17, invoiceStatus);
        set(18, invoiceStatusDetails);
        set(19, invoiceProduct);
        set(20, invoiceDescription);
        set(21, invoiceAmount);
        set(22, invoiceCurrencyCode);
        set(23, invoiceDue);
        set(24, invoiceCreatedAt);
        set(25, invoiceContext);
        set(26, paymentId);
        set(27, paymentStatus);
        set(28, paymentExternalFailureCode);
        set(29, paymentExternalFailureDescription);
        set(30, paymentAmount);
        set(31, paymentCurrencyCode);
        set(32, paymentFee);
        set(33, paymentProviderFee);
        set(34, paymentExternalFee);
        set(35, paymentTool);
        set(36, paymentMaskedPan);
        set(37, paymentBin);
        set(38, paymentToken);
        set(39, paymentSystem);
        set(40, paymentSessionId);
        set(41, paymentCountryId);
        set(42, paymentCityId);
        set(43, paymentIp);
        set(44, paymentPhoneNumber);
        set(45, paymentEmail);
        set(46, paymentFingerprint);
        set(47, paymentCreatedAt);
        set(48, paymentContext);
        set(49, paymentAdjustmentId);
        set(50, paymentAdjustmentStatus);
        set(51, paymentAdjustmentStatusCreatedAt);
        set(52, paymentAdjustmentCreatedAt);
        set(53, paymentAdjustmentReason);
        set(54, paymentAdjustmentFee);
        set(55, paymentAdjustmentProviderFee);
        set(56, paymentAdjustmentExternalFee);
        set(57, paymentFailureClass);
        set(58, invoiceTemplateId);
        set(59, invoiceCart);
        set(60, paymentFlow);
        set(61, paymentHoldOnExpiration);
        set(62, paymentHoldUntil);
        set(63, paymentTerminalProvider);
        set(64, paymentRefundId);
        set(65, paymentRefundStatus);
        set(66, paymentRefundCreatedAt);
        set(67, paymentRefundReason);
        set(68, paymentRefundFee);
        set(69, paymentRefundProviderFee);
        set(70, paymentRefundExternalFee);
        set(71, paymentCustomerId);
        set(72, paymentProviderId);
        set(73, paymentTerminalId);
        set(74, paymentDomainRevision);
        set(75, invoiceContextType);
        set(76, paymentContextType);
        set(77, paymentAdjustmentAmount);
        set(78, invoicePartyRevision);
        set(79, paymentPartyRevision);
        set(80, paymentDigitalWalletId);
        set(81, paymentDigitalWalletProvider);
        set(82, paymentRefundAmount);
        set(83, paymentRefundCurrencyCode);
        set(84, paymentBankCardTokenProvider);
        set(85, paymentShortId);
        set(86, paymentInstitutionId);
    }
}
