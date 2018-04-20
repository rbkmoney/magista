/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.magista.domain.tables;


import com.rbkmoney.magista.domain.Keys;
import com.rbkmoney.magista.domain.Mst;
import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.RefundStatus;
import com.rbkmoney.magista.domain.tables.records.RefundRecord;

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
public class Refund extends TableImpl<RefundRecord> {

    private static final long serialVersionUID = 1553281586;

    /**
     * The reference instance of <code>mst.refund</code>
     */
    public static final Refund REFUND = new Refund();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RefundRecord> getRecordType() {
        return RefundRecord.class;
    }

    /**
     * The column <code>mst.refund.id</code>.
     */
    public final TableField<RefundRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('mst.refund_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>mst.refund.event_id</code>.
     */
    public final TableField<RefundRecord, Long> EVENT_ID = createField("event_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>mst.refund.event_created_at</code>.
     */
    public final TableField<RefundRecord, LocalDateTime> EVENT_CREATED_AT = createField("event_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>mst.refund.event_type</code>.
     */
    public final TableField<RefundRecord, InvoiceEventType> EVENT_TYPE = createField("event_type", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.InvoiceEventType.class), this, "");

    /**
     * The column <code>mst.refund.invoice_id</code>.
     */
    public final TableField<RefundRecord, String> INVOICE_ID = createField("invoice_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.refund.payment_id</code>.
     */
    public final TableField<RefundRecord, String> PAYMENT_ID = createField("payment_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.refund.refund_id</code>.
     */
    public final TableField<RefundRecord, String> REFUND_ID = createField("refund_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.refund.party_id</code>.
     */
    public final TableField<RefundRecord, String> PARTY_ID = createField("party_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.refund.party_shop_id</code>.
     */
    public final TableField<RefundRecord, String> PARTY_SHOP_ID = createField("party_shop_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.refund.party_contract_id</code>.
     */
    public final TableField<RefundRecord, String> PARTY_CONTRACT_ID = createField("party_contract_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.refund.refund_status</code>.
     */
    public final TableField<RefundRecord, RefundStatus> REFUND_STATUS = createField("refund_status", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.RefundStatus.class), this, "");

    /**
     * The column <code>mst.refund.refund_operation_failure_class</code>.
     */
    public final TableField<RefundRecord, FailureClass> REFUND_OPERATION_FAILURE_CLASS = createField("refund_operation_failure_class", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.FailureClass.class), this, "");

    /**
     * The column <code>mst.refund.refund_external_failure</code>.
     */
    public final TableField<RefundRecord, String> REFUND_EXTERNAL_FAILURE = createField("refund_external_failure", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.refund.refund_external_failure_reason</code>.
     */
    public final TableField<RefundRecord, String> REFUND_EXTERNAL_FAILURE_REASON = createField("refund_external_failure_reason", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.refund.refund_created_at</code>.
     */
    public final TableField<RefundRecord, LocalDateTime> REFUND_CREATED_AT = createField("refund_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>mst.refund.refund_reason</code>.
     */
    public final TableField<RefundRecord, String> REFUND_REASON = createField("refund_reason", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.refund.refund_currency_code</code>.
     */
    public final TableField<RefundRecord, String> REFUND_CURRENCY_CODE = createField("refund_currency_code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.refund.refund_amount</code>.
     */
    public final TableField<RefundRecord, Long> REFUND_AMOUNT = createField("refund_amount", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>mst.refund.refund_fee</code>.
     */
    public final TableField<RefundRecord, Long> REFUND_FEE = createField("refund_fee", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>mst.refund.refund_provider_fee</code>.
     */
    public final TableField<RefundRecord, Long> REFUND_PROVIDER_FEE = createField("refund_provider_fee", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>mst.refund.refund_external_fee</code>.
     */
    public final TableField<RefundRecord, Long> REFUND_EXTERNAL_FEE = createField("refund_external_fee", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>mst.refund.refund_domain_revision</code>.
     */
    public final TableField<RefundRecord, Long> REFUND_DOMAIN_REVISION = createField("refund_domain_revision", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>mst.refund</code> table reference
     */
    public Refund() {
        this("refund", null);
    }

    /**
     * Create an aliased <code>mst.refund</code> table reference
     */
    public Refund(String alias) {
        this(alias, REFUND);
    }

    private Refund(String alias, Table<RefundRecord> aliased) {
        this(alias, aliased, null);
    }

    private Refund(String alias, Table<RefundRecord> aliased, Field<?>[] parameters) {
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
    public Identity<RefundRecord, Long> getIdentity() {
        return Keys.IDENTITY_REFUND;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<RefundRecord> getPrimaryKey() {
        return Keys.REFUND_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<RefundRecord>> getKeys() {
        return Arrays.<UniqueKey<RefundRecord>>asList(Keys.REFUND_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Refund as(String alias) {
        return new Refund(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Refund rename(String name) {
        return new Refund(name, null);
    }
}
