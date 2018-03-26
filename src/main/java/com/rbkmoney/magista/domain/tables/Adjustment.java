/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.magista.domain.tables;


import com.rbkmoney.magista.domain.Keys;
import com.rbkmoney.magista.domain.Mst;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.records.AdjustmentRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
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
public class Adjustment extends TableImpl<AdjustmentRecord> {

    private static final long serialVersionUID = 2096129354;

    /**
     * The reference instance of <code>mst.adjustment</code>
     */
    public static final Adjustment ADJUSTMENT = new Adjustment();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AdjustmentRecord> getRecordType() {
        return AdjustmentRecord.class;
    }

    /**
     * The column <code>mst.adjustment.id</code>.
     */
    public final TableField<AdjustmentRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('mst.adjustment_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>mst.adjustment.event_id</code>.
     */
    public final TableField<AdjustmentRecord, Long> EVENT_ID = createField("event_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>mst.adjustment.event_created_at</code>.
     */
    public final TableField<AdjustmentRecord, LocalDateTime> EVENT_CREATED_AT = createField("event_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>mst.adjustment.event_type</code>.
     */
    public final TableField<AdjustmentRecord, InvoiceEventType> EVENT_TYPE = createField("event_type", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.InvoiceEventType.class), this, "");

    /**
     * The column <code>mst.adjustment.invoice_id</code>.
     */
    public final TableField<AdjustmentRecord, String> INVOICE_ID = createField("invoice_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.adjustment.payment_id</code>.
     */
    public final TableField<AdjustmentRecord, String> PAYMENT_ID = createField("payment_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.adjustment.adjustment_id</code>.
     */
    public final TableField<AdjustmentRecord, String> ADJUSTMENT_ID = createField("adjustment_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.adjustment.party_id</code>.
     */
    public final TableField<AdjustmentRecord, String> PARTY_ID = createField("party_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.adjustment.party_shop_id</code>.
     */
    public final TableField<AdjustmentRecord, String> PARTY_SHOP_ID = createField("party_shop_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.adjustment.adjustment_status</code>.
     */
    public final TableField<AdjustmentRecord, AdjustmentStatus> ADJUSTMENT_STATUS = createField("adjustment_status", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.AdjustmentStatus.class), this, "");

    /**
     * The column <code>mst.adjustment.adjustment_status_created_at</code>.
     */
    public final TableField<AdjustmentRecord, LocalDateTime> ADJUSTMENT_STATUS_CREATED_AT = createField("adjustment_status_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>mst.adjustment.adjustment_created_at</code>.
     */
    public final TableField<AdjustmentRecord, LocalDateTime> ADJUSTMENT_CREATED_AT = createField("adjustment_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>mst.adjustment.adjustment_reason</code>.
     */
    public final TableField<AdjustmentRecord, String> ADJUSTMENT_REASON = createField("adjustment_reason", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.adjustment.adjustment_fee</code>.
     */
    public final TableField<AdjustmentRecord, Long> ADJUSTMENT_FEE = createField("adjustment_fee", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>mst.adjustment.adjustment_provider_fee</code>.
     */
    public final TableField<AdjustmentRecord, Long> ADJUSTMENT_PROVIDER_FEE = createField("adjustment_provider_fee", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>mst.adjustment.adjustment_external_fee</code>.
     */
    public final TableField<AdjustmentRecord, Long> ADJUSTMENT_EXTERNAL_FEE = createField("adjustment_external_fee", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * Create a <code>mst.adjustment</code> table reference
     */
    public Adjustment() {
        this("adjustment", null);
    }

    /**
     * Create an aliased <code>mst.adjustment</code> table reference
     */
    public Adjustment(String alias) {
        this(alias, ADJUSTMENT);
    }

    private Adjustment(String alias, Table<AdjustmentRecord> aliased) {
        this(alias, aliased, null);
    }

    private Adjustment(String alias, Table<AdjustmentRecord> aliased, Field<?>[] parameters) {
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
    public Identity<AdjustmentRecord, Long> getIdentity() {
        return Keys.IDENTITY_ADJUSTMENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<AdjustmentRecord> getPrimaryKey() {
        return Keys.ADJUSTMENT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<AdjustmentRecord>> getKeys() {
        return Arrays.<UniqueKey<AdjustmentRecord>>asList(Keys.ADJUSTMENT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Adjustment as(String alias) {
        return new Adjustment(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Adjustment rename(String name) {
        return new Adjustment(name, null);
    }
}
