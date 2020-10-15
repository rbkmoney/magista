package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.damsel.merch_stat.StatPayment;
import com.rbkmoney.damsel.merch_stat.StatRefund;
import com.rbkmoney.magista.dao.ReportDao;
import com.rbkmoney.magista.dao.impl.field.ConditionParameterSource;
import com.rbkmoney.magista.dao.impl.mapper.ColumnStringMapRowMapper;
import com.rbkmoney.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.magista.dao.impl.mapper.StatPaymentMapper;
import com.rbkmoney.magista.dao.impl.mapper.StatRefundMapper;
import com.rbkmoney.magista.domain.Tables;
import com.rbkmoney.magista.domain.enums.*;
import com.rbkmoney.magista.domain.tables.pojos.Adjustment;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.rbkmoney.magista.domain.Tables.CHARGEBACK_DATA;
import static com.rbkmoney.magista.domain.Tables.PAYMENT_EVENT;
import static com.rbkmoney.magista.domain.tables.Adjustment.ADJUSTMENT;
import static com.rbkmoney.magista.domain.tables.PaymentData.PAYMENT_DATA;
import static com.rbkmoney.magista.domain.tables.PayoutEventStat.PAYOUT_EVENT_STAT;
import static com.rbkmoney.magista.domain.tables.Refund.REFUND;
import static org.jooq.Comparator.EQUALS;
import static org.jooq.Comparator.GREATER;

@Component
public class ReportDaoImpl extends AbstractDao implements ReportDao {

    private final StatPaymentMapper statPaymentMapper;
    private final StatRefundMapper statRefundMapper;
    private final RowMapper<Adjustment> adjustmentRowMapper;

    public ReportDaoImpl(@Qualifier("slaveDataSource") DataSource ds) {
        super(ds);
        statPaymentMapper = new StatPaymentMapper();
        statRefundMapper = new StatRefundMapper();
        adjustmentRowMapper = new RecordRowMapper<>(Tables.ADJUSTMENT, Adjustment.class);
    }

    @Override
    public Map<String, String> getPaymentAccountingData(String merchantId, String shopId, String currencyCode, Optional<LocalDateTime> fromTime, LocalDateTime toTime) throws DaoException {
        Query query = getDslContext().select(
                DSL.val(merchantId).as("merchant_id"),
                DSL.val(shopId).as("shop_id"),
                DSL.val(currencyCode).as("currency_code"),
                DSL.ifnull(DSL.sum(PAYMENT_EVENT.PAYMENT_AMOUNT), 0L).as("funds_acquired"),
                DSL.ifnull(DSL.sum(PAYMENT_EVENT.PAYMENT_FEE), 0L).as("fee_charged")
        ).from(PAYMENT_DATA)
                .join(PAYMENT_EVENT)
                .on(
                        appendDateTimeRangeConditions(
                                PAYMENT_DATA.PARTY_ID.eq(UUID.fromString(merchantId))
                                        .and(PAYMENT_DATA.PARTY_SHOP_ID.eq(shopId))
                                        .and(PAYMENT_DATA.INVOICE_ID.eq(PAYMENT_EVENT.INVOICE_ID))
                                        .and(PAYMENT_DATA.PAYMENT_ID.eq(PAYMENT_EVENT.PAYMENT_ID))
                                        .and(PAYMENT_DATA.PAYMENT_CURRENCY_CODE.eq(currencyCode))
                                        .and(PAYMENT_EVENT.PAYMENT_STATUS.eq(com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.captured))
                                        .and(PAYMENT_EVENT.EVENT_TYPE.eq(InvoiceEventType.INVOICE_PAYMENT_STATUS_CHANGED)),
                                PAYMENT_EVENT.EVENT_CREATED_AT,
                                fromTime,
                                Optional.of(toTime)
                        )
                );

        return fetchOne(query, new ColumnStringMapRowMapper());
    }

    @Override
    public Map<String, String> getRefundAccountingData(String merchantId, String shopId, String currencyCode, Optional<LocalDateTime> fromTime, LocalDateTime toTime) throws DaoException {
        Query query = getDslContext().select(
                DSL.val(merchantId).as("merchant_id"),
                DSL.val(shopId).as("shop_id"),
                DSL.val(currencyCode).as("currency_code"),
                DSL.ifnull(DSL.sum(REFUND.REFUND_AMOUNT.minus(REFUND.REFUND_FEE)), 0L).as("funds_refunded")
        ).from(REFUND).where(
                appendDateTimeRangeConditions(
                        REFUND.PARTY_ID.eq(merchantId)
                                .and(REFUND.PARTY_SHOP_ID.eq(shopId))
                                .and(REFUND.REFUND_CURRENCY_CODE.eq(currencyCode))
                                .and(REFUND.REFUND_STATUS.eq(RefundStatus.succeeded)),
                        REFUND.EVENT_CREATED_AT,
                        fromTime,
                        Optional.of(toTime)
                )
        );

        return fetchOne(query, new ColumnStringMapRowMapper());
    }

    @Override
    public Map<String, String> getAdjustmentAccountingData(String merchantId, String shopId, String currencyCode, Optional<LocalDateTime> fromTime, LocalDateTime toTime) throws DaoException {
        Query query = getDslContext().select(
                DSL.val(merchantId).as("merchant_id"),
                DSL.val(shopId).as("shop_id"),
                DSL.val(currencyCode).as("currency_code"),
                DSL.ifnull(DSL.sum(ADJUSTMENT.ADJUSTMENT_AMOUNT), 0L).as("funds_adjusted")
        ).from(ADJUSTMENT)
                .where(
                        appendDateTimeRangeConditions(
                                ADJUSTMENT.PARTY_ID.eq(merchantId)
                                        .and(ADJUSTMENT.PARTY_SHOP_ID.eq(shopId))
                                        .and(ADJUSTMENT.ADJUSTMENT_STATUS.eq(AdjustmentStatus.captured))
                                        .and(ADJUSTMENT.ADJUSTMENT_CURRENCY_CODE.eq(currencyCode)),
                                ADJUSTMENT.EVENT_CREATED_AT,
                                fromTime,
                                Optional.of(toTime)
                        )
                );
        return fetchOne(query, new ColumnStringMapRowMapper());
    }

    @Override
    public Map<String, String> getPayoutAccountingData(String merchantId, String shopId, String currencyCode, Optional<LocalDateTime> fromTime, LocalDateTime toTime) throws DaoException {
        Field<String> merchantIdField = DSL.field("merchant_id", String.class);
        Field<String> shopIdField = DSL.field("shop_id", String.class);
        Field<String> currencyCodeField = DSL.field("currency_code", String.class);
        Field<Long> fundsPaidOutField = DSL.field("funds_paid_out", Long.class);
        Field<Long> paidFundsField = DSL.field("paid_funds", Long.class);
        Field<Long> cancelledFundsField = DSL.field("cancelled_funds", Long.class);

        Query query = getDslContext().select(
                merchantIdField,
                shopIdField,
                currencyCodeField,
                DSL.ifnull(paidFundsField, 0L)
                        .minus(DSL.ifnull(cancelledFundsField, 0L)).as(fundsPaidOutField)
        ).from(
                getDslContext().select(
                        DSL.val(merchantId).as(merchantIdField),
                        DSL.val(shopId).as(shopIdField),
                        DSL.val(currencyCode).as(currencyCodeField),
                        DSL.sum(
                                DSL.ifnull(PAYOUT_EVENT_STAT.PAYOUT_AMOUNT, 0L)
                                        .minus(DSL.ifnull(PAYOUT_EVENT_STAT.PAYOUT_FEE, 0L))
                        ).as(paidFundsField)
                ).from(PAYOUT_EVENT_STAT).where(
                        appendDateTimeRangeConditions(
                                PAYOUT_EVENT_STAT.PARTY_ID.eq(merchantId)
                                        .and(PAYOUT_EVENT_STAT.PARTY_SHOP_ID.eq(shopId))
                                        .and(PAYOUT_EVENT_STAT.PAYOUT_STATUS.eq(PayoutStatus.paid))
                                        .and(PAYOUT_EVENT_STAT.PAYOUT_CURRENCY_CODE.eq(currencyCode)),
                                PAYOUT_EVENT_STAT.EVENT_CREATED_AT,
                                fromTime,
                                Optional.of(toTime)
                        )
                ).asTable().leftJoin(
                        getDslContext().select(
                                DSL.sum(
                                        DSL.ifnull(PAYOUT_EVENT_STAT.PAYOUT_AMOUNT, 0L)
                                                .minus(DSL.ifnull(PAYOUT_EVENT_STAT.PAYOUT_FEE, 0L))
                                ).as(cancelledFundsField)
                        ).from(PAYOUT_EVENT_STAT).where(
                                appendDateTimeRangeConditions(
                                        PAYOUT_EVENT_STAT.PARTY_SHOP_ID.eq(shopId)
                                                .and(PAYOUT_EVENT_STAT.PAYOUT_STATUS.eq(PayoutStatus.cancelled))
                                                .and(PAYOUT_EVENT_STAT.PAYOUT_ID.in(
                                                        getDslContext().select(PAYOUT_EVENT_STAT.PAYOUT_ID)
                                                                .from(PAYOUT_EVENT_STAT)
                                                                .where(
                                                                        PAYOUT_EVENT_STAT.PARTY_ID.eq(merchantId)
                                                                                .and(PAYOUT_EVENT_STAT.PARTY_SHOP_ID.eq(shopId))
                                                                                .and(PAYOUT_EVENT_STAT.PAYOUT_CURRENCY_CODE.eq(currencyCode))
                                                                                .and(PAYOUT_EVENT_STAT.PAYOUT_STATUS.eq(PayoutStatus.paid))
                                                                )
                                                ))
                                                .and(PAYOUT_EVENT_STAT.PAYOUT_CURRENCY_CODE.eq(currencyCode)),
                                        PAYOUT_EVENT_STAT.EVENT_CREATED_AT,
                                        fromTime,
                                        Optional.of(toTime)
                                )
                        ).asTable()
                ).on()
        );

        return fetchOne(query, new ColumnStringMapRowMapper());
    }

    @Override
    public Map<String, String> getChargebackAccountingData(String merchantId, String shopId, String currencyCode, LocalDateTime fromTime, LocalDateTime toTime) {
        Query query = getDslContext().select(
                DSL.val(merchantId).as("merchant_id"),
                DSL.val(shopId).as("shop_id"),
                DSL.val(currencyCode).as("currency_code"),
                DSL.ifnull(DSL.sum(CHARGEBACK_DATA.CHARGEBACK_AMOUNT), 0L).as("funds_returned")
        ).from(CHARGEBACK_DATA)
                .where(
                        appendDateTimeRangeConditions(
                                CHARGEBACK_DATA.PARTY_ID.eq(merchantId)
                                        .and(CHARGEBACK_DATA.PARTY_SHOP_ID.eq(shopId))
                                        .and(CHARGEBACK_DATA.CHARGEBACK_STATUS.eq(ChargebackStatus.accepted))
                                        .and(CHARGEBACK_DATA.CHARGEBACK_CURRENCY_CODE.eq(currencyCode)),
                                CHARGEBACK_DATA.EVENT_CREATED_AT,
                                Optional.ofNullable(fromTime),
                                Optional.of(toTime)
                        )
                );
        return fetchOne(query, new ColumnStringMapRowMapper());
    }

    @Override
    public Collection<Map.Entry<Long, StatPayment>> getPaymentsForReport(
            String partyId,
            Optional<String> shopId,
            Optional<String> invoiceId,
            Optional<String> paymentId,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<LocalDateTime> whereTime,
            int limit
    ) throws DaoException {
        Query query = getDslContext()
                .select(
                        PAYMENT_DATA.INVOICE_ID,
                        PAYMENT_DATA.PAYMENT_ID,
                        PAYMENT_DATA.PARTY_ID,
                        PAYMENT_DATA.PARTY_SHOP_ID,
                        PAYMENT_DATA.PAYMENT_CURRENCY_CODE,
                        PAYMENT_DATA.PAYMENT_ORIGIN_AMOUNT,
                        PAYMENT_DATA.PAYMENT_CUSTOMER_ID,
                        PAYMENT_DATA.PAYMENT_TOOL,
                        PAYMENT_DATA.PAYMENT_BANK_CARD_MASKED_PAN,
                        PAYMENT_DATA.PAYMENT_BANK_CARD_BIN,
                        PAYMENT_DATA.PAYMENT_BANK_CARD_TOKEN,
                        PAYMENT_DATA.PAYMENT_BANK_CARD_SYSTEM,
                        PAYMENT_DATA.PAYMENT_BANK_CARD_TOKEN_PROVIDER,
                        PAYMENT_DATA.PAYMENT_TERMINAL_PROVIDER,
                        PAYMENT_DATA.PAYMENT_DIGITAL_WALLET_ID,
                        PAYMENT_DATA.PAYMENT_DIGITAL_WALLET_PROVIDER,
                        PAYMENT_DATA.PAYMENT_FLOW,
                        PAYMENT_DATA.PAYMENT_MAKE_RECURRENT_FLAG,
                        PAYMENT_DATA.PAYMENT_RECURRENT_PAYER_PARENT_INVOICE_ID,
                        PAYMENT_DATA.PAYMENT_RECURRENT_PAYER_PARENT_PAYMENT_ID,
                        PAYMENT_DATA.PAYMENT_HOLD_ON_EXPIRATION,
                        PAYMENT_DATA.PAYMENT_HOLD_UNTIL,
                        PAYMENT_DATA.PAYMENT_PAYER_TYPE,
                        PAYMENT_DATA.PAYMENT_SESSION_ID,
                        PAYMENT_DATA.PAYMENT_FINGERPRINT,
                        PAYMENT_DATA.PAYMENT_IP,
                        PAYMENT_DATA.PAYMENT_PHONE_NUMBER,
                        PAYMENT_DATA.PAYMENT_EMAIL,
                        PAYMENT_DATA.PAYMENT_CREATED_AT,
                        PAYMENT_DATA.PAYMENT_PARTY_REVISION,
                        PAYMENT_DATA.PAYMENT_CONTEXT_TYPE,
                        PAYMENT_DATA.PAYMENT_CONTEXT,
                        PAYMENT_DATA.EXTERNAL_ID,
                        PAYMENT_EVENT.ID,
                        PAYMENT_EVENT.EVENT_ID,
                        PAYMENT_EVENT.EVENT_CREATED_AT,
                        PAYMENT_EVENT.EVENT_TYPE,
                        PAYMENT_EVENT.INVOICE_ID,
                        PAYMENT_EVENT.PAYMENT_ID,
                        PAYMENT_EVENT.PAYMENT_STATUS,
                        PAYMENT_EVENT.PAYMENT_OPERATION_FAILURE_CLASS,
                        PAYMENT_EVENT.PAYMENT_EXTERNAL_FAILURE,
                        PAYMENT_EVENT.PAYMENT_EXTERNAL_FAILURE_REASON,
                        PAYMENT_EVENT.PAYMENT_AMOUNT,
                        PAYMENT_EVENT.PAYMENT_FEE,
                        PAYMENT_EVENT.PAYMENT_PROVIDER_FEE,
                        PAYMENT_EVENT.PAYMENT_EXTERNAL_FEE,
                        PAYMENT_EVENT.PAYMENT_DOMAIN_REVISION,
                        PAYMENT_EVENT.PAYMENT_SHORT_ID,
                        PAYMENT_EVENT.PAYMENT_PROVIDER_ID,
                        PAYMENT_EVENT.PAYMENT_TERMINAL_ID
                )
                .from(PAYMENT_DATA)
                .join(PAYMENT_EVENT)
                .on(
                        appendDateTimeRangeConditions(
                                appendConditions(
                                        PAYMENT_DATA.PARTY_ID.eq(UUID.fromString(partyId))
                                                .and(PAYMENT_DATA.INVOICE_ID.eq(PAYMENT_EVENT.INVOICE_ID))
                                                .and(PAYMENT_DATA.PAYMENT_ID.eq(PAYMENT_EVENT.PAYMENT_ID))
                                                .and(PAYMENT_EVENT.EVENT_TYPE.eq(InvoiceEventType.INVOICE_PAYMENT_STATUS_CHANGED))
                                                .and(PAYMENT_EVENT.PAYMENT_STATUS.eq(com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.captured)),
                                        Operator.AND,
                                        new ConditionParameterSource()
                                                .addValue(PAYMENT_EVENT.EVENT_CREATED_AT, whereTime.orElse(null), GREATER)
                                                .addValue(PAYMENT_DATA.INVOICE_ID, invoiceId.orElse(null), EQUALS)
                                                .addValue(PAYMENT_DATA.PAYMENT_ID, paymentId.orElse(null), EQUALS)
                                                .addValue(PAYMENT_DATA.PARTY_SHOP_ID, shopId.orElse(null), EQUALS)
                                ),
                                PAYMENT_EVENT.EVENT_CREATED_AT,
                                fromTime,
                                toTime
                        )
                )
                .orderBy(PAYMENT_EVENT.EVENT_CREATED_AT)
                .limit(limit);

        return fetch(query, statPaymentMapper);
    }

    @Override
    public Collection<Map.Entry<Long, StatRefund>> getRefundsForReport(String partyId, Optional<String> shopId, Optional<String> invoiceId, Optional<String> paymentId, Optional<String> refundId, Optional<LocalDateTime> fromTime, Optional<LocalDateTime> toTime, Optional<LocalDateTime> whereTime, int limit) throws DaoException {
        Query query = getDslContext().selectFrom(REFUND)
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(
                                        REFUND.PARTY_ID.eq(partyId)
                                                .and(REFUND.REFUND_STATUS.eq(RefundStatus.succeeded)),
                                        Operator.AND,
                                        new ConditionParameterSource()
                                                .addValue(REFUND.EVENT_CREATED_AT, whereTime.orElse(null), GREATER)
                                                .addValue(REFUND.INVOICE_ID, invoiceId.orElse(null), EQUALS)
                                                .addValue(REFUND.PAYMENT_ID, paymentId.orElse(null), EQUALS)
                                                .addValue(REFUND.PARTY_SHOP_ID, shopId.orElse(null), EQUALS)
                                ),
                                REFUND.EVENT_CREATED_AT,
                                fromTime,
                                toTime
                        )
                ).orderBy(REFUND.EVENT_CREATED_AT);

        return fetch(query, statRefundMapper);
    }

    @Override
    public Collection<Adjustment> getAdjustmentsForReport(String partyId, Optional<String> shopId, Optional<LocalDateTime> fromTime, Optional<LocalDateTime> toTime, Optional<LocalDateTime> whereTime, int limit) throws DaoException {
        Query query = getDslContext().selectFrom(ADJUSTMENT)
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(
                                        ADJUSTMENT.PARTY_ID.eq(partyId)
                                                .and(ADJUSTMENT.ADJUSTMENT_STATUS.eq(AdjustmentStatus.captured)),
                                        Operator.AND,
                                        new ConditionParameterSource()
                                                .addValue(ADJUSTMENT.EVENT_CREATED_AT, whereTime.orElse(null), GREATER)
                                                .addValue(ADJUSTMENT.PARTY_SHOP_ID, shopId.orElse(null), EQUALS)
                                ),
                                ADJUSTMENT.EVENT_CREATED_AT,
                                fromTime,
                                toTime
                        )
                ).orderBy(REFUND.EVENT_CREATED_AT);

        return fetch(query, adjustmentRowMapper);
    }

}
