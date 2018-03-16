/*
 * This file is generated by jOOQ.
*/
package com.rbkmoney.magista.domain.tables;


import com.rbkmoney.magista.domain.Keys;
import com.rbkmoney.magista.domain.Mst;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.InvoicePaymentRefundStatus;
import com.rbkmoney.magista.domain.enums.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceStatus;
import com.rbkmoney.magista.domain.tables.records.InvoiceEventStatRecord;

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
public class InvoiceEventStat extends TableImpl<InvoiceEventStatRecord> {

    private static final long serialVersionUID = -1753340459;

    /**
     * The reference instance of <code>mst.invoice_event_stat</code>
     */
    public static final InvoiceEventStat INVOICE_EVENT_STAT = new InvoiceEventStat();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<InvoiceEventStatRecord> getRecordType() {
        return InvoiceEventStatRecord.class;
    }

    /**
     * The column <code>mst.invoice_event_stat.id</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('mst.invoice_event_stat_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>mst.invoice_event_stat.event_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> EVENT_ID = createField("event_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>mst.invoice_event_stat.event_category</code>.
     */
    public final TableField<InvoiceEventStatRecord, InvoiceEventCategory> EVENT_CATEGORY = createField("event_category", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.InvoiceEventCategory.class), this, "");

    /**
     * The column <code>mst.invoice_event_stat.event_type</code>.
     */
    public final TableField<InvoiceEventStatRecord, InvoiceEventType> EVENT_TYPE = createField("event_type", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.InvoiceEventType.class), this, "");

    /**
     * The column <code>mst.invoice_event_stat.event_created_at</code>.
     */
    public final TableField<InvoiceEventStatRecord, LocalDateTime> EVENT_CREATED_AT = createField("event_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>mst.invoice_event_stat.party_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PARTY_ID = createField("party_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.invoice_event_stat.party_email</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PARTY_EMAIL = createField("party_email", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.party_shop_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PARTY_SHOP_ID = createField("party_shop_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.invoice_event_stat.party_shop_name</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PARTY_SHOP_NAME = createField("party_shop_name", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.party_shop_description</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PARTY_SHOP_DESCRIPTION = createField("party_shop_description", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.party_shop_url</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PARTY_SHOP_URL = createField("party_shop_url", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.party_shop_category_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, Integer> PARTY_SHOP_CATEGORY_ID = createField("party_shop_category_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>mst.invoice_event_stat.party_shop_payout_tool_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PARTY_SHOP_PAYOUT_TOOL_ID = createField("party_shop_payout_tool_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.party_contract_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PARTY_CONTRACT_ID = createField("party_contract_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.party_contract_registered_number</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PARTY_CONTRACT_REGISTERED_NUMBER = createField("party_contract_registered_number", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.party_contract_inn</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PARTY_CONTRACT_INN = createField("party_contract_inn", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.invoice_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> INVOICE_ID = createField("invoice_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.invoice_event_stat.invoice_status</code>.
     */
    public final TableField<InvoiceEventStatRecord, InvoiceStatus> INVOICE_STATUS = createField("invoice_status", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.InvoiceStatus.class), this, "");

    /**
     * The column <code>mst.invoice_event_stat.invoice_status_details</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> INVOICE_STATUS_DETAILS = createField("invoice_status_details", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.invoice_product</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> INVOICE_PRODUCT = createField("invoice_product", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.invoice_event_stat.invoice_description</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> INVOICE_DESCRIPTION = createField("invoice_description", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.invoice_amount</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> INVOICE_AMOUNT = createField("invoice_amount", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>mst.invoice_event_stat.invoice_currency_code</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> INVOICE_CURRENCY_CODE = createField("invoice_currency_code", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "");

    /**
     * The column <code>mst.invoice_event_stat.invoice_due</code>.
     */
    public final TableField<InvoiceEventStatRecord, LocalDateTime> INVOICE_DUE = createField("invoice_due", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>mst.invoice_event_stat.invoice_created_at</code>.
     */
    public final TableField<InvoiceEventStatRecord, LocalDateTime> INVOICE_CREATED_AT = createField("invoice_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>mst.invoice_event_stat.invoice_context</code>.
     */
    public final TableField<InvoiceEventStatRecord, byte[]> INVOICE_CONTEXT = createField("invoice_context", org.jooq.impl.SQLDataType.BLOB, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_ID = createField("payment_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_status</code>.
     */
    public final TableField<InvoiceEventStatRecord, InvoicePaymentStatus> PAYMENT_STATUS = createField("payment_status", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class), this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_external_failure_code</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_EXTERNAL_FAILURE_CODE = createField("payment_external_failure_code", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_external_failure_description</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_EXTERNAL_FAILURE_DESCRIPTION = createField("payment_external_failure_description", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_amount</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> PAYMENT_AMOUNT = createField("payment_amount", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_currency_code</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_CURRENCY_CODE = createField("payment_currency_code", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_fee</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> PAYMENT_FEE = createField("payment_fee", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_provider_fee</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> PAYMENT_PROVIDER_FEE = createField("payment_provider_fee", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_external_fee</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> PAYMENT_EXTERNAL_FEE = createField("payment_external_fee", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_tool</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_TOOL = createField("payment_tool", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_masked_pan</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_MASKED_PAN = createField("payment_masked_pan", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_bin</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_BIN = createField("payment_bin", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_token</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_TOKEN = createField("payment_token", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_system</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_SYSTEM = createField("payment_system", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_session_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_SESSION_ID = createField("payment_session_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_country_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, Integer> PAYMENT_COUNTRY_ID = createField("payment_country_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_city_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, Integer> PAYMENT_CITY_ID = createField("payment_city_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_ip</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_IP = createField("payment_ip", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_phone_number</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_PHONE_NUMBER = createField("payment_phone_number", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_email</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_EMAIL = createField("payment_email", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_fingerprint</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_FINGERPRINT = createField("payment_fingerprint", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_created_at</code>.
     */
    public final TableField<InvoiceEventStatRecord, LocalDateTime> PAYMENT_CREATED_AT = createField("payment_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_context</code>.
     */
    public final TableField<InvoiceEventStatRecord, byte[]> PAYMENT_CONTEXT = createField("payment_context", org.jooq.impl.SQLDataType.BLOB, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_adjustment_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_ADJUSTMENT_ID = createField("payment_adjustment_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_adjustment_status</code>.
     */
    public final TableField<InvoiceEventStatRecord, AdjustmentStatus> PAYMENT_ADJUSTMENT_STATUS = createField("payment_adjustment_status", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.AdjustmentStatus.class), this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_adjustment_status_created_at</code>.
     */
    public final TableField<InvoiceEventStatRecord, LocalDateTime> PAYMENT_ADJUSTMENT_STATUS_CREATED_AT = createField("payment_adjustment_status_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_adjustment_created_at</code>.
     */
    public final TableField<InvoiceEventStatRecord, LocalDateTime> PAYMENT_ADJUSTMENT_CREATED_AT = createField("payment_adjustment_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_adjustment_reason</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_ADJUSTMENT_REASON = createField("payment_adjustment_reason", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_adjustment_fee</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> PAYMENT_ADJUSTMENT_FEE = createField("payment_adjustment_fee", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_adjustment_provider_fee</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> PAYMENT_ADJUSTMENT_PROVIDER_FEE = createField("payment_adjustment_provider_fee", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_adjustment_external_fee</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> PAYMENT_ADJUSTMENT_EXTERNAL_FEE = createField("payment_adjustment_external_fee", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_failure_class</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_FAILURE_CLASS = createField("payment_failure_class", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.invoice_template_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> INVOICE_TEMPLATE_ID = createField("invoice_template_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.invoice_cart</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> INVOICE_CART = createField("invoice_cart", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_flow</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_FLOW = createField("payment_flow", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_hold_on_expiration</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_HOLD_ON_EXPIRATION = createField("payment_hold_on_expiration", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_hold_until</code>.
     */
    public final TableField<InvoiceEventStatRecord, LocalDateTime> PAYMENT_HOLD_UNTIL = createField("payment_hold_until", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_terminal_provider</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_TERMINAL_PROVIDER = createField("payment_terminal_provider", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_refund_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_REFUND_ID = createField("payment_refund_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_refund_status</code>.
     */
    public final TableField<InvoiceEventStatRecord, InvoicePaymentRefundStatus> PAYMENT_REFUND_STATUS = createField("payment_refund_status", org.jooq.util.postgres.PostgresDataType.VARCHAR.asEnumDataType(com.rbkmoney.magista.domain.enums.InvoicePaymentRefundStatus.class), this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_refund_created_at</code>.
     */
    public final TableField<InvoiceEventStatRecord, LocalDateTime> PAYMENT_REFUND_CREATED_AT = createField("payment_refund_created_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_refund_reason</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_REFUND_REASON = createField("payment_refund_reason", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_refund_fee</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> PAYMENT_REFUND_FEE = createField("payment_refund_fee", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_refund_provider_fee</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> PAYMENT_REFUND_PROVIDER_FEE = createField("payment_refund_provider_fee", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_refund_external_fee</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> PAYMENT_REFUND_EXTERNAL_FEE = createField("payment_refund_external_fee", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_customer_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_CUSTOMER_ID = createField("payment_customer_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_provider_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, Integer> PAYMENT_PROVIDER_ID = createField("payment_provider_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_terminal_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, Integer> PAYMENT_TERMINAL_ID = createField("payment_terminal_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_domain_revision</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> PAYMENT_DOMAIN_REVISION = createField("payment_domain_revision", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.invoice_context_type</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> INVOICE_CONTEXT_TYPE = createField("invoice_context_type", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_context_type</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_CONTEXT_TYPE = createField("payment_context_type", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_adjustment_amount</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> PAYMENT_ADJUSTMENT_AMOUNT = createField("payment_adjustment_amount", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.invoice_party_revision</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> INVOICE_PARTY_REVISION = createField("invoice_party_revision", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_party_revision</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> PAYMENT_PARTY_REVISION = createField("payment_party_revision", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_digital_wallet_id</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_DIGITAL_WALLET_ID = createField("payment_digital_wallet_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_digital_wallet_provider</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_DIGITAL_WALLET_PROVIDER = createField("payment_digital_wallet_provider", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_refund_amount</code>.
     */
    public final TableField<InvoiceEventStatRecord, Long> PAYMENT_REFUND_AMOUNT = createField("payment_refund_amount", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_refund_currency_code</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_REFUND_CURRENCY_CODE = createField("payment_refund_currency_code", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>mst.invoice_event_stat.payment_status_sub_failure</code>.
     */
    public final TableField<InvoiceEventStatRecord, String> PAYMENT_STATUS_SUB_FAILURE = createField("payment_status_sub_failure", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * Create a <code>mst.invoice_event_stat</code> table reference
     */
    public InvoiceEventStat() {
        this("invoice_event_stat", null);
    }

    /**
     * Create an aliased <code>mst.invoice_event_stat</code> table reference
     */
    public InvoiceEventStat(String alias) {
        this(alias, INVOICE_EVENT_STAT);
    }

    private InvoiceEventStat(String alias, Table<InvoiceEventStatRecord> aliased) {
        this(alias, aliased, null);
    }

    private InvoiceEventStat(String alias, Table<InvoiceEventStatRecord> aliased, Field<?>[] parameters) {
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
    public Identity<InvoiceEventStatRecord, Long> getIdentity() {
        return Keys.IDENTITY_INVOICE_EVENT_STAT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<InvoiceEventStatRecord> getPrimaryKey() {
        return Keys.INVOICE_EVENT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<InvoiceEventStatRecord>> getKeys() {
        return Arrays.<UniqueKey<InvoiceEventStatRecord>>asList(Keys.INVOICE_EVENT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvoiceEventStat as(String alias) {
        return new InvoiceEventStat(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public InvoiceEventStat rename(String name) {
        return new InvoiceEventStat(name, null);
    }
}
