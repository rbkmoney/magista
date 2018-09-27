/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.magista.domain.tables.records;


import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.RefundStatus;
import com.rbkmoney.magista.domain.tables.Refund;

import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record21;
import org.jooq.Row21;
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
public class RefundRecord extends UpdatableRecordImpl<RefundRecord> implements Record21<Long, Long, LocalDateTime, InvoiceEventType, String, String, String, String, String, RefundStatus, FailureClass, String, String, LocalDateTime, String, String, Long, Long, Long, Long, Long> {

    private static final long serialVersionUID = -2033538217;

    /**
     * Setter for <code>mst.refund.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>mst.refund.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>mst.refund.event_id</code>.
     */
    public void setEventId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>mst.refund.event_id</code>.
     */
    public Long getEventId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>mst.refund.event_created_at</code>.
     */
    public void setEventCreatedAt(LocalDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>mst.refund.event_created_at</code>.
     */
    public LocalDateTime getEventCreatedAt() {
        return (LocalDateTime) get(2);
    }

    /**
     * Setter for <code>mst.refund.event_type</code>.
     */
    public void setEventType(InvoiceEventType value) {
        set(3, value);
    }

    /**
     * Getter for <code>mst.refund.event_type</code>.
     */
    public InvoiceEventType getEventType() {
        return (InvoiceEventType) get(3);
    }

    /**
     * Setter for <code>mst.refund.invoice_id</code>.
     */
    public void setInvoiceId(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>mst.refund.invoice_id</code>.
     */
    public String getInvoiceId() {
        return (String) get(4);
    }

    /**
     * Setter for <code>mst.refund.payment_id</code>.
     */
    public void setPaymentId(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>mst.refund.payment_id</code>.
     */
    public String getPaymentId() {
        return (String) get(5);
    }

    /**
     * Setter for <code>mst.refund.refund_id</code>.
     */
    public void setRefundId(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>mst.refund.refund_id</code>.
     */
    public String getRefundId() {
        return (String) get(6);
    }

    /**
     * Setter for <code>mst.refund.party_id</code>.
     */
    public void setPartyId(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>mst.refund.party_id</code>.
     */
    public String getPartyId() {
        return (String) get(7);
    }

    /**
     * Setter for <code>mst.refund.party_shop_id</code>.
     */
    public void setPartyShopId(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>mst.refund.party_shop_id</code>.
     */
    public String getPartyShopId() {
        return (String) get(8);
    }

    /**
     * Setter for <code>mst.refund.refund_status</code>.
     */
    public void setRefundStatus(RefundStatus value) {
        set(9, value);
    }

    /**
     * Getter for <code>mst.refund.refund_status</code>.
     */
    public RefundStatus getRefundStatus() {
        return (RefundStatus) get(9);
    }

    /**
     * Setter for <code>mst.refund.refund_operation_failure_class</code>.
     */
    public void setRefundOperationFailureClass(FailureClass value) {
        set(10, value);
    }

    /**
     * Getter for <code>mst.refund.refund_operation_failure_class</code>.
     */
    public FailureClass getRefundOperationFailureClass() {
        return (FailureClass) get(10);
    }

    /**
     * Setter for <code>mst.refund.refund_external_failure</code>.
     */
    public void setRefundExternalFailure(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>mst.refund.refund_external_failure</code>.
     */
    public String getRefundExternalFailure() {
        return (String) get(11);
    }

    /**
     * Setter for <code>mst.refund.refund_external_failure_reason</code>.
     */
    public void setRefundExternalFailureReason(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>mst.refund.refund_external_failure_reason</code>.
     */
    public String getRefundExternalFailureReason() {
        return (String) get(12);
    }

    /**
     * Setter for <code>mst.refund.refund_created_at</code>.
     */
    public void setRefundCreatedAt(LocalDateTime value) {
        set(13, value);
    }

    /**
     * Getter for <code>mst.refund.refund_created_at</code>.
     */
    public LocalDateTime getRefundCreatedAt() {
        return (LocalDateTime) get(13);
    }

    /**
     * Setter for <code>mst.refund.refund_reason</code>.
     */
    public void setRefundReason(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>mst.refund.refund_reason</code>.
     */
    public String getRefundReason() {
        return (String) get(14);
    }

    /**
     * Setter for <code>mst.refund.refund_currency_code</code>.
     */
    public void setRefundCurrencyCode(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>mst.refund.refund_currency_code</code>.
     */
    public String getRefundCurrencyCode() {
        return (String) get(15);
    }

    /**
     * Setter for <code>mst.refund.refund_amount</code>.
     */
    public void setRefundAmount(Long value) {
        set(16, value);
    }

    /**
     * Getter for <code>mst.refund.refund_amount</code>.
     */
    public Long getRefundAmount() {
        return (Long) get(16);
    }

    /**
     * Setter for <code>mst.refund.refund_fee</code>.
     */
    public void setRefundFee(Long value) {
        set(17, value);
    }

    /**
     * Getter for <code>mst.refund.refund_fee</code>.
     */
    public Long getRefundFee() {
        return (Long) get(17);
    }

    /**
     * Setter for <code>mst.refund.refund_provider_fee</code>.
     */
    public void setRefundProviderFee(Long value) {
        set(18, value);
    }

    /**
     * Getter for <code>mst.refund.refund_provider_fee</code>.
     */
    public Long getRefundProviderFee() {
        return (Long) get(18);
    }

    /**
     * Setter for <code>mst.refund.refund_external_fee</code>.
     */
    public void setRefundExternalFee(Long value) {
        set(19, value);
    }

    /**
     * Getter for <code>mst.refund.refund_external_fee</code>.
     */
    public Long getRefundExternalFee() {
        return (Long) get(19);
    }

    /**
     * Setter for <code>mst.refund.refund_domain_revision</code>.
     */
    public void setRefundDomainRevision(Long value) {
        set(20, value);
    }

    /**
     * Getter for <code>mst.refund.refund_domain_revision</code>.
     */
    public Long getRefundDomainRevision() {
        return (Long) get(20);
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
    // Record21 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row21<Long, Long, LocalDateTime, InvoiceEventType, String, String, String, String, String, RefundStatus, FailureClass, String, String, LocalDateTime, String, String, Long, Long, Long, Long, Long> fieldsRow() {
        return (Row21) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row21<Long, Long, LocalDateTime, InvoiceEventType, String, String, String, String, String, RefundStatus, FailureClass, String, String, LocalDateTime, String, String, Long, Long, Long, Long, Long> valuesRow() {
        return (Row21) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Refund.REFUND.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field2() {
        return Refund.REFUND.EVENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field3() {
        return Refund.REFUND.EVENT_CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<InvoiceEventType> field4() {
        return Refund.REFUND.EVENT_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Refund.REFUND.INVOICE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Refund.REFUND.PAYMENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Refund.REFUND.REFUND_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Refund.REFUND.PARTY_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Refund.REFUND.PARTY_SHOP_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<RefundStatus> field10() {
        return Refund.REFUND.REFUND_STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<FailureClass> field11() {
        return Refund.REFUND.REFUND_OPERATION_FAILURE_CLASS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return Refund.REFUND.REFUND_EXTERNAL_FAILURE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return Refund.REFUND.REFUND_EXTERNAL_FAILURE_REASON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field14() {
        return Refund.REFUND.REFUND_CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field15() {
        return Refund.REFUND.REFUND_REASON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field16() {
        return Refund.REFUND.REFUND_CURRENCY_CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field17() {
        return Refund.REFUND.REFUND_AMOUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field18() {
        return Refund.REFUND.REFUND_FEE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field19() {
        return Refund.REFUND.REFUND_PROVIDER_FEE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field20() {
        return Refund.REFUND.REFUND_EXTERNAL_FEE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field21() {
        return Refund.REFUND.REFUND_DOMAIN_REVISION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value2() {
        return getEventId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value3() {
        return getEventCreatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceEventType value4() {
        return getEventType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getInvoiceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getPaymentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getRefundId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getPartyId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getPartyShopId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundStatus value10() {
        return getRefundStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FailureClass value11() {
        return getRefundOperationFailureClass();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getRefundExternalFailure();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getRefundExternalFailureReason();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value14() {
        return getRefundCreatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value15() {
        return getRefundReason();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value16() {
        return getRefundCurrencyCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value17() {
        return getRefundAmount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value18() {
        return getRefundFee();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value19() {
        return getRefundProviderFee();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value20() {
        return getRefundExternalFee();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value21() {
        return getRefundDomainRevision();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value2(Long value) {
        setEventId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value3(LocalDateTime value) {
        setEventCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value4(InvoiceEventType value) {
        setEventType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value5(String value) {
        setInvoiceId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value6(String value) {
        setPaymentId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value7(String value) {
        setRefundId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value8(String value) {
        setPartyId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value9(String value) {
        setPartyShopId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value10(RefundStatus value) {
        setRefundStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value11(FailureClass value) {
        setRefundOperationFailureClass(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value12(String value) {
        setRefundExternalFailure(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value13(String value) {
        setRefundExternalFailureReason(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value14(LocalDateTime value) {
        setRefundCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value15(String value) {
        setRefundReason(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value16(String value) {
        setRefundCurrencyCode(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value17(Long value) {
        setRefundAmount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value18(Long value) {
        setRefundFee(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value19(Long value) {
        setRefundProviderFee(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value20(Long value) {
        setRefundExternalFee(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord value21(Long value) {
        setRefundDomainRevision(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefundRecord values(Long value1, Long value2, LocalDateTime value3, InvoiceEventType value4, String value5, String value6, String value7, String value8, String value9, RefundStatus value10, FailureClass value11, String value12, String value13, LocalDateTime value14, String value15, String value16, Long value17, Long value18, Long value19, Long value20, Long value21) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        value18(value18);
        value19(value19);
        value20(value20);
        value21(value21);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RefundRecord
     */
    public RefundRecord() {
        super(Refund.REFUND);
    }

    /**
     * Create a detached, initialised RefundRecord
     */
    public RefundRecord(Long id, Long eventId, LocalDateTime eventCreatedAt, InvoiceEventType eventType, String invoiceId, String paymentId, String refundId, String partyId, String partyShopId, RefundStatus refundStatus, FailureClass refundOperationFailureClass, String refundExternalFailure, String refundExternalFailureReason, LocalDateTime refundCreatedAt, String refundReason, String refundCurrencyCode, Long refundAmount, Long refundFee, Long refundProviderFee, Long refundExternalFee, Long refundDomainRevision) {
        super(Refund.REFUND);

        set(0, id);
        set(1, eventId);
        set(2, eventCreatedAt);
        set(3, eventType);
        set(4, invoiceId);
        set(5, paymentId);
        set(6, refundId);
        set(7, partyId);
        set(8, partyShopId);
        set(9, refundStatus);
        set(10, refundOperationFailureClass);
        set(11, refundExternalFailure);
        set(12, refundExternalFailureReason);
        set(13, refundCreatedAt);
        set(14, refundReason);
        set(15, refundCurrencyCode);
        set(16, refundAmount);
        set(17, refundFee);
        set(18, refundProviderFee);
        set(19, refundExternalFee);
        set(20, refundDomainRevision);
    }
}
