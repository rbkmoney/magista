/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.magista.domain.enums;


import com.rbkmoney.magista.domain.Mst;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.EnumType;
import org.jooq.Schema;


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
public enum InvoiceEventType implements EnumType {

    INVOICE_CREATED("INVOICE_CREATED"),

    INVOICE_STATUS_CHANGED("INVOICE_STATUS_CHANGED"),

    INVOICE_PAYMENT_STARTED("INVOICE_PAYMENT_STARTED"),

    INVOICE_PAYMENT_STATUS_CHANGED("INVOICE_PAYMENT_STATUS_CHANGED"),

    INVOICE_PAYMENT_ADJUSTMENT_CREATED("INVOICE_PAYMENT_ADJUSTMENT_CREATED"),

    INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED("INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED"),

    INVOICE_PAYMENT_REFUND_CREATED("INVOICE_PAYMENT_REFUND_CREATED"),

    INVOICE_PAYMENT_REFUND_STATUS_CHANGED("INVOICE_PAYMENT_REFUND_STATUS_CHANGED"),

    INVOICE_PAYMENT_ADJUSTED("INVOICE_PAYMENT_ADJUSTED"),

    PAYMENT_TERMINAL_RECIEPT("PAYMENT_TERMINAL_RECIEPT");

    private final String literal;

    private InvoiceEventType(String literal) {
        this.literal = literal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return getSchema() == null ? null : getSchema().getCatalog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Mst.MST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "invoice_event_type";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLiteral() {
        return literal;
    }
}
