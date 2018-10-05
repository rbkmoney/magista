package com.rbkmoney.magista.dao.impl;

import com.google.common.collect.ImmutableMap;
import com.rbkmoney.damsel.merch_stat.StatPayment;
import com.rbkmoney.magista.dao.ReportDao;
import com.rbkmoney.magista.dao.impl.field.ConditionParameterSource;
import com.rbkmoney.magista.dao.impl.mapper.StatPaymentMapper;
import com.rbkmoney.magista.domain.enums.AdjustmentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.RefundStatus;
import com.rbkmoney.magista.domain.tables.PaymentEvent;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    public ReportDaoImpl(DataSource ds) {
        super(ds);
        statPaymentMapper = new StatPaymentMapper();
    }

    @Override
    public Map<String, String> getPaymentAccountingData(String merchantId, String shopId, String currencyCode, Optional<LocalDateTime> fromTime, LocalDateTime toTime) throws DaoException {
        Query query = getDslContext().select(
                PAYMENT_DATA.PARTY_ID.as("merchant_id"),
                PAYMENT_DATA.PARTY_SHOP_ID.as("shop_id"),
                PAYMENT_DATA.PAYMENT_CURRENCY_CODE.as("currency_code"),
                DSL.sum(PAYMENT_DATA.PAYMENT_AMOUNT).as("funds_acquired"),
                DSL.sum(PAYMENT_EVENT.PAYMENT_FEE).as("fee_charged")
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
                ).groupBy(
                        PAYMENT_DATA.PARTY_ID,
                        PAYMENT_DATA.PARTY_SHOP_ID,
                        PAYMENT_DATA.PAYMENT_CURRENCY_CODE
                );

        return Optional.ofNullable(
                fetchOne(query, (rs, i) -> ImmutableMap.<String, String>builder()
                        .put("merchant_id", rs.getString("merchant_id"))
                        .put("shop_id", rs.getString("shop_id"))
                        .put("currency_code", rs.getString("currency_code"))
                        .put("funds_acquired", rs.getString("funds_acquired"))
                        .put("fee_charged", rs.getString("fee_charged"))
                        .build()
                )
        ).orElse(
                ImmutableMap.<String, String>builder()
                        .put("merchant_id", merchantId)
                        .put("shop_id", shopId)
                        .put("currency_code", currencyCode)
                        .put("funds_acquired", "0")
                        .put("fee_charged", "0")
                        .build()
        );
    }

    @Override
    public Map<String, String> getRefundAccountingData(String merchantId, String shopId, String currencyCode, Optional<LocalDateTime> fromTime, LocalDateTime toTime) throws DaoException {
        Query query = getDslContext().select(
                REFUND.PARTY_ID.as("merchant_id"),
                REFUND.PARTY_SHOP_ID.as("shop_id"),
                REFUND.REFUND_CURRENCY_CODE.as("currency_code"),
                DSL.sum(REFUND.REFUND_AMOUNT.minus(REFUND.REFUND_FEE)).as("funds_refunded")
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
        ).groupBy(
                REFUND.PARTY_ID,
                REFUND.PARTY_SHOP_ID,
                REFUND.REFUND_CURRENCY_CODE
        );

        return Optional.ofNullable(
                fetchOne(query, (rs, i) -> ImmutableMap.<String, String>builder()
                        .put("merchant_id", rs.getString("merchant_id"))
                        .put("shop_id", rs.getString("shop_id"))
                        .put("currency_code", rs.getString("currency_code"))
                        .put("funds_refunded", rs.getString("funds_refunded"))
                        .build())
        ).orElse(
                ImmutableMap.<String, String>builder()
                        .put("merchant_id", merchantId)
                        .put("shop_id", shopId)
                        .put("currency_code", currencyCode)
                        .put("funds_refunded", "0")
                        .build()
        );
    }

    @Override
    public Map<String, String> getAdjustmentAccountingData(String merchantId, String shopId, String currencyCode, Optional<LocalDateTime> fromTime, LocalDateTime toTime) throws DaoException {
        Query query = getDslContext().select(
                ADJUSTMENT.PARTY_ID.as("merchant_id"),
                ADJUSTMENT.PARTY_SHOP_ID.as("shop_id"),
                PAYMENT_DATA.PAYMENT_CURRENCY_CODE.as("currency_code"),
                DSL.sum(PAYMENT_EVENT.PAYMENT_FEE.minus(ADJUSTMENT.ADJUSTMENT_FEE)).as("funds_adjusted")
        ).from(ADJUSTMENT)
                .join(PAYMENT_DATA)
                .on(
                        appendDateTimeRangeConditions(
                                ADJUSTMENT.PARTY_ID.eq(merchantId)
                                        .and(ADJUSTMENT.PARTY_SHOP_ID.eq(shopId))
                                        .and(ADJUSTMENT.INVOICE_ID.eq(PAYMENT_DATA.INVOICE_ID))
                                        .and(ADJUSTMENT.PAYMENT_ID.eq(PAYMENT_DATA.PAYMENT_ID))
                                        .and(ADJUSTMENT.ADJUSTMENT_STATUS.eq(AdjustmentStatus.captured))
                                        .and(PAYMENT_DATA.PAYMENT_CURRENCY_CODE.eq(currencyCode)),
                                ADJUSTMENT.EVENT_CREATED_AT,
                                fromTime,
                                Optional.of(toTime)
                        )
                ).join(PAYMENT_EVENT)
                .on(
                        PAYMENT_DATA.INVOICE_ID.eq(PAYMENT_EVENT.INVOICE_ID)
                                .and(PAYMENT_DATA.PAYMENT_ID.eq(PAYMENT_EVENT.PAYMENT_ID))
                                .and(PAYMENT_EVENT.EVENT_TYPE.eq(InvoiceEventType.INVOICE_PAYMENT_STATUS_CHANGED))
                                .and(PAYMENT_EVENT.PAYMENT_STATUS.eq(com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.captured))
                ).groupBy(
                        ADJUSTMENT.PARTY_ID,
                        ADJUSTMENT.PARTY_SHOP_ID,
                        PAYMENT_DATA.PAYMENT_CURRENCY_CODE
                );
        return Optional.ofNullable(
                fetchOne(query, (rs, i) -> ImmutableMap.<String, String>builder()
                        .put("merchant_id", rs.getString("merchant_id"))
                        .put("shop_id", rs.getString("shop_id"))
                        .put("currency_code", rs.getString("currency_code"))
                        .put("funds_adjusted", rs.getString("funds_adjusted"))
                        .build())
        ).orElse(
                ImmutableMap.<String, String>builder()
                        .put("merchant_id", merchantId)
                        .put("shop_id", shopId)
                        .put("currency_code", currencyCode)
                        .put("funds_adjusted", "0")
                        .build()
        );
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
                paidFundsField.minus(DSL.coalesce(cancelledFundsField, 0)).as(fundsPaidOutField)
        ).from(
                getDslContext().select(
                        PAYOUT_EVENT_STAT.PARTY_ID.as(merchantIdField),
                        PAYOUT_EVENT_STAT.PARTY_SHOP_ID.as(shopIdField),
                        PAYOUT_EVENT_STAT.PAYOUT_CURRENCY_CODE.as(currencyCodeField),
                        DSL.sum(
                                PAYOUT_EVENT_STAT.PAYOUT_AMOUNT
                                        .minus(DSL.coalesce(PAYOUT_EVENT_STAT.PAYOUT_FEE, 0))
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
                ).groupBy(
                        PAYOUT_EVENT_STAT.PARTY_ID,
                        PAYOUT_EVENT_STAT.PARTY_SHOP_ID,
                        PAYOUT_EVENT_STAT.PAYOUT_CURRENCY_CODE
                ).asTable().leftJoin(
                        getDslContext().select(
                                DSL.sum(
                                        PAYOUT_EVENT_STAT.PAYOUT_AMOUNT
                                                .minus(DSL.coalesce(PAYOUT_EVENT_STAT.PAYOUT_FEE, 0))
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
                        ).groupBy(
                                PAYOUT_EVENT_STAT.PARTY_ID,
                                PAYOUT_EVENT_STAT.PARTY_SHOP_ID,
                                PAYOUT_EVENT_STAT.PAYOUT_CURRENCY_CODE
                        ).asTable()
                ).on()
        );

        return Optional.ofNullable(
                fetchOne(query, (rs, i) -> ImmutableMap.<String, String>builder()
                        .put("merchant_id", rs.getString("merchant_id"))
                        .put("shop_id", rs.getString("shop_id"))
                        .put("currency_code", rs.getString("currency_code"))
                        .put("funds_paid_out", rs.getString("funds_paid_out"))
                        .build())
        ).orElse(
                ImmutableMap.<String, String>builder()
                        .put("merchant_id", merchantId)
                        .put("shop_id", shopId)
                        .put("currency_code", currencyCode)
                        .put("funds_paid_out", "0")
                        .build()
        );
    }

    @Override
    public Collection<Map.Entry<Long, StatPayment>> getPaymentsForReport(
            String partyId,
            String shopId,
            Optional<String> invoiceId,
            Optional<String> paymentId,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            int limit
    ) throws DaoException {
        PaymentEvent paymentEvent = PAYMENT_EVENT.as("payment_event");

        Query query = getDslContext()
                .select(
                        PAYMENT_DATA.INVOICE_ID,
                        PAYMENT_DATA.PAYMENT_ID,
                        PAYMENT_DATA.PARTY_ID,
                        PAYMENT_DATA.PARTY_SHOP_ID,
                        PAYMENT_DATA.PAYMENT_CURRENCY_CODE,
                        PAYMENT_DATA.PAYMENT_AMOUNT,
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
                        paymentEvent.ID,
                        paymentEvent.EVENT_ID,
                        paymentEvent.EVENT_CREATED_AT,
                        paymentEvent.EVENT_TYPE,
                        paymentEvent.INVOICE_ID,
                        paymentEvent.PAYMENT_ID,
                        paymentEvent.PAYMENT_STATUS,
                        paymentEvent.PAYMENT_OPERATION_FAILURE_CLASS,
                        paymentEvent.PAYMENT_EXTERNAL_FAILURE,
                        paymentEvent.PAYMENT_EXTERNAL_FAILURE_REASON,
                        paymentEvent.PAYMENT_FEE,
                        paymentEvent.PAYMENT_PROVIDER_FEE,
                        paymentEvent.PAYMENT_EXTERNAL_FEE,
                        paymentEvent.PAYMENT_DOMAIN_REVISION,
                        paymentEvent.PAYMENT_SHORT_ID,
                        paymentEvent.PAYMENT_PROVIDER_ID,
                        paymentEvent.PAYMENT_TERMINAL_ID
                )
                .from(PAYMENT_DATA)
                .join(
                        DSL.lateral(
                                getDslContext()
                                        .selectFrom(PAYMENT_EVENT)
                                        .where(
                                                appendDateTimeRangeConditions(
                                                        PAYMENT_DATA.INVOICE_ID.eq(PAYMENT_EVENT.INVOICE_ID)
                                                                .and(PAYMENT_DATA.PAYMENT_ID.eq(PAYMENT_EVENT.PAYMENT_ID))
                                                                .and(PAYMENT_EVENT.PAYMENT_STATUS.eq(com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.captured)),
                                                        PAYMENT_EVENT.EVENT_CREATED_AT,
                                                        fromTime,
                                                        toTime
                                                )
                                        ).orderBy(PAYMENT_EVENT.ID.desc())
                                        .limit(1)
                        ).as(paymentEvent)
                ).on(
                        appendConditions(
                                PAYMENT_DATA.PARTY_ID.eq(UUID.fromString(partyId))
                                        .and(PAYMENT_DATA.PARTY_SHOP_ID.eq(shopId)),
                                Operator.AND,
                                new ConditionParameterSource()
                                        .addValue(paymentEvent.ID, fromId.orElse(null), GREATER)
                                        .addValue(PAYMENT_DATA.INVOICE_ID, invoiceId.orElse(null), EQUALS)
                                        .addValue(PAYMENT_DATA.PAYMENT_ID, paymentId.orElse(null), EQUALS)
                        )
                )
                .orderBy(paymentEvent.EVENT_CREATED_AT)
                .limit(limit);

        return fetch(query, statPaymentMapper);
    }

}
