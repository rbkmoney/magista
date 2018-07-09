package com.rbkmoney.magista.dao;

import com.google.common.collect.ImmutableMap;
import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.BankCardTokenProvider;
import com.rbkmoney.damsel.domain.InvoiceCart;
import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentStatus;
import com.rbkmoney.damsel.merch_stat.InvoiceStatus;
import com.rbkmoney.damsel.merch_stat.OnHoldExpiration;
import com.rbkmoney.damsel.merch_stat.PaymentTool;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.*;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.tables.InvoiceEvent;
import com.rbkmoney.magista.domain.tables.PaymentData;
import com.rbkmoney.magista.domain.tables.PaymentEvent;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.domain.tables.pojos.Refund;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.query.impl.InvoicesFunction;
import com.rbkmoney.magista.query.impl.PaymentsFunction;
import com.rbkmoney.magista.util.DamselUtil;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.rbkmoney.geck.common.util.TypeUtil.toEnumField;
import static com.rbkmoney.magista.domain.Tables.PAYMENT_EVENT;
import static com.rbkmoney.magista.domain.tables.Adjustment.ADJUSTMENT;
import static com.rbkmoney.magista.domain.tables.InvoiceData.INVOICE_DATA;
import static com.rbkmoney.magista.domain.tables.InvoiceEvent.INVOICE_EVENT;
import static com.rbkmoney.magista.domain.tables.InvoiceEventStat.INVOICE_EVENT_STAT;
import static com.rbkmoney.magista.domain.tables.PaymentData.PAYMENT_DATA;
import static com.rbkmoney.magista.domain.tables.PayoutEventStat.PAYOUT_EVENT_STAT;
import static com.rbkmoney.magista.domain.tables.Refund.REFUND;
import static org.jooq.Comparator.EQUALS;
import static org.jooq.Comparator.LESS;

/**
 * Created by vpankrashkin on 10.08.16.
 */
public class StatisticsDaoImpl extends AbstractDao implements StatisticsDao {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final int MAX_LIMIT = 1000;

    public StatisticsDaoImpl(DataSource ds) {
        super(ds);
    }

    @Override
    public Collection<Map.Entry<Long, StatInvoice>> getInvoices(
            InvoicesFunction.InvoicesParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            Optional<Integer> limit
    ) throws DaoException {

        Query query = buildInvoiceSelectConditionStepQuery(
                parameters,
                fromTime,
                toTime,
                fromId
        ).orderBy(INVOICE_DATA.INVOICE_CREATED_AT.desc())
                .limit(Math.min(limit.orElse(MAX_LIMIT), MAX_LIMIT));
        return fetch(query, (rs, i) -> {
            StatInvoice statInvoice = new StatInvoice();
            statInvoice.setId(rs.getString(INVOICE_DATA.INVOICE_ID.getName()));
            statInvoice.setOwnerId(rs.getString(INVOICE_DATA.PARTY_ID.getName()));
            statInvoice.setShopId(rs.getString(INVOICE_DATA.PARTY_SHOP_ID.getName()));
            statInvoice.setAmount(rs.getLong(INVOICE_DATA.INVOICE_AMOUNT.getName()));
            statInvoice.setCurrencySymbolicCode(rs.getString(INVOICE_DATA.INVOICE_CURRENCY_CODE.getName()));
            statInvoice.setProduct(rs.getString(INVOICE_DATA.INVOICE_PRODUCT.getName()));
            statInvoice.setDescription(rs.getString(INVOICE_DATA.INVOICE_DESCRIPTION.getName()));
            statInvoice.setCreatedAt(
                    TypeUtil.temporalToString(
                            rs.getObject(INVOICE_DATA.INVOICE_CREATED_AT.getName(), LocalDateTime.class)
                    )
            );
            statInvoice.setDue(
                    TypeUtil.temporalToString(
                            rs.getObject(INVOICE_DATA.INVOICE_DUE.getName(), LocalDateTime.class)
                    )
            );

            com.rbkmoney.magista.domain.enums.InvoiceStatus invoiceStatusType = TypeUtil.toEnumField(
                    rs.getString(INVOICE_EVENT.INVOICE_STATUS.getName()),
                    com.rbkmoney.magista.domain.enums.InvoiceStatus.class
            );

            String eventCreatedAtString = TypeUtil.temporalToString(
                    rs.getObject(INVOICE_EVENT.EVENT_CREATED_AT.getName(), LocalDateTime.class)
            );

            InvoiceStatus invoiceStatus;
            switch (invoiceStatusType) {
                case cancelled:
                    InvoiceCancelled invoiceCancelled = new InvoiceCancelled();
                    invoiceCancelled.setDetails(rs.getString(INVOICE_EVENT.INVOICE_STATUS_DETAILS.getName()));
                    invoiceCancelled.setAt(eventCreatedAtString);
                    invoiceStatus = InvoiceStatus.cancelled(invoiceCancelled);
                    break;
                case unpaid:
                    invoiceStatus = InvoiceStatus.unpaid(new InvoiceUnpaid());
                    break;
                case paid:
                    InvoicePaid invoicePaid = new InvoicePaid();
                    invoicePaid.setAt(eventCreatedAtString);
                    invoiceStatus = InvoiceStatus.paid(invoicePaid);
                    break;
                case fulfilled:
                    InvoiceFulfilled invoiceFulfilled = new InvoiceFulfilled();
                    invoiceFulfilled.setAt(eventCreatedAtString);
                    invoiceFulfilled.setDetails(rs.getString(INVOICE_EVENT.INVOICE_STATUS_DETAILS.getName()));
                    invoiceStatus = InvoiceStatus.fulfilled(invoiceFulfilled);
                    break;
                default:
                    throw new NotFoundException(String.format("Invoice status '%s' not found", invoiceStatusType.getLiteral()));
            }
            statInvoice.setStatus(invoiceStatus);

            String invoiceCartJson = rs.getString(INVOICE_DATA.INVOICE_CART_JSON.getName());
            if (invoiceCartJson != null) {
                statInvoice.setCart(DamselUtil.fromJson(invoiceCartJson, InvoiceCart.class));
            }

            byte[] context = rs.getBytes(INVOICE_DATA.INVOICE_CONTEXT.getName());
            if (context != null) {
                statInvoice.setContext(
                        new Content(
                                rs.getString(INVOICE_DATA.INVOICE_CONTEXT_TYPE.getName()),
                                ByteBuffer.wrap(context)
                        )
                );
            }

            return new AbstractMap.SimpleEntry<>(rs.getLong(INVOICE_DATA.ID.getName()), statInvoice);
        });
    }

    @Override
    public Collection<Map.Entry<Long, StatPayment>> getPayments(
            PaymentsFunction.PaymentsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            Optional<Integer> limit
    ) throws DaoException {
        Query query = buildPaymentSelectConditionStepQuery(
                parameters,
                fromTime,
                toTime,
                fromId
        ).orderBy(PAYMENT_DATA.PAYMENT_CREATED_AT.desc())
                .limit(Math.min(limit.orElse(MAX_LIMIT), MAX_LIMIT));
        return fetch(query, (rs, i) -> {
            StatPayment statPayment = new StatPayment();
            statPayment.setId(rs.getString(PAYMENT_DATA.PAYMENT_ID.getName()));
            statPayment.setInvoiceId(rs.getString(PAYMENT_DATA.INVOICE_ID.getName()));
            statPayment.setOwnerId(rs.getString(PAYMENT_DATA.PARTY_ID.getName()));
            statPayment.setShopId(rs.getString(PAYMENT_DATA.PARTY_SHOP_ID.getName()));
            statPayment.setAmount(rs.getLong(PAYMENT_DATA.PAYMENT_AMOUNT.getName()));
            statPayment.setFee(rs.getLong(PAYMENT_EVENT.PAYMENT_FEE.getName()));
            statPayment.setCurrencySymbolicCode(rs.getString(PAYMENT_DATA.PAYMENT_CURRENCY_CODE.getName()));
            statPayment.setCreatedAt(
                    TypeUtil.temporalToString(
                            rs.getObject(PAYMENT_DATA.PAYMENT_CREATED_AT.getName(), LocalDateTime.class)
                    )
            );


            String eventCreatedAtString = TypeUtil.temporalToString(
                    rs.getObject(PAYMENT_EVENT.EVENT_CREATED_AT.getName(), LocalDateTime.class)
            );
            com.rbkmoney.magista.domain.enums.InvoicePaymentStatus invoicePaymentStatus = TypeUtil.toEnumField(
                    rs.getString(PAYMENT_EVENT.PAYMENT_STATUS.getName()),
                    com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class
            );

            InvoicePaymentStatus paymentStatus;
            switch (invoicePaymentStatus) {
                case pending:
                    paymentStatus = InvoicePaymentStatus.pending(new InvoicePaymentPending());
                    break;
                case cancelled:
                    InvoicePaymentCancelled invoicePaymentCancelled = new InvoicePaymentCancelled();
                    invoicePaymentCancelled.setAt(eventCreatedAtString);
                    paymentStatus = InvoicePaymentStatus.cancelled(invoicePaymentCancelled);
                    break;
                case failed:
                    InvoicePaymentFailed invoicePaymentFailed = new InvoicePaymentFailed();
                    invoicePaymentFailed.setAt(eventCreatedAtString);
                    OperationFailure operationFailure = DamselUtil.toOperationFailure(
                            TypeUtil.toEnumField(rs.getString(PAYMENT_EVENT.PAYMENT_OPERATION_FAILURE_CLASS.getName()), FailureClass.class),
                            rs.getString(PAYMENT_EVENT.PAYMENT_EXTERNAL_FAILURE.getName()),
                            rs.getString(PAYMENT_EVENT.PAYMENT_EXTERNAL_FAILURE_REASON.getName())
                    );
                    invoicePaymentFailed.setFailure(operationFailure);
                    paymentStatus = InvoicePaymentStatus.failed(invoicePaymentFailed);
                    break;
                case captured:
                    InvoicePaymentCaptured invoicePaymentCaptured = new InvoicePaymentCaptured();
                    invoicePaymentCaptured.setAt(eventCreatedAtString);
                    paymentStatus = InvoicePaymentStatus.captured(invoicePaymentCaptured);
                    break;
                case refunded:
                    InvoicePaymentRefunded invoicePaymentRefunded = new InvoicePaymentRefunded();
                    invoicePaymentRefunded.setAt(eventCreatedAtString);
                    paymentStatus = InvoicePaymentStatus.refunded(invoicePaymentRefunded);
                    break;
                case processed:
                    InvoicePaymentProcessed invoicePaymentProcessed = new InvoicePaymentProcessed();
                    invoicePaymentProcessed.setAt(eventCreatedAtString);
                    paymentStatus = InvoicePaymentStatus.processed(invoicePaymentProcessed);
                    break;
                default:
                    throw new NotFoundException(String.format("Payment status '%s' not found", invoicePaymentStatus.getLiteral()));
            }
            statPayment.setStatus(paymentStatus);

            String customerId = rs.getString(PAYMENT_DATA.PAYMENT_CUSTOMER_ID.getName());
            if (customerId != null) {
                statPayment.setPayer(Payer.customer(new CustomerPayer(customerId)));
            } else {
                PaymentResourcePayer paymentResourcePayer = new PaymentResourcePayer();
                paymentResourcePayer.setIpAddress(rs.getString(PAYMENT_DATA.PAYMENT_IP.getName()));
                paymentResourcePayer.setFingerprint(rs.getString(PAYMENT_DATA.PAYMENT_FINGERPRINT.getName()));
                paymentResourcePayer.setPhoneNumber(rs.getString(PAYMENT_DATA.PAYMENT_PHONE_NUMBER.getName()));
                paymentResourcePayer.setEmail(rs.getString(PAYMENT_DATA.PAYMENT_EMAIL.getName()));
                paymentResourcePayer.setSessionId(rs.getString(PAYMENT_DATA.PAYMENT_SESSION_ID.getName()));


                com.rbkmoney.magista.domain.enums.PaymentTool paymentToolType = TypeUtil.toEnumField(
                        rs.getString(PAYMENT_DATA.PAYMENT_TOOL.getName()),
                        com.rbkmoney.magista.domain.enums.PaymentTool.class
                );

                PaymentTool paymentTool;
                switch (paymentToolType) {
                    case bank_card:
                        BankCard bankCard = new BankCard(
                                rs.getString(PAYMENT_DATA.PAYMENT_BANK_CARD_TOKEN.getName()),
                                TypeUtil.toEnumField(rs.getString(PAYMENT_DATA.PAYMENT_BANK_CARD_SYSTEM.getName()), BankCardPaymentSystem.class),
                                rs.getString(PAYMENT_DATA.PAYMENT_BANK_CARD_BIN.getName()),
                                rs.getString(PAYMENT_DATA.PAYMENT_BANK_CARD_MASKED_PAN.getName())
                        );
                        bankCard.setTokenProvider(
                                Optional.ofNullable(rs.getString(PAYMENT_DATA.PAYMENT_BANK_CARD_TOKEN_PROVIDER.getName()))
                                        .map(bankCardTokenProvider -> TypeUtil.toEnumField(bankCardTokenProvider, BankCardTokenProvider.class))
                                        .orElse(null)
                        );
                        paymentTool = PaymentTool.bank_card(bankCard);
                        break;
                    case payment_terminal:
                        paymentTool = PaymentTool.payment_terminal(new PaymentTerminal(
                                TypeUtil.toEnumField(rs.getString(PAYMENT_DATA.PAYMENT_TERMINAL_PROVIDER.getName()), TerminalPaymentProvider.class)
                        ));
                        break;
                    case digital_wallet:
                        paymentTool = PaymentTool.digital_wallet(new DigitalWallet(
                                TypeUtil.toEnumField(rs.getString(PAYMENT_DATA.PAYMENT_DIGITAL_WALLET_PROVIDER.getName()), DigitalWalletProvider.class),
                                rs.getString(PAYMENT_DATA.PAYMENT_DIGITAL_WALLET_ID.getName())
                        ));
                        break;
                    default:
                        throw new NotFoundException(String.format("Payment tool '%s' not found", paymentToolType.getLiteral()));
                }
                paymentResourcePayer.setPaymentTool(paymentTool);

                statPayment.setPayer(Payer.payment_resource(paymentResourcePayer));
            }

            PaymentFlow paymentFlow = TypeUtil.toEnumField(rs.getString(PAYMENT_DATA.PAYMENT_FLOW.getName()), PaymentFlow.class);
            switch (paymentFlow) {
                case hold:
                    InvoicePaymentFlowHold invoicePaymentFlowHold = new InvoicePaymentFlowHold(
                            TypeUtil.toEnumField(rs.getString(PAYMENT_DATA.PAYMENT_HOLD_ON_EXPIRATION.getName()), OnHoldExpiration.class),
                            TypeUtil.temporalToString(
                                    rs.getObject(PAYMENT_DATA.PAYMENT_HOLD_UNTIL.getName(), LocalDateTime.class)
                            )
                    );
                    statPayment.setFlow(InvoicePaymentFlow.hold(invoicePaymentFlowHold));
                    break;
                case instant:
                    statPayment.setFlow(InvoicePaymentFlow.instant(new InvoicePaymentFlowInstant()));
                    break;
                default:
                    throw new NotFoundException(String.format("Payment flow '%s' not found", paymentFlow.getLiteral()));
            }
            statPayment.setShortId(rs.getString(PAYMENT_EVENT.PAYMENT_SHORT_ID.getName()));

            byte[] context = rs.getBytes(PAYMENT_DATA.PAYMENT_CONTEXT.getName());
            if (context != null) {
                statPayment.setContext(
                        new Content(
                                rs.getString(PAYMENT_DATA.PAYMENT_CONTEXT_TYPE.getName()),
                                ByteBuffer.wrap(context)
                        )
                );
            }

            return new AbstractMap.SimpleEntry<>(rs.getLong(PAYMENT_DATA.ID.getName()), statPayment);
        });
    }

    @Override
    public Collection<Refund> getRefunds(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<String> contractId,
            ConditionParameterSource parameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException {
        Query query = getDslContext().selectFrom(REFUND)
                .where(buildRefundCondition(merchantId, shopId, contractId, parameterSource, fromTime, toTime))
                .orderBy(REFUND.REFUND_CREATED_AT.desc())
                .limit(Math.min(limit.orElse(MAX_LIMIT), MAX_LIMIT))
                .offset(offset.orElse(0));
        return fetch(query, (rs, i) -> {
            Refund refund = new Refund();
            refund.setEventCreatedAt(rs.getObject(REFUND.EVENT_CREATED_AT.getName(), LocalDateTime.class));
            refund.setPartyId(rs.getString(REFUND.PARTY_ID.getName()));
            refund.setPartyShopId(rs.getString(REFUND.PARTY_SHOP_ID.getName()));
            refund.setInvoiceId(rs.getString(REFUND.INVOICE_ID.getName()));
            refund.setPaymentId(rs.getString(REFUND.PAYMENT_ID.getName()));
            refund.setRefundId(rs.getString(REFUND.REFUND_ID.getName()));
            refund.setRefundCurrencyCode(rs.getString(REFUND.REFUND_CURRENCY_CODE.getName()));
            refund.setRefundAmount(rs.getLong(REFUND.REFUND_AMOUNT.getName()));
            refund.setRefundFee(rs.getLong(REFUND.REFUND_FEE.getName()));
            refund.setRefundProviderFee(rs.getLong(REFUND.REFUND_PROVIDER_FEE.getName()));
            refund.setRefundExternalFee(rs.getLong(REFUND.REFUND_EXTERNAL_FEE.getName()));
            refund.setRefundReason(rs.getString(REFUND.REFUND_REASON.getName()));
            refund.setRefundStatus(TypeUtil.toEnumField(rs.getString(REFUND.REFUND_STATUS.getName()), RefundStatus.class));
            refund.setRefundOperationFailureClass(
                    TypeUtil.toEnumField(rs.getString(REFUND.REFUND_OPERATION_FAILURE_CLASS.getName()), FailureClass.class)
            );
            refund.setRefundExternalFailure(rs.getString(REFUND.REFUND_EXTERNAL_FAILURE.getName()));
            refund.setRefundExternalFailureReason(rs.getString(REFUND.REFUND_EXTERNAL_FAILURE_REASON.getName()));
            refund.setRefundCreatedAt(rs.getObject(REFUND.REFUND_CREATED_AT.getName(), LocalDateTime.class));
            return refund;
        });
    }

    @Override
    public Integer getRefundsCount(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<String> contractId,
            ConditionParameterSource parameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) throws DaoException {
        Query query = getDslContext().select(DSL.count()).from(REFUND)
                .where(buildRefundCondition(merchantId, shopId, contractId, parameterSource, fromTime, toTime));
        return fetchOne(query, Integer.class);
    }

    @Override
    public Collection<PayoutEventStat> getPayouts(
            Optional<String> merchantId,
            Optional<String> shopId,
            ConditionParameterSource parameterSource,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException {
        Query query = buildPayoutSelectConditionStepQuery(parameterSource)
                .orderBy(PAYOUT_EVENT_STAT.PAYOUT_CREATED_AT.desc())
                .limit(Math.min(limit.orElse(MAX_LIMIT), MAX_LIMIT))
                .offset(offset.orElse(0));

        return fetch(query, PayoutEventDaoImpl.ROW_MAPPER);
    }

    @Override
    public Integer getPayoutsCount(
            Optional<String> merchantId,
            Optional<String> shopId,
            ConditionParameterSource parameterSource
    ) throws DaoException {
        Query query = buildPayoutSelectConditionStepQuery(parameterSource, DSL.count());
        return fetchOne(query, Integer.class);
    }

    @Override
    public Collection<Map<String, String>> getPaymentsTurnoverStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT payment_currency_code AS currency_symbolic_code, SUM(payment_amount - payment_fee) AS amount_with_fee, SUM(payment_amount) AS amount_without_fee, trunc(EXTRACT(EPOCH FROM (payment_created_at - (:from_time::TIMESTAMP))) / EXTRACT(EPOCH FROM INTERVAL '" + splitInterval + "  sec')) AS sp_val FROM mst.invoice_event_stat WHERE id IN (select max(id) from mst.invoice_event_stat WHERE event_category = 'PAYMENT' :: mst.INVOICE_EVENT_CATEGORY AND party_shop_id = :shop_id AND party_id = :merchant_id AND payment_created_at >= :from_time AND payment_created_at < :to_time GROUP BY invoice_id, payment_id) AND payment_status = :succeeded_status::mst.invoice_payment_status GROUP BY sp_val, payment_currency_code ORDER BY sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        return fetch(sql, params, (rs, i) -> {
            Map<String, String> map = new HashMap<>();
            map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
            map.put("currency_symbolic_code", rs.getString("currency_symbolic_code"));
            map.put("amount_with_fee", rs.getString("amount_with_fee"));
            map.put("amount_without_fee", rs.getString("amount_without_fee"));
            return map;
        });
    }

    @Override
    public Collection<Map<String, String>> getPaymentsGeoStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT payment_city_id as city_id, payment_country_id as country_id, payment_currency_code as currency_symbolic_code, SUM(payment_amount - payment_fee) as amount_with_fee, SUM(payment_amount) as amount_without_fee, trunc(EXTRACT(epoch FROM (payment_created_at - (:from_time::timestamp))) / EXTRACT(epoch FROM INTERVAL '" + splitInterval + "  sec')) AS sp_val FROM mst.invoice_event_stat where id IN (SELECT max(id) FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT' :: mst.INVOICE_EVENT_CATEGORY AND party_shop_id = :shop_id AND party_id = :merchant_id AND payment_created_at >= :from_time AND payment_created_at < :to_time AND payment_city_id NOTNULL AND payment_country_id NOTNULL GROUP BY invoice_id, payment_id) AND payment_status = :succeeded_status :: mst.INVOICE_PAYMENT_STATUS  group by sp_val, payment_city_id, payment_country_id, payment_currency_code order by sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        return fetch(sql, params, (rs, i) -> {
            Map<String, String> map = new HashMap<>();
            map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
            map.put("city_id", rs.getString("city_id"));
            map.put("country_id", rs.getString("country_id"));
            map.put("currency_symbolic_code", rs.getString("currency_symbolic_code"));
            map.put("amount_with_fee", rs.getString("amount_with_fee"));
            map.put("amount_without_fee", rs.getString("amount_without_fee"));
            return map;
        });
    }

    @Override
    public Collection<Map<String, String>> getPaymentsConversionStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT t.*, t.successful_count::FLOAT / greatest(t.total_count, 1) AS conversion FROM (SELECT SUM(CASE WHEN (payment_status = :succeeded_status::mst.invoice_payment_status OR payment_status = :failed_status::mst.invoice_payment_status) THEN 1 ELSE 0 END) AS total_count, SUM(CASE WHEN event_category = 'PAYMENT'::mst.invoice_event_category and payment_status = :succeeded_status::mst.invoice_payment_status THEN 1 ELSE 0 END) AS successful_count, trunc(EXTRACT(EPOCH FROM (payment_created_at - (:from_time::TIMESTAMP))) / EXTRACT(EPOCH FROM INTERVAL '" + splitInterval + "  sec')) AS sp_val FROM mst.invoice_event_stat WHERE id IN (SELECT max(id) from mst.invoice_event_stat WHERE party_shop_id = :shop_id AND party_id = :merchant_id AND payment_created_at >= :from_time AND payment_created_at < :to_time GROUP BY invoice_id , payment_id) GROUP BY sp_val ORDER BY sp_val) AS t";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        params.addValue("failed_status", InvoicePaymentStatus._Fields.FAILED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        return fetch(sql, params, (rs, i) -> {
            Map<String, String> map = new HashMap<>();
            map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
            map.put("conversion", rs.getString("conversion"));
            map.put("total_count", rs.getString("total_count"));
            map.put("successful_count", rs.getString("successful_count"));
            return map;
        });
    }

    @Override
    public Collection<Map<String, String>> getCustomersRateStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT count(payment_fingerprint) AS unic_count, trunc(EXTRACT(EPOCH FROM (payment_created_at - (:from_time::TIMESTAMP))) / EXTRACT(EPOCH FROM INTERVAL '" + splitInterval + " sec')) AS sp_val FROM mst.invoice_event_stat WHERE id IN (select max(id) from mst.invoice_event_stat WHERE event_category = 'PAYMENT' :: mst.INVOICE_EVENT_CATEGORY AND party_shop_id = :shop_id AND party_id = :merchant_id AND payment_created_at >= :from_time AND payment_created_at < :to_time GROUP BY invoice_id, payment_id) GROUP BY sp_val ORDER BY sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        log.trace("SQL: {}, Params: {}", sql, params);
        return fetch(sql, params, (rs, i) -> {
            Map<String, String> map = new HashMap<>();
            map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
            map.put("unic_count", rs.getString("unic_count"));
            return map;
        });
    }

    @Override
    public Collection<Map<String, String>> getPaymentsCardTypesStat(String merchantId, String shopId, Instant fromTime, Instant toTime, int splitInterval) throws DaoException {
        String sql = "SELECT count(payment_system) AS total_count, payment_system AS payment_system, SUM(payment_amount - payment_fee) AS amount_with_fee, SUM(payment_amount) AS amount_without_fee, trunc(EXTRACT(EPOCH FROM (payment_created_at - (:from_time::TIMESTAMP))) / EXTRACT(EPOCH FROM INTERVAL '" + splitInterval + "  sec')) AS sp_val FROM mst.invoice_event_stat WHERE id IN (SELECT max(id) FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT' :: mst.INVOICE_EVENT_CATEGORY AND payment_system NOTNULL AND party_shop_id = :shop_id AND party_id = :merchant_id AND payment_created_at >= :from_time AND payment_created_at < :to_time GROUP BY invoice_id, payment_id) GROUP BY sp_val, payment_system ORDER BY sp_val";
        MapSqlParameterSource params = createParamsMap(merchantId, shopId, fromTime, toTime, splitInterval);
        params.addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName());
        log.trace("SQL: {}, Params: {}", sql, params);
        return fetch(sql, params, (rs, i) -> {
            Map<String, String> map = new HashMap<>();
            map.put("offset", (rs.getLong("sp_val") * splitInterval) + "");
            map.put("total_count", rs.getString("total_count"));
            map.put("payment_system", rs.getString("payment_system"));
            map.put("amount_with_fee", rs.getString("amount_with_fee"));
            map.put("amount_without_fee", rs.getString("amount_without_fee"));
            return map;
        });
    }

    @Override
    public Map<String, String> getPaymentAccountingData(String merchantId, String contractId, String currencyCode, Optional<LocalDateTime> fromTime, LocalDateTime toTime) throws DaoException {
        Query query = getDslContext().select(
                INVOICE_EVENT_STAT.PARTY_ID.as("merchant_id"),
                INVOICE_EVENT_STAT.PARTY_CONTRACT_ID.as("contract_id"),
                INVOICE_EVENT_STAT.PAYMENT_CURRENCY_CODE.as("currency_code"),
                DSL.sum(INVOICE_EVENT_STAT.PAYMENT_AMOUNT).as("funds_acquired"),
                DSL.sum(INVOICE_EVENT_STAT.PAYMENT_FEE).as("fee_charged")
        ).from(INVOICE_EVENT_STAT).where(
                appendDateTimeRangeConditions(
                        INVOICE_EVENT_STAT.EVENT_CATEGORY.eq(InvoiceEventCategory.PAYMENT)
                                .and(INVOICE_EVENT_STAT.PARTY_ID.eq(merchantId))
                                .and(INVOICE_EVENT_STAT.PARTY_CONTRACT_ID.eq(contractId))
                                .and(INVOICE_EVENT_STAT.PAYMENT_CURRENCY_CODE.eq(currencyCode))
                                .and(INVOICE_EVENT_STAT.PAYMENT_STATUS.eq(com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.captured))
                                .and(INVOICE_EVENT_STAT.EVENT_TYPE.ne(InvoiceEventType.INVOICE_PAYMENT_ADJUSTED)),
                        INVOICE_EVENT_STAT.EVENT_CREATED_AT,
                        fromTime,
                        Optional.of(toTime)
                )
        ).groupBy(
                INVOICE_EVENT_STAT.PARTY_ID,
                INVOICE_EVENT_STAT.PARTY_CONTRACT_ID,
                INVOICE_EVENT_STAT.PAYMENT_CURRENCY_CODE
        );

        return Optional.ofNullable(
                fetchOne(query, (rs, i) -> ImmutableMap.<String, String>builder()
                        .put("merchant_id", rs.getString("merchant_id"))
                        .put("contract_id", rs.getString("contract_id"))
                        .put("currency_code", rs.getString("currency_code"))
                        .put("funds_acquired", rs.getString("funds_acquired"))
                        .put("fee_charged", rs.getString("fee_charged"))
                        .build()
                )
        ).orElse(
                ImmutableMap.<String, String>builder()
                        .put("merchant_id", merchantId)
                        .put("contract_id", contractId)
                        .put("currency_code", currencyCode)
                        .put("funds_acquired", "0")
                        .put("fee_charged", "0")
                        .build()
        );
    }

    @Override
    public Map<String, String> getRefundAccountingData(String merchantId, String contractId, String currencyCode, Optional<LocalDateTime> fromTime, LocalDateTime toTime) throws DaoException {
        Query query = getDslContext().select(
                REFUND.PARTY_ID.as("merchant_id"),
                REFUND.PARTY_CONTRACT_ID.as("contract_id"),
                REFUND.REFUND_CURRENCY_CODE.as("currency_code"),
                DSL.sum(REFUND.REFUND_AMOUNT.minus(REFUND.REFUND_FEE)).as("funds_refunded")
        ).from(REFUND).where(
                appendDateTimeRangeConditions(
                        REFUND.PARTY_ID.eq(merchantId)
                                .and(REFUND.PARTY_CONTRACT_ID.eq(contractId))
                                .and(REFUND.REFUND_CURRENCY_CODE.eq(currencyCode))
                                .and(REFUND.REFUND_STATUS.eq(RefundStatus.succeeded)),
                        REFUND.EVENT_CREATED_AT,
                        fromTime,
                        Optional.of(toTime)
                )
        ).groupBy(
                REFUND.PARTY_ID,
                REFUND.PARTY_CONTRACT_ID,
                REFUND.REFUND_CURRENCY_CODE
        );

        return Optional.ofNullable(
                fetchOne(query, (rs, i) -> ImmutableMap.<String, String>builder()
                        .put("merchant_id", rs.getString("merchant_id"))
                        .put("contract_id", rs.getString("contract_id"))
                        .put("currency_code", rs.getString("currency_code"))
                        .put("funds_refunded", rs.getString("funds_refunded"))
                        .build())
        ).orElse(
                ImmutableMap.<String, String>builder()
                        .put("merchant_id", merchantId)
                        .put("contract_id", contractId)
                        .put("currency_code", currencyCode)
                        .put("funds_refunded", "0")
                        .build()
        );
    }

    @Override
    public Map<String, String> getAdjustmentAccountingData(String merchantId, String contractId, String currencyCode, Optional<LocalDateTime> fromTime, LocalDateTime toTime) throws DaoException {
        Query query = getDslContext().select(
                ADJUSTMENT.PARTY_ID.as("merchant_id"),
                ADJUSTMENT.PARTY_CONTRACT_ID.as("contract_id"),
                INVOICE_EVENT_STAT.PAYMENT_CURRENCY_CODE.as("currency_code"),
                DSL.sum(INVOICE_EVENT_STAT.PAYMENT_FEE.minus(ADJUSTMENT.ADJUSTMENT_FEE)).as("funds_adjusted")
        ).from(ADJUSTMENT)
                .join(INVOICE_EVENT_STAT)
                .on(
                        appendDateTimeRangeConditions(
                                ADJUSTMENT.PARTY_ID.eq(merchantId)
                                        .and(ADJUSTMENT.PARTY_CONTRACT_ID.eq(contractId))
                                        .and(ADJUSTMENT.INVOICE_ID.eq(INVOICE_EVENT_STAT.INVOICE_ID))
                                        .and(ADJUSTMENT.PAYMENT_ID.eq(INVOICE_EVENT_STAT.PAYMENT_ID))
                                        .and(ADJUSTMENT.ADJUSTMENT_STATUS.eq(AdjustmentStatus.captured))
                                        .and(INVOICE_EVENT_STAT.EVENT_CATEGORY.eq(InvoiceEventCategory.PAYMENT))
                                        .and(INVOICE_EVENT_STAT.EVENT_TYPE.ne(InvoiceEventType.INVOICE_PAYMENT_ADJUSTED))
                                        .and(INVOICE_EVENT_STAT.PAYMENT_CURRENCY_CODE.eq(currencyCode))
                                        .and(INVOICE_EVENT_STAT.PAYMENT_STATUS.eq(com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.captured)),
                                ADJUSTMENT.EVENT_CREATED_AT,
                                fromTime,
                                Optional.of(toTime)
                        )
                ).groupBy(
                        ADJUSTMENT.PARTY_ID,
                        ADJUSTMENT.PARTY_CONTRACT_ID,
                        INVOICE_EVENT_STAT.PAYMENT_CURRENCY_CODE
                );
        System.out.println(query.getSQL(ParamType.INLINED));
        return Optional.ofNullable(
                fetchOne(query, (rs, i) -> ImmutableMap.<String, String>builder()
                        .put("merchant_id", rs.getString("merchant_id"))
                        .put("contract_id", rs.getString("contract_id"))
                        .put("currency_code", rs.getString("currency_code"))
                        .put("funds_adjusted", rs.getString("funds_adjusted"))
                        .build())
        ).orElse(
                ImmutableMap.<String, String>builder()
                        .put("merchant_id", merchantId)
                        .put("contract_id", contractId)
                        .put("currency_code", currencyCode)
                        .put("funds_adjusted", "0")
                        .build()
        );
    }

    @Override
    public Map<String, String> getPayoutAccountingData(String merchantId, String contractId, String currencyCode, Optional<LocalDateTime> fromTime, LocalDateTime toTime) throws DaoException {
        Field<String> merchantIdField = DSL.field("merchant_id", String.class);
        Field<String> contractIdField = DSL.field("contract_id", String.class);
        Field<String> currencyCodeField = DSL.field("currency_code", String.class);
        Field<Long> fundsPaidOutField = DSL.field("funds_paid_out", Long.class);
        Field<Long> paidFundsField = DSL.field("paid_funds", Long.class);
        Field<Long> cancelledFundsField = DSL.field("cancelled_funds", Long.class);

        Query query = getDslContext().select(
                merchantIdField,
                contractIdField,
                currencyCodeField,
                paidFundsField.minus(DSL.coalesce(cancelledFundsField, 0)).as(fundsPaidOutField)
        ).from(
                getDslContext().select(
                        PAYOUT_EVENT_STAT.PARTY_ID.as(merchantIdField),
                        DSL.value(contractId).as(contractIdField),
                        PAYOUT_EVENT_STAT.PAYOUT_CURRENCY_CODE.as(currencyCodeField),
                        DSL.sum(
                                PAYOUT_EVENT_STAT.PAYOUT_AMOUNT
                                        .minus(DSL.coalesce(PAYOUT_EVENT_STAT.PAYOUT_FEE, 0))
                        ).as(paidFundsField)
                ).from(PAYOUT_EVENT_STAT).where(
                        appendDateTimeRangeConditions(
                                PAYOUT_EVENT_STAT.PARTY_ID.eq(merchantId)
                                        .and(PAYOUT_EVENT_STAT.PARTY_SHOP_ID.in(
                                                getDslContext().select(INVOICE_EVENT_STAT.PARTY_SHOP_ID)
                                                        .from(INVOICE_EVENT_STAT)
                                                        .where(INVOICE_EVENT_STAT.PARTY_CONTRACT_ID.eq(contractId))
                                                        .groupBy(INVOICE_EVENT_STAT.PARTY_SHOP_ID)
                                        ))
                                        .and(PAYOUT_EVENT_STAT.PAYOUT_STATUS.eq(PayoutStatus.paid))
                                        .and(PAYOUT_EVENT_STAT.PAYOUT_CURRENCY_CODE.eq(currencyCode)),
                                PAYOUT_EVENT_STAT.EVENT_CREATED_AT,
                                fromTime,
                                Optional.of(toTime)
                        )
                ).groupBy(
                        PAYOUT_EVENT_STAT.PARTY_ID,
                        PAYOUT_EVENT_STAT.PAYOUT_CURRENCY_CODE
                ).asTable().leftJoin(
                        getDslContext().select(
                                DSL.sum(
                                        PAYOUT_EVENT_STAT.PAYOUT_AMOUNT
                                                .minus(DSL.coalesce(PAYOUT_EVENT_STAT.PAYOUT_FEE, 0))
                                ).as(cancelledFundsField)
                        ).from(PAYOUT_EVENT_STAT).where(
                                appendDateTimeRangeConditions(
                                        PAYOUT_EVENT_STAT.PARTY_SHOP_ID.in(
                                                getDslContext().select(INVOICE_EVENT_STAT.PARTY_SHOP_ID)
                                                        .from(INVOICE_EVENT_STAT)
                                                        .where(INVOICE_EVENT_STAT.PARTY_CONTRACT_ID.eq(contractId))
                                                        .groupBy(INVOICE_EVENT_STAT.PARTY_SHOP_ID)
                                        )
                                                .and(PAYOUT_EVENT_STAT.PAYOUT_STATUS.eq(PayoutStatus.cancelled))
                                                .and(PAYOUT_EVENT_STAT.PAYOUT_ID.in(
                                                        getDslContext().select(PAYOUT_EVENT_STAT.PAYOUT_ID)
                                                                .from(PAYOUT_EVENT_STAT)
                                                                .where(
                                                                        PAYOUT_EVENT_STAT.PARTY_ID.eq(merchantId)
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
                                PAYOUT_EVENT_STAT.PAYOUT_CURRENCY_CODE
                        ).asTable()
                ).on()
        );

        return Optional.ofNullable(
                fetchOne(query, (rs, i) -> ImmutableMap.<String, String>builder()
                        .put("merchant_id", rs.getString("merchant_id"))
                        .put("contract_id", rs.getString("contract_id"))
                        .put("currency_code", rs.getString("currency_code"))
                        .put("funds_paid_out", rs.getString("funds_paid_out"))
                        .build())
        ).orElse(
                ImmutableMap.<String, String>builder()
                        .put("merchant_id", merchantId)
                        .put("contract_id", contractId)
                        .put("currency_code", currencyCode)
                        .put("funds_paid_out", "0")
                        .build()
        );
    }

    private MapSqlParameterSource createParamsMap(String merchantId, String shopId, Instant fromTime, Instant toTime, Integer splitInterval) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("merchant_id", merchantId);
        params.addValue("shop_id", shopId);
        params.addValue("from_time", LocalDateTime.ofInstant(fromTime, ZoneId.of("UTC")), Types.OTHER);
        params.addValue("to_time", LocalDateTime.ofInstant(toTime, ZoneId.of("UTC")), Types.OTHER);
        params.addValue("split_interval", splitInterval);
        return params;
    }

    private SelectOnConditionStep buildPaymentSelectConditionStepQuery(
            PaymentsFunction.PaymentsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            SelectField<?>... fields
    ) {
        PaymentEvent paymentEvent = PAYMENT_EVENT.as("payment_event");
        ConditionParameterSource conditionParameterSource = new ConditionParameterSource()
                .addValue(
                        PAYMENT_DATA.PARTY_ID,
                        Optional.ofNullable(parameters.getMerchantId())
                                .map(merchantId -> UUID.fromString(merchantId))
                                .orElse(null),
                        EQUALS
                )
                .addValue(PAYMENT_DATA.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                .addValue(PAYMENT_DATA.PARTY_CONTRACT_ID, parameters.getContractId(), EQUALS)
                .addValue(PAYMENT_DATA.INVOICE_ID, parameters.getInvoiceId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_ID, parameters.getPaymentId(), EQUALS)
                .addValue(PAYMENT_DATA.ID, fromId.orElse(null), LESS)
                .addValue(paymentEvent.PAYMENT_STATUS,
                        toEnumField(parameters.getPaymentStatus(), com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FLOW,
                        toEnumField(parameters.getPaymentFlow(), PaymentFlow.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TOOL,
                        toEnumField(parameters.getPaymentMethod(), com.rbkmoney.magista.domain.enums.PaymentTool.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_TOKEN_PROVIDER,
                        toEnumField(parameters.getPaymentBankCardTokenProvider(), com.rbkmoney.magista.domain.enums.BankCardTokenProvider.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TERMINAL_PROVIDER, parameters.getPaymentTerminalProvider(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_AMOUNT, parameters.getPaymentAmount(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_EMAIL, parameters.getPaymentEmail(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_IP, parameters.getPaymentIp(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FINGERPRINT, parameters.getPaymentFingerprint(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_BIN, parameters.getPaymentBankCardBin(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_SYSTEM, parameters.getPaymentBankCardSystem(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_BIN, parameters.getPaymentBankCardBin(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_MASKED_PAN, parameters.getPaymentBankCardLastDigits(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_CUSTOMER_ID, parameters.getPaymentCustomerId(), EQUALS);

        return getDslContext()
                .select(fields)
                .from(PAYMENT_DATA)
                .join(
                        DSL.lateral(
                                getDslContext()
                                        .selectFrom(PAYMENT_EVENT)
                                        .where(
                                                appendDateTimeRangeConditions(PAYMENT_DATA.INVOICE_ID.eq(PAYMENT_EVENT.INVOICE_ID)
                                                                .and(PAYMENT_DATA.PAYMENT_ID.eq(PAYMENT_EVENT.PAYMENT_ID)),
                                                        PAYMENT_EVENT.EVENT_CREATED_AT,
                                                        fromTime,
                                                        toTime
                                                )
                                        ).orderBy(PAYMENT_EVENT.ID.desc())
                                        .limit(1)
                        ).as(paymentEvent)
                ).on(appendConditions(DSL.trueCondition(), Operator.AND, conditionParameterSource));
    }

    private SelectOnConditionStep buildInvoiceSelectConditionStepQuery(
            InvoicesFunction.InvoicesParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            SelectField<?>... fields
    ) {
        InvoiceEvent invoiceEvent = INVOICE_EVENT.as("invoice_event");

        SelectOnConditionStep selectOnConditionStep = getDslContext()
                .select(fields)
                .from(INVOICE_DATA)
                .join(
                        DSL.lateral(
                                getDslContext()
                                        .selectFrom(INVOICE_EVENT)
                                        .where(
                                                appendDateTimeRangeConditions(
                                                        INVOICE_DATA.INVOICE_ID.eq(INVOICE_EVENT.INVOICE_ID),
                                                        INVOICE_EVENT.EVENT_CREATED_AT,
                                                        fromTime,
                                                        toTime
                                                )
                                        ).orderBy(INVOICE_EVENT.ID.desc())
                                        .limit(1)
                        ).as(invoiceEvent)
                ).on(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND,
                                        new ConditionParameterSource()
                                                .addValue(INVOICE_DATA.PARTY_ID,
                                                        Optional.ofNullable(parameters.getMerchantId())
                                                                .map(merchantId -> UUID.fromString(merchantId))
                                                                .orElse(null),
                                                        EQUALS)
                                                .addValue(INVOICE_DATA.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                                                .addValue(INVOICE_DATA.PARTY_CONTRACT_ID, parameters.getContractId(), EQUALS)
                                                .addValue(INVOICE_DATA.INVOICE_ID, parameters.getInvoiceId(), EQUALS)
                                                .addValue(INVOICE_DATA.ID, fromId.orElse(null), LESS)
                                                .addValue(invoiceEvent.INVOICE_STATUS,
                                                        toEnumField(
                                                                parameters.getInvoiceStatus(),
                                                                com.rbkmoney.magista.domain.enums.InvoiceStatus.class
                                                        ),
                                                        EQUALS)
                                                .addValue(INVOICE_DATA.INVOICE_AMOUNT, parameters.getInvoiceAmount(), EQUALS)),
                                INVOICE_DATA.INVOICE_CREATED_AT,
                                fromTime,
                                toTime
                        )
                );

        PaymentData paymentData = PAYMENT_DATA.as("payment_data");
        PaymentEvent paymentEvent = PAYMENT_EVENT.as("payment_event");
        ConditionParameterSource paymentParameterSource = new ConditionParameterSource()
                .addValue(paymentData.PAYMENT_ID, parameters.getPaymentId(), EQUALS)
                .addValue(paymentData.PAYMENT_FLOW,
                        TypeUtil.toEnumField(parameters.getPaymentFlow(), PaymentFlow.class),
                        EQUALS)
                .addValue(paymentData.PAYMENT_TOOL,
                        TypeUtil.toEnumField(parameters.getPaymentMethod(), com.rbkmoney.magista.domain.enums.PaymentTool.class),
                        EQUALS)
                .addValue(paymentData.PAYMENT_TERMINAL_PROVIDER, parameters.getPaymentTerminalProvider(), EQUALS)
                .addValue(paymentData.PAYMENT_AMOUNT, parameters.getPaymentAmount(), EQUALS)
                .addValue(paymentData.PAYMENT_EMAIL, parameters.getPaymentEmail(), EQUALS)
                .addValue(paymentData.PAYMENT_IP, parameters.getPaymentIp(), EQUALS)
                .addValue(paymentData.PAYMENT_FINGERPRINT, parameters.getPaymentFingerprint(), EQUALS)
                .addValue(paymentData.PAYMENT_BANK_CARD_BIN, parameters.getPaymentBankCardBin(), EQUALS)
                .addValue(paymentData.PAYMENT_BANK_CARD_MASKED_PAN, parameters.getPaymentBankCardLastDigits(), EQUALS)
                .addValue(paymentData.PAYMENT_CUSTOMER_ID, parameters.getPaymentCustomerId(), EQUALS);

        if (!paymentParameterSource.getConditionFields().isEmpty()) {
            selectOnConditionStep = selectOnConditionStep.join(
                    DSL.lateral(
                            getDslContext()
                                    .select(paymentData.fields())
                                    .from(PAYMENT_DATA)
                                    .where(
                                            INVOICE_DATA.INVOICE_ID.eq(PAYMENT_DATA.INVOICE_ID)
                                    ).limit(1)
                    ).as(paymentData)
            ).on(
                    appendConditions(
                            INVOICE_DATA.INVOICE_ID.eq(paymentData.INVOICE_ID),
                            Operator.AND,
                            paymentParameterSource
                    )
            ).join(
                    DSL.lateral(
                            getDslContext()
                                    .select(paymentEvent.fields())
                                    .from(PAYMENT_EVENT)
                                    .where(
                                            paymentData.INVOICE_ID.eq(PAYMENT_EVENT.INVOICE_ID)
                                                    .and(paymentData.PAYMENT_ID.eq(PAYMENT_EVENT.PAYMENT_ID))
                                    ).orderBy(PAYMENT_EVENT.ID.desc())
                                    .limit(1)
                    ).as(paymentEvent)
            ).on(
                    Optional.ofNullable(parameters.getPaymentStatus())
                            .map(paymentStatusString -> paymentEvent.PAYMENT_STATUS.eq(
                                    TypeUtil.toEnumField(
                                            paymentStatusString,
                                            com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class
                                    )
                                    )
                            ).orElse(DSL.trueCondition())
            );
        }

        return selectOnConditionStep;
    }

    private Condition buildRefundCondition(
            Optional<String> merchantId,
            Optional<String> shopId,
            Optional<String> contractId,
            ConditionParameterSource parameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) {
        Condition condition = DSL.trueCondition();
        if (merchantId.isPresent()) {
            condition = condition.and(REFUND.PARTY_ID.eq(merchantId.get()));
        }
        if (shopId.isPresent()) {
            condition = condition.and(REFUND.PARTY_SHOP_ID.eq(shopId.get()));
        }

        if (contractId.isPresent()) {
            condition = condition.and(REFUND.PARTY_CONTRACT_ID.eq(contractId.get()));
        }
        condition = REFUND.ID.in(
                getDslContext().select(DSL.max(REFUND.ID)).from(REFUND)
                        .where(
                                appendDateTimeRangeConditions(
                                        condition,
                                        REFUND.EVENT_CREATED_AT,
                                        fromTime,
                                        toTime
                                )
                        ).groupBy(REFUND.INVOICE_ID, REFUND.PAYMENT_ID, REFUND.REFUND_ID)
        );
        return appendConditions(condition, Operator.AND, parameterSource);
    }

    private Condition appendDateTimeRangeConditions(Condition condition,
                                                    Field<LocalDateTime> field,
                                                    Optional<LocalDateTime> fromTime,
                                                    Optional<LocalDateTime> toTime) {
        if (fromTime.isPresent()) {
            condition = condition.and(field.ge(fromTime.get()));
        }

        if (toTime.isPresent()) {
            condition = condition.and(field.lt(toTime.get()));
        }
        return condition;
    }

    private SelectConditionStep buildPayoutSelectConditionStepQuery(
            ConditionParameterSource paymentParameterSource,
            SelectField<?>... fields) {
        Condition condition = PAYOUT_EVENT_STAT.EVENT_CATEGORY.eq(PayoutEventCategory.PAYOUT);

        condition = PAYOUT_EVENT_STAT.ID.in(
                getDslContext().select(DSL.max(PAYOUT_EVENT_STAT.ID)).from(PAYOUT_EVENT_STAT)
                        .where(condition).groupBy(PAYOUT_EVENT_STAT.PAYOUT_ID)
        );

        condition = appendConditions(condition, Operator.AND, paymentParameterSource);

        return getDslContext().select(fields).from(PAYOUT_EVENT_STAT)
                .where(condition);
    }

}
