/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.magista.domain;


import com.rbkmoney.magista.domain.tables.InvoiceEventStat;
import com.rbkmoney.magista.domain.tables.PayoutEventStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


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
public class Mst extends SchemaImpl {

    private static final long serialVersionUID = 1871334162;

    /**
     * The reference instance of <code>mst</code>
     */
    public static final Mst MST = new Mst();

    /**
     * The table <code>mst.invoice_event_stat</code>.
     */
    public final InvoiceEventStat INVOICE_EVENT_STAT = com.rbkmoney.magista.domain.tables.InvoiceEventStat.INVOICE_EVENT_STAT;

    /**
     * The table <code>mst.payout_event_stat</code>.
     */
    public final PayoutEventStat PAYOUT_EVENT_STAT = com.rbkmoney.magista.domain.tables.PayoutEventStat.PAYOUT_EVENT_STAT;

    /**
     * No further instances allowed
     */
    private Mst() {
        super("mst", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        List result = new ArrayList();
        result.addAll(getSequences0());
        return result;
    }

    private final List<Sequence<?>> getSequences0() {
        return Arrays.<Sequence<?>>asList(
            Sequences.INVOICE_EVENT_STAT_ID_SEQ,
            Sequences.PAYOUT_EVENT_STAT_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            InvoiceEventStat.INVOICE_EVENT_STAT,
            PayoutEventStat.PAYOUT_EVENT_STAT);
    }
}
