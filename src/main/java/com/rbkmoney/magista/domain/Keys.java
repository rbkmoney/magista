/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.magista.domain;


import com.rbkmoney.magista.domain.tables.InvoiceEventStat;
import com.rbkmoney.magista.domain.tables.PayoutEventStat;
import com.rbkmoney.magista.domain.tables.records.InvoiceEventStatRecord;
import com.rbkmoney.magista.domain.tables.records.PayoutEventStatRecord;

import javax.annotation.Generated;

import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;


/**
 * A class modelling foreign key relationships between tables of the <code>mst</code> 
 * schema
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<InvoiceEventStatRecord, Long> IDENTITY_INVOICE_EVENT_STAT = Identities0.IDENTITY_INVOICE_EVENT_STAT;
    public static final Identity<PayoutEventStatRecord, Long> IDENTITY_PAYOUT_EVENT_STAT = Identities0.IDENTITY_PAYOUT_EVENT_STAT;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<InvoiceEventStatRecord> INVOICE_EVENT_PKEY = UniqueKeys0.INVOICE_EVENT_PKEY;
    public static final UniqueKey<PayoutEventStatRecord> PAYMENT_EVENT_PKEY = UniqueKeys0.PAYMENT_EVENT_PKEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 extends AbstractKeys {
        public static Identity<InvoiceEventStatRecord, Long> IDENTITY_INVOICE_EVENT_STAT = createIdentity(InvoiceEventStat.INVOICE_EVENT_STAT, InvoiceEventStat.INVOICE_EVENT_STAT.ID);
        public static Identity<PayoutEventStatRecord, Long> IDENTITY_PAYOUT_EVENT_STAT = createIdentity(PayoutEventStat.PAYOUT_EVENT_STAT, PayoutEventStat.PAYOUT_EVENT_STAT.ID);
    }

    private static class UniqueKeys0 extends AbstractKeys {
        public static final UniqueKey<InvoiceEventStatRecord> INVOICE_EVENT_PKEY = createUniqueKey(InvoiceEventStat.INVOICE_EVENT_STAT, "invoice_event_pkey", InvoiceEventStat.INVOICE_EVENT_STAT.ID);
        public static final UniqueKey<PayoutEventStatRecord> PAYMENT_EVENT_PKEY = createUniqueKey(PayoutEventStat.PAYOUT_EVENT_STAT, "payment_event_pkey", PayoutEventStat.PAYOUT_EVENT_STAT.ID);
    }
}
