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
public enum PaymentTool implements EnumType {

    bank_card("bank_card"),

    payment_terminal("payment_terminal"),

    digital_wallet("digital_wallet");

    private final String literal;

    private PaymentTool(String literal) {
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
        return "payment_tool";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLiteral() {
        return literal;
    }
}
