/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.magista.domain.tables;


import com.rbkmoney.magista.domain.Keys;
import com.rbkmoney.magista.domain.Mst;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.InvoiceStatus;
import com.rbkmoney.magista.domain.tables.records.InvoiceEventRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


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
public class InvoiceEvent extends TableImpl<InvoiceEventRecord> {

    private static final long serialVersionUID = 1081101796;

    /**
     * The reference instance of <code>mst.invoice_event</code>
     */
    public static final InvoiceEvent INVOICE_EVENT = new InvoiceEvent();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<InvoiceEventRecord> getRecordType() {
        return InvoiceEventRecord.class;
    }

    /**
     * The column <code>mst.invoice_event.id</code>.
     */
    public final TableField<InvoiceEventRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('mst.invoice_event_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>mst.invoice_event.event_id</code>.
     */
    public final TableField<InvoiceEventRecord, Long> EVENT_ID = createField("event_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>mst.invoice_event.event_created_at</code>.
     */
    public final TableField<InvoiceEventRecord, LocalDateTime> EVENT_CREATED_AT = createField("event_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>mst.invoice_event.event_type</code>.
     */
    public final TableField<InvoiceEventRecord, InvoiceEventType> EVENT_TYPE = createField("event_type", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.InvoiceEventType.class), this, "");

    /**
     * The column <code>mst.invoice_event.invoice_id</code>.
     */
    public final TableField<InvoiceEventRecord, String> INVOICE_ID = createField("invoice_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.invoice_event.invoice_status</code>.
     */
    public final TableField<InvoiceEventRecord, InvoiceStatus> INVOICE_STATUS = createField("invoice_status", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.InvoiceStatus.class), this, "");

    /**
     * The column <code>mst.invoice_event.invoice_status_details</code>.
     */
    public final TableField<InvoiceEventRecord, String> INVOICE_STATUS_DETAILS = createField("invoice_status_details", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * Create a <code>mst.invoice_event</code> table reference
     */
    public InvoiceEvent() {
        this("invoice_event", null);
    }

    /**
     * Create an aliased <code>mst.invoice_event</code> table reference
     */
    public InvoiceEvent(String alias) {
        this(alias, INVOICE_EVENT);
    }

    private InvoiceEvent(String alias, Table<InvoiceEventRecord> aliased) {
        this(alias, aliased, null);
    }

    private InvoiceEvent(String alias, Table<InvoiceEventRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
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
    public Identity<InvoiceEventRecord, Long> getIdentity() {
        return Keys.IDENTITY_INVOICE_EVENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<InvoiceEventRecord> getPrimaryKey() {
        return Keys.INVOICE_EVENT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<InvoiceEventRecord>> getKeys() {
        return Arrays.<UniqueKey<InvoiceEventRecord>>asList(Keys.INVOICE_EVENT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<InvoiceEventRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<InvoiceEventRecord, ?>>asList(Keys.INVOICE_EVENT__INVOICE_EVENT_INVOICE_ID_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceEvent as(String alias) {
        return new InvoiceEvent(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public InvoiceEvent rename(String name) {
        return new InvoiceEvent(name, null);
    }
}
