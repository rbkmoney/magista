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
public enum PayoutStatus implements EnumType {

    unpaid("unpaid"),

    paid("paid"),

    cancelled("cancelled"),

    confirmed("confirmed");

    private final String literal;

    private PayoutStatus(String literal) {
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
        return "payout_status";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLiteral() {
        return literal;
    }
}
