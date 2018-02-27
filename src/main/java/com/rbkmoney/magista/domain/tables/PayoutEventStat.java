/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.magista.domain.tables;


import com.rbkmoney.magista.domain.Keys;
import com.rbkmoney.magista.domain.Mst;
import com.rbkmoney.magista.domain.enums.PayoutEventCategory;
import com.rbkmoney.magista.domain.enums.PayoutEventType;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.PayoutType;
import com.rbkmoney.magista.domain.tables.records.PayoutEventStatRecord;

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
public class PayoutEventStat extends TableImpl<PayoutEventStatRecord> {

    private static final long serialVersionUID = -1422280596;

    /**
     * The reference instance of <code>mst.payout_event_stat</code>
     */
    public static final PayoutEventStat PAYOUT_EVENT_STAT = new PayoutEventStat();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PayoutEventStatRecord> getRecordType() {
        return PayoutEventStatRecord.class;
    }

    /**
     * The column <code>mst.payout_event_stat.id</code>.
     */
    public final TableField<PayoutEventStatRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('mst.payout_event_stat_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>mst.payout_event_stat.event_id</code>.
     */
    public final TableField<PayoutEventStatRecord, Long> EVENT_ID = createField("event_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>mst.payout_event_stat.event_category</code>.
     */
    public final TableField<PayoutEventStatRecord, PayoutEventCategory> EVENT_CATEGORY = createField("event_category", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.PayoutEventCategory.class), this, "");

    /**
     * The column <code>mst.payout_event_stat.event_type</code>.
     */
    public final TableField<PayoutEventStatRecord, PayoutEventType> EVENT_TYPE = createField("event_type", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.PayoutEventType.class), this, "");

    /**
     * The column <code>mst.payout_event_stat.event_created_at</code>.
     */
    public final TableField<PayoutEventStatRecord, LocalDateTime> EVENT_CREATED_AT = createField("event_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>mst.payout_event_stat.party_id</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PARTY_ID = createField("party_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.payout_event_stat.party_shop_id</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PARTY_SHOP_ID = createField("party_shop_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_id</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ID = createField("payout_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_created_at</code>.
     */
    public final TableField<PayoutEventStatRecord, LocalDateTime> PAYOUT_CREATED_AT = createField("payout_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_status</code>.
     */
    public final TableField<PayoutEventStatRecord, PayoutStatus> PAYOUT_STATUS = createField("payout_status", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.PayoutStatus.class), this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_amount</code>.
     */
    public final TableField<PayoutEventStatRecord, Long> PAYOUT_AMOUNT = createField("payout_amount", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_fee</code>.
     */
    public final TableField<PayoutEventStatRecord, Long> PAYOUT_FEE = createField("payout_fee", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_currency_code</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_CURRENCY_CODE = createField("payout_currency_code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_type</code>.
     */
    public final TableField<PayoutEventStatRecord, PayoutType> PAYOUT_TYPE = createField("payout_type", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.PayoutType.class), this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_card_token</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_CARD_TOKEN = createField("payout_card_token", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_card_masked_pan</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_CARD_MASKED_PAN = createField("payout_card_masked_pan", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_card_bin</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_CARD_BIN = createField("payout_card_bin", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_card_payment_system</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_CARD_PAYMENT_SYSTEM = createField("payout_card_payment_system", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_bank_id</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_BANK_ID = createField("payout_account_bank_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_bank_corr_id</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_BANK_CORR_ID = createField("payout_account_bank_corr_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_bank_local_code</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_BANK_LOCAL_CODE = createField("payout_account_bank_local_code", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_bank_name</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_BANK_NAME = createField("payout_account_bank_name", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_inn</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_INN = createField("payout_account_inn", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_legal_agreement_id</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_LEGAL_AGREEMENT_ID = createField("payout_account_legal_agreement_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_legal_agreement_signed_at</code>.
     */
    public final TableField<PayoutEventStatRecord, LocalDateTime> PAYOUT_ACCOUNT_LEGAL_AGREEMENT_SIGNED_AT = createField("payout_account_legal_agreement_signed_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_purpose</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_PURPOSE = createField("payout_account_purpose", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_cancel_details</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_CANCEL_DETAILS = createField("payout_cancel_details", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_legal_name</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_LEGAL_NAME = createField("payout_account_legal_name", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_trading_name</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_TRADING_NAME = createField("payout_account_trading_name", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_registered_address</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_REGISTERED_ADDRESS = createField("payout_account_registered_address", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_actual_address</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_ACTUAL_ADDRESS = createField("payout_account_actual_address", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_registered_number</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_REGISTERED_NUMBER = createField("payout_account_registered_number", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_bank_address</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_BANK_ADDRESS = createField("payout_account_bank_address", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_bank_iban</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_BANK_IBAN = createField("payout_account_bank_iban", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.payout_event_stat.payout_account_bank_bic</code>.
     */
    public final TableField<PayoutEventStatRecord, String> PAYOUT_ACCOUNT_BANK_BIC = createField("payout_account_bank_bic", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * Create a <code>mst.payout_event_stat</code> table reference
     */
    public PayoutEventStat() {
        this("payout_event_stat", null);
    }

    /**
     * Create an aliased <code>mst.payout_event_stat</code> table reference
     */
    public PayoutEventStat(String alias) {
        this(alias, PAYOUT_EVENT_STAT);
    }

    private PayoutEventStat(String alias, Table<PayoutEventStatRecord> aliased) {
        this(alias, aliased, null);
    }

    private PayoutEventStat(String alias, Table<PayoutEventStatRecord> aliased, Field<?>[] parameters) {
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
    public Identity<PayoutEventStatRecord, Long> getIdentity() {
        return Keys.IDENTITY_PAYOUT_EVENT_STAT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PayoutEventStatRecord> getPrimaryKey() {
        return Keys.PAYMENT_EVENT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PayoutEventStatRecord>> getKeys() {
        return Arrays.<UniqueKey<PayoutEventStatRecord>>asList(Keys.PAYMENT_EVENT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PayoutEventStat as(String alias) {
        return new PayoutEventStat(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PayoutEventStat rename(String name) {
        return new PayoutEventStat(name, null);
    }
}
