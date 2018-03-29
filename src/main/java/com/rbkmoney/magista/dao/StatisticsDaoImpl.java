package com.rbkmoney.magista.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoiceStatus;
import com.rbkmoney.magista.domain.enums.PayoutEventCategory;
import com.rbkmoney.magista.domain.enums.RefundStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.domain.tables.pojos.Refund;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.util.TypeUtil;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.rbkmoney.magista.domain.tables.InvoiceEventStat.INVOICE_EVENT_STAT;
import static com.rbkmoney.magista.domain.tables.PayoutEventStat.PAYOUT_EVENT_STAT;
import static com.rbkmoney.magista.domain.tables.Refund.REFUND;

/**
 * Created by vpankrashkin on 10.08.16.
 */
public class StatisticsDaoImpl extends AbstractDao implements StatisticsDao {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final int MAX_LIMIT = 1000;

    private final Cache<Map.Entry<Condition, String>, List<Map.Entry<LocalDateTime, Integer>>> statCache;

    public StatisticsDaoImpl(DataSource ds, long cacheMaxSize, long expireTime) {
        super(ds);
        statCache = Caffeine.newBuilder()
                .maximumSize(cacheMaxSize)
                .expireAfterWrite(expireTime, TimeUnit.MILLISECONDS)
                .build();
    }

    @Override
    public Collection<InvoiceEventStat> getInvoices(
            Optional<String> merchantId,
            Optional<String> shopId,
            ConditionParameterSource invoiceParameterSource,
            ConditionParameterSource paymentParameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException {

        int limitValue = Math.min(limit.orElse(MAX_LIMIT), MAX_LIMIT);

        if (offset.isPresent()) {
            DateTimeRange dateTimeRange = getDateTimeRangeByOffset(
                    buildInvoiceCondition(merchantId, shopId, invoiceParameterSource, paymentParameterSource, fromTime, toTime),
                    fromTime,
                    toTime,
                    INVOICE_EVENT_STAT.EVENT_CREATED_AT,
                    offset.get(),
                    limitValue
            );

            fromTime = Optional.ofNullable(dateTimeRange.getFromTime());
            toTime = Optional.ofNullable(dateTimeRange.getToTime());
            offset = Optional.ofNullable(dateTimeRange.getOffset());
        }

        Query query = getDslContext().select(
                INVOICE_EVENT_STAT.PARTY_ID,
                INVOICE_EVENT_STAT.PARTY_SHOP_ID,
                INVOICE_EVENT_STAT.INVOICE_ID,
                INVOICE_EVENT_STAT.INVOICE_CREATED_AT,
                INVOICE_EVENT_STAT.INVOICE_STATUS,
                INVOICE_EVENT_STAT.INVOICE_STATUS_DETAILS,
                INVOICE_EVENT_STAT.INVOICE_PRODUCT,
                INVOICE_EVENT_STAT.INVOICE_DESCRIPTION,
                INVOICE_EVENT_STAT.INVOICE_DUE,
                INVOICE_EVENT_STAT.INVOICE_AMOUNT,
                INVOICE_EVENT_STAT.INVOICE_CURRENCY_CODE,
                INVOICE_EVENT_STAT.INVOICE_CART,
                INVOICE_EVENT_STAT.INVOICE_CONTEXT_TYPE,
                INVOICE_EVENT_STAT.INVOICE_CONTEXT
        ).from(INVOICE_EVENT_STAT)
                .where(buildInvoiceCondition(merchantId, shopId, invoiceParameterSource, paymentParameterSource, fromTime, toTime))
                .orderBy(INVOICE_EVENT_STAT.INVOICE_CREATED_AT.desc())
                .limit(limitValue)
                .offset(offset.orElse(0));
        return fetch(query, (rs, i) -> {
            InvoiceEventStat invoiceEventStat = new InvoiceEventStat();
            invoiceEventStat.setPartyId(rs.getString(INVOICE_EVENT_STAT.PARTY_ID.getName()));
            invoiceEventStat.setPartyShopId(rs.getString(INVOICE_EVENT_STAT.PARTY_SHOP_ID.getName()));
            invoiceEventStat.setInvoiceId(rs.getString(INVOICE_EVENT_STAT.INVOICE_ID.getName()));
            invoiceEventStat.setInvoiceCreatedAt(rs.getObject(INVOICE_EVENT_STAT.INVOICE_CREATED_AT.getName(), LocalDateTime.class));
            invoiceEventStat.setInvoiceStatus(TypeUtil.toEnumField(rs.getString(INVOICE_EVENT_STAT.INVOICE_STATUS.getName()), InvoiceStatus.class));
            invoiceEventStat.setInvoiceStatusDetails(rs.getString(INVOICE_EVENT_STAT.INVOICE_STATUS_DETAILS.getName()));
            invoiceEventStat.setInvoiceProduct(rs.getString(INVOICE_EVENT_STAT.INVOICE_PRODUCT.getName()));
            invoiceEventStat.setInvoiceDescription(rs.getString(INVOICE_EVENT_STAT.INVOICE_DESCRIPTION.getName()));
            invoiceEventStat.setInvoiceDue(rs.getObject(INVOICE_EVENT_STAT.INVOICE_DUE.getName(), LocalDateTime.class));
            invoiceEventStat.setInvoiceAmount(rs.getLong(INVOICE_EVENT_STAT.INVOICE_AMOUNT.getName()));
            invoiceEventStat.setInvoiceCurrencyCode(rs.getString(INVOICE_EVENT_STAT.INVOICE_CURRENCY_CODE.getName()));
            invoiceEventStat.setInvoiceCart(rs.getString(INVOICE_EVENT_STAT.INVOICE_CART.getName()));
            invoiceEventStat.setInvoiceContextType(rs.getString(INVOICE_EVENT_STAT.INVOICE_CONTEXT_TYPE.getName()));
            invoiceEventStat.setInvoiceContext(rs.getBytes(INVOICE_EVENT_STAT.INVOICE_CONTEXT.getName()));

            return invoiceEventStat;
        });
    }

    @Override
    public int getInvoicesCount(
            Optional<String> merchantId,
            Optional<String> shopId,
            ConditionParameterSource invoiceParameterSource,
            ConditionParameterSource paymentParameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) throws DaoException {
        Query query = getDslContext().select(DSL.count()).from(INVOICE_EVENT_STAT)
                .where(buildInvoiceCondition(merchantId, shopId, invoiceParameterSource, paymentParameterSource, fromTime, toTime));
        return fetchOne(query, Integer.class);
    }

    @Override
    public Collection<InvoiceEventStat> getPayments(
            Optional<String> merchantId,
            Optional<String> shopId,
            ConditionParameterSource parameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException {

        int limitValue = Math.min(limit.orElse(MAX_LIMIT), MAX_LIMIT);

        if (offset.isPresent()) {
            DateTimeRange dateTimeRange = getDateTimeRangeByOffset(
                    buildPaymentCondition(merchantId, shopId, parameterSource, fromTime, toTime),
                    fromTime,
                    toTime,
                    INVOICE_EVENT_STAT.EVENT_CREATED_AT,
                    offset.get(),
                    limitValue
            );
            fromTime = Optional.ofNullable(dateTimeRange.getFromTime());
            toTime = Optional.ofNullable(dateTimeRange.getToTime());
            offset = Optional.ofNullable(dateTimeRange.getOffset());
        }

        Query query = getDslContext().select(
                INVOICE_EVENT_STAT.PAYMENT_ID,
                INVOICE_EVENT_STAT.INVOICE_ID,
                INVOICE_EVENT_STAT.PARTY_ID,
                INVOICE_EVENT_STAT.PARTY_SHOP_ID,
                INVOICE_EVENT_STAT.PAYMENT_CREATED_AT,
                INVOICE_EVENT_STAT.PAYMENT_STATUS,
                INVOICE_EVENT_STAT.PAYMENT_FAILURE_CLASS,
                INVOICE_EVENT_STAT.PAYMENT_EXTERNAL_FAILURE_CODE,
                INVOICE_EVENT_STAT.PAYMENT_EXTERNAL_FAILURE_DESCRIPTION,
                INVOICE_EVENT_STAT.PAYMENT_AMOUNT,
                INVOICE_EVENT_STAT.PAYMENT_FEE,
                INVOICE_EVENT_STAT.PAYMENT_CURRENCY_CODE,
                INVOICE_EVENT_STAT.PAYMENT_TOOL,
                INVOICE_EVENT_STAT.PAYMENT_TOKEN,
                INVOICE_EVENT_STAT.PAYMENT_SYSTEM,
                INVOICE_EVENT_STAT.PAYMENT_BIN,
                INVOICE_EVENT_STAT.PAYMENT_MASKED_PAN,
                INVOICE_EVENT_STAT.PAYMENT_TERMINAL_PROVIDER,
                INVOICE_EVENT_STAT.PAYMENT_DIGITAL_WALLET_ID,
                INVOICE_EVENT_STAT.PAYMENT_DIGITAL_WALLET_PROVIDER,
                INVOICE_EVENT_STAT.PAYMENT_IP,
                INVOICE_EVENT_STAT.PAYMENT_FINGERPRINT,
                INVOICE_EVENT_STAT.PAYMENT_PHONE_NUMBER,
                INVOICE_EVENT_STAT.PAYMENT_EMAIL,
                INVOICE_EVENT_STAT.PAYMENT_SESSION_ID,
                INVOICE_EVENT_STAT.PAYMENT_CUSTOMER_ID,
                INVOICE_EVENT_STAT.PAYMENT_FLOW,
                INVOICE_EVENT_STAT.PAYMENT_HOLD_ON_EXPIRATION,
                INVOICE_EVENT_STAT.PAYMENT_HOLD_UNTIL,
                INVOICE_EVENT_STAT.PAYMENT_COUNTRY_ID,
                INVOICE_EVENT_STAT.PAYMENT_CITY_ID,
                INVOICE_EVENT_STAT.PAYMENT_CONTEXT_TYPE,
                INVOICE_EVENT_STAT.PAYMENT_CONTEXT
        ).from(INVOICE_EVENT_STAT)
                .where(buildPaymentCondition(merchantId, shopId, parameterSource, fromTime, toTime))
                .orderBy(INVOICE_EVENT_STAT.PAYMENT_CREATED_AT.desc())
                .limit(limitValue)
                .offset(offset.orElse(0));
        return fetch(query, (rs, i) -> {
            InvoiceEventStat invoiceEventStat = new InvoiceEventStat();

            invoiceEventStat.setPartyId(rs.getString(INVOICE_EVENT_STAT.PARTY_ID.getName()));
            invoiceEventStat.setPartyShopId(rs.getString(INVOICE_EVENT_STAT.PARTY_SHOP_ID.getName()));
            invoiceEventStat.setInvoiceId(rs.getString(INVOICE_EVENT_STAT.INVOICE_ID.getName()));
            invoiceEventStat.setPaymentId(rs.getString(INVOICE_EVENT_STAT.PAYMENT_ID.getName()));
            invoiceEventStat.setPaymentCreatedAt(rs.getObject(INVOICE_EVENT_STAT.PAYMENT_CREATED_AT.getName(), LocalDateTime.class));
            invoiceEventStat.setPaymentStatus(
                    TypeUtil.toEnumField(rs.getString(INVOICE_EVENT_STAT.PAYMENT_STATUS.getName()),
                            com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class)
            );
            invoiceEventStat.setPaymentFailureClass(rs.getString(INVOICE_EVENT_STAT.PAYMENT_FAILURE_CLASS.getName()));
            invoiceEventStat.setPaymentExternalFailureCode(rs.getString(INVOICE_EVENT_STAT.PAYMENT_EXTERNAL_FAILURE_CODE.getName()));
            invoiceEventStat.setPaymentExternalFailureDescription(rs.getString(INVOICE_EVENT_STAT.PAYMENT_EXTERNAL_FAILURE_DESCRIPTION.getName()));
            invoiceEventStat.setPaymentAmount(rs.getLong(INVOICE_EVENT_STAT.PAYMENT_AMOUNT.getName()));
            invoiceEventStat.setPaymentFee(rs.getLong(INVOICE_EVENT_STAT.PAYMENT_FEE.getName()));
            invoiceEventStat.setPaymentCurrencyCode(rs.getString(INVOICE_EVENT_STAT.PAYMENT_CURRENCY_CODE.getName()));
            invoiceEventStat.setPaymentTool(rs.getString(INVOICE_EVENT_STAT.PAYMENT_TOOL.getName()));
            invoiceEventStat.setPaymentToken(rs.getString(INVOICE_EVENT_STAT.PAYMENT_TOKEN.getName()));
            invoiceEventStat.setPaymentSystem(rs.getString(INVOICE_EVENT_STAT.PAYMENT_SYSTEM.getName()));
            invoiceEventStat.setPaymentBin(rs.getString(INVOICE_EVENT_STAT.PAYMENT_BIN.getName()));
            invoiceEventStat.setPaymentMaskedPan(rs.getString(INVOICE_EVENT_STAT.PAYMENT_MASKED_PAN.getName()));
            invoiceEventStat.setPaymentTerminalProvider(rs.getString(INVOICE_EVENT_STAT.PAYMENT_TERMINAL_PROVIDER.getName()));
            invoiceEventStat.setPaymentDigitalWalletId(rs.getString(INVOICE_EVENT_STAT.PAYMENT_DIGITAL_WALLET_ID.getName()));
            invoiceEventStat.setPaymentDigitalWalletProvider(rs.getString(INVOICE_EVENT_STAT.PAYMENT_DIGITAL_WALLET_PROVIDER.getName()));
            invoiceEventStat.setPaymentIp(rs.getString(INVOICE_EVENT_STAT.PAYMENT_IP.getName()));
            invoiceEventStat.setPaymentFingerprint(rs.getString(INVOICE_EVENT_STAT.PAYMENT_FINGERPRINT.getName()));
            invoiceEventStat.setPaymentPhoneNumber(rs.getString(INVOICE_EVENT_STAT.PAYMENT_PHONE_NUMBER.getName()));
            invoiceEventStat.setPaymentEmail(rs.getString(INVOICE_EVENT_STAT.PAYMENT_EMAIL.getName()));
            invoiceEventStat.setPaymentSessionId(rs.getString(INVOICE_EVENT_STAT.PAYMENT_SESSION_ID.getName()));
            invoiceEventStat.setPaymentCustomerId(rs.getString(INVOICE_EVENT_STAT.PAYMENT_CUSTOMER_ID.getName()));
            invoiceEventStat.setPaymentFlow(rs.getString(INVOICE_EVENT_STAT.PAYMENT_FLOW.getName()));
            invoiceEventStat.setPaymentHoldOnExpiration(rs.getString(INVOICE_EVENT_STAT.PAYMENT_HOLD_ON_EXPIRATION.getName()));
            invoiceEventStat.setPaymentHoldUntil(rs.getObject(INVOICE_EVENT_STAT.PAYMENT_HOLD_UNTIL.getName(), LocalDateTime.class));
            invoiceEventStat.setPaymentCountryId(rs.getInt(INVOICE_EVENT_STAT.PAYMENT_COUNTRY_ID.getName()));
            invoiceEventStat.setPaymentCityId(rs.getInt(INVOICE_EVENT_STAT.PAYMENT_CITY_ID.getName()));
            invoiceEventStat.setPaymentContextType(rs.getString(INVOICE_EVENT_STAT.PAYMENT_CONTEXT_TYPE.getName()));
            invoiceEventStat.setPaymentContext(rs.getBytes(INVOICE_EVENT_STAT.PAYMENT_CONTEXT.getName()));

            return invoiceEventStat;
        });
    }

    @Override
    public Integer getPaymentsCount(
            Optional<String> merchantId,
            Optional<String> shopId,
            ConditionParameterSource parameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) throws DaoException {
        Query query = getDslContext().select(DSL.count()).from(INVOICE_EVENT_STAT)
                .where(buildPaymentCondition(merchantId, shopId, parameterSource, fromTime, toTime));
        return fetchOne(query, Integer.class);
    }

    @Override
    public Collection<Refund> getRefunds(
            Optional<String> merchantId,
            Optional<String> shopId,
            ConditionParameterSource parameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Integer> offset,
            Optional<Integer> limit
    ) throws DaoException {
        Query query = getDslContext().selectFrom(REFUND)
                .where(buildRefundCondition(merchantId, shopId, parameterSource, fromTime, toTime))
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
            refund.setRefundOperationFailureClass(rs.getString(REFUND.REFUND_OPERATION_FAILURE_CLASS.getName()));
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
            ConditionParameterSource parameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) throws DaoException {
        Query query = getDslContext().select(DSL.count()).from(REFUND)
                .where(buildRefundCondition(merchantId, shopId, parameterSource, fromTime, toTime));
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
    public Collection<Map<String, String>> getAccountingDataByPeriod(Instant fromTime, Instant toTime, Optional<List<Integer>> withoutShopCategoryIds) throws DaoException {
        //TODO rewrite this request to jooq
        String sql = "SELECT part1.party_id AS merchant_id, part1.party_shop_id AS shop_id, part1.payment_currency_code AS currency_code, (coalesce(funds_to_be_paid_prev_periods, 0) - coalesce(funds_paid_out_prev_periods, 0) - coalesce(funds_refunded_prev_periods, 0)) AS opening_balance, coalesce(funds_acquired_period, 0) AS funds_acquired, coalesce(fee_charged_period, 0) AS fee_charged, coalesce(funds_paid_out_period, 0) AS funds_paid_out, coalesce(funds_refunded_period, 0) AS funds_refunded, (coalesce(funds_to_be_paid_prev_periods, 0) - coalesce(funds_paid_out_prev_periods, 0) - coalesce(funds_refunded_prev_periods, 0) + coalesce(funds_acquired_period, 0) - coalesce(fee_charged_period, 0) - coalesce(funds_paid_out_period, 0) - coalesce(funds_refunded_period, 0)) AS closing_balance FROM (SELECT party_id, party_shop_id, payment_currency_code, sum(payment_amount) AS funds_acquired_period, sum(payment_fee) AS fee_charged_period FROM mst.invoice_event_stat WHERE id IN (SELECT max(id) FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT' :: mst.INVOICE_EVENT_CATEGORY AND party_shop_category_id NOT IN (:shop_categories) AND event_created_at >= :from_time AND event_created_at < :to_time GROUP BY invoice_id, payment_id) AND payment_status IN (:succeeded_status :: mst.INVOICE_PAYMENT_STATUS, :refunded_status :: mst.INVOICE_PAYMENT_STATUS) GROUP BY party_shop_id, payment_currency_code, party_id) part1 LEFT JOIN (SELECT party_id, party_shop_id, sum( payment_amount) AS funds_refunded_period FROM mst.invoice_event_stat WHERE id IN (SELECT max(id) FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT' :: mst.INVOICE_EVENT_CATEGORY AND event_created_at >= :from_time AND event_created_at < :to_time GROUP BY invoice_id, payment_id) AND payment_status = :refunded_status :: mst.INVOICE_PAYMENT_STATUS GROUP BY party_shop_id, payment_currency_code, party_id) part2 ON part1.party_id = part2.party_id AND part1.party_shop_id = part2.party_shop_id LEFT JOIN (SELECT party_id, party_shop_id, sum(payment_amount - payment_fee) AS funds_to_be_paid_prev_periods FROM mst.invoice_event_stat WHERE id IN (SELECT max(id) FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT' :: mst.INVOICE_EVENT_CATEGORY AND event_created_at < :from_time GROUP BY invoice_id, payment_id) AND payment_status IN (:succeeded_status :: mst.INVOICE_PAYMENT_STATUS, :refunded_status :: mst.INVOICE_PAYMENT_STATUS) GROUP BY party_shop_id, party_id) part3 ON part1.party_id = part3.party_id AND part1.party_shop_id = part3.party_shop_id LEFT JOIN (SELECT party_id, party_shop_id, sum(payment_amount) AS funds_refunded_prev_periods FROM mst.invoice_event_stat WHERE id IN (SELECT max(id) FROM mst.invoice_event_stat WHERE event_category = 'PAYMENT' :: mst.INVOICE_EVENT_CATEGORY AND event_created_at < :from_time GROUP BY invoice_id, payment_id) AND payment_status = :refunded_status :: mst.INVOICE_PAYMENT_STATUS GROUP BY party_shop_id, party_id) part4 ON part1.party_id = part4.party_id AND part1.party_shop_id = part4.party_shop_id LEFT JOIN (SELECT party_id, party_shop_id, sum(payout_amount) AS funds_paid_out_period FROM mst.payout_event_stat WHERE id IN (SELECT max(id) FROM mst.payout_event_stat WHERE payout_created_at >= :from_time AND payout_created_at < :to_time GROUP BY payout_id) AND payout_status = :payout_confirmed_status :: mst.PAYOUT_STATUS GROUP BY party_shop_id, party_id) part5 ON part1.party_id = part5.party_id AND part1.party_shop_id = part5.party_shop_id LEFT JOIN (SELECT party_id, party_shop_id, sum(payout_amount) AS funds_paid_out_prev_periods FROM mst.payout_event_stat WHERE id IN (SELECT max(id) FROM mst.payout_event_stat WHERE payout_created_at < :from_time GROUP BY payout_id) AND payout_status = :payout_confirmed_status :: mst.PAYOUT_STATUS GROUP BY party_shop_id, party_id) part6 ON part1.party_id = part6.party_id AND part1.party_shop_id = part6.party_shop_id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from_time", LocalDateTime.ofInstant(fromTime, ZoneOffset.UTC), Types.OTHER)
                .addValue("to_time", LocalDateTime.ofInstant(toTime, ZoneOffset.UTC), Types.OTHER)
                .addValue("succeeded_status", InvoicePaymentStatus._Fields.CAPTURED.getFieldName())
                .addValue("refunded_status", InvoicePaymentStatus._Fields.REFUNDED.getFieldName())
                .addValue("payout_confirmed_status", com.rbkmoney.damsel.payout_processing.PayoutStatus._Fields.CONFIRMED.getFieldName())
                .addValue("shop_categories", withoutShopCategoryIds.orElse(Arrays.asList(-1)));
        log.trace("SQL: {}, Params: {}", sql, params.getValues());

        return fetch(sql, params, (rs, i) -> {
            Map<String, String> map = new HashMap<>();
            map.put("merchant_id", rs.getString("merchant_id"));
            map.put("shop_id", rs.getString("shop_id"));
            map.put("currency_code", rs.getString("currency_code"));
            map.put("opening_balance", rs.getString("opening_balance"));
            map.put("funds_acquired", rs.getString("funds_acquired"));
            map.put("fee_charged", rs.getString("fee_charged"));
            map.put("funds_paid_out", rs.getString("funds_paid_out"));
            map.put("funds_refunded", rs.getString("funds_refunded"));
            map.put("closing_balance", rs.getString("closing_balance"));
            return map;
        });
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

    private Condition buildPaymentCondition(
            Optional<String> merchantId,
            Optional<String> shopId,
            ConditionParameterSource paymentParameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) {
        Condition condition = appendPartyAndDateTimeRangeConditions(
                INVOICE_EVENT_STAT.EVENT_CATEGORY.eq(InvoiceEventCategory.PAYMENT),
                merchantId,
                shopId,
                INVOICE_EVENT_STAT.EVENT_CREATED_AT,
                fromTime,
                toTime);

        condition = INVOICE_EVENT_STAT.ID.in(
                getDslContext().select(DSL.max(INVOICE_EVENT_STAT.ID)).from(INVOICE_EVENT_STAT)
                        .where(condition).groupBy(INVOICE_EVENT_STAT.INVOICE_ID, INVOICE_EVENT_STAT.PAYMENT_ID)
        );

        return appendConditions(condition, Operator.AND, paymentParameterSource);
    }

    private Condition buildInvoiceCondition(
            Optional<String> merchantId,
            Optional<String> shopId,
            ConditionParameterSource invoiceParameterSource,
            ConditionParameterSource paymentParameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) {
        Condition condition = appendPartyAndDateTimeRangeConditions(
                INVOICE_EVENT_STAT.EVENT_CATEGORY.eq(InvoiceEventCategory.INVOICE),
                merchantId,
                shopId,
                INVOICE_EVENT_STAT.EVENT_CREATED_AT,
                fromTime,
                toTime);

        condition = INVOICE_EVENT_STAT.ID.in(
                getDslContext().select(DSL.max(INVOICE_EVENT_STAT.ID)).from(INVOICE_EVENT_STAT)
                        .where(condition).groupBy(INVOICE_EVENT_STAT.INVOICE_ID)
        );

        if (!paymentParameterSource.getConditionFields().isEmpty()) {
            condition = condition.and(
                    INVOICE_EVENT_STAT.INVOICE_ID.in(
                            getDslContext().select(INVOICE_EVENT_STAT.INVOICE_ID)
                                    .from(INVOICE_EVENT_STAT)
                                    .where(buildPaymentCondition(merchantId, shopId, paymentParameterSource, fromTime, toTime))
                    )
            );
        }
        return appendConditions(condition, Operator.AND, invoiceParameterSource);
    }

    private Condition buildRefundCondition(
            Optional<String> merchantId,
            Optional<String> shopId,
            ConditionParameterSource parameterSource,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) {
        Condition condition = REFUND.ID.in(
                getDslContext().select(DSL.max(REFUND.ID)).from(REFUND)
                        .where(
                                appendPartyAndDateTimeRangeConditions(
                                        DSL.trueCondition(),
                                        merchantId,
                                        shopId,
                                        REFUND.EVENT_CREATED_AT,
                                        fromTime,
                                        toTime
                                )
                        ).groupBy(REFUND.INVOICE_ID, REFUND.PAYMENT_ID, REFUND.REFUND_ID)
        );
        return appendConditions(condition, Operator.AND, parameterSource);
    }

    private Condition appendPartyAndDateTimeRangeConditions(Condition condition,
                                                            Optional<String> merchantId,
                                                            Optional<String> shopId,
                                                            Field<LocalDateTime> field,
                                                            Optional<LocalDateTime> fromTime,
                                                            Optional<LocalDateTime> toTime) {
        if (merchantId.isPresent()) {
            condition = condition.and(INVOICE_EVENT_STAT.PARTY_ID.eq(merchantId.get()));
        }
        if (shopId.isPresent()) {
            condition = condition.and(INVOICE_EVENT_STAT.PARTY_SHOP_ID.eq(shopId.get()));
        }

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

    private <T extends Record> DateTimeRange getDateTimeRangeByOffset(Condition condition,
                                                                      Optional<LocalDateTime> fromTime,
                                                                      Optional<LocalDateTime> toTime,
                                                                      TableField<T, LocalDateTime> dateTimeField,
                                                                      int offset,
                                                                      int limit) {
        log.debug("Trying to get datetime range, fromTime='{}', toTime='{}', offset='{}', limit='{}', condition='{}'",
                fromTime, toTime, offset, limit, condition);
        List<Map.Entry<LocalDateTime, Integer>> dateRanges = getCacheDateTimeRanges(condition,
                fromTime,
                toTime,
                dateTimeField);

        boolean offsetFound = false;
        int fromTimeBound = limit;
        DateTimeRange currentRange = new DateTimeRange(fromTime.orElse(null), toTime.orElse(null), offset);
        for (Map.Entry<LocalDateTime, Integer> dateRange : dateRanges) {
            int newOffset = currentRange.getOffset() - dateRange.getValue();
            if (!offsetFound && newOffset >= 0) {
                currentRange.setToTime(dateRange.getKey());
                currentRange.setOffset(newOffset);
            } else {
                offsetFound = true;
                if ((fromTimeBound + currentRange.getOffset()) - dateRange.getValue() > 0) {
                    fromTimeBound -= dateRange.getValue();
                } else {
                    currentRange.setFromTime(dateRange.getKey());
                    break;
                }

            }
        }
        log.debug("Datetime range was retrieved, datetimeRange='{}', condition='{}'", currentRange, condition);
        return currentRange;
    }

    private <T extends Record> List<Map.Entry<LocalDateTime, Integer>> getCacheDateTimeRanges(Condition condition,
                                                                                              Optional<LocalDateTime> fromTime,
                                                                                              Optional<LocalDateTime> toTime,
                                                                                              TableField<T, LocalDateTime> dateTimeField) {
        log.debug("Trying to get datetime ranges from the cache, condition='{}', dateTimeField='{}'", condition, dateTimeField);
        final Map.Entry key = new AbstractMap.SimpleEntry<>(condition, dateTimeField.getName());

        List<Map.Entry<LocalDateTime, Integer>> dateRanges = statCache.getIfPresent(key);
        if (dateRanges == null && checkBounds(toTime, dateTimeField)) {
            dateRanges = statCache.get(key, keyValue -> getDateTimeRanges(condition, dateTimeField));
        }

        if (dateRanges != null) {
            log.debug("Datetime ranges was retrieved from the cache, rangesCount='{}', condition='{}', dateTimeField='{}'", dateRanges.size(), condition, dateTimeField);
            return dateRanges;
        }

        log.debug("Datetime ranges not found in the cache, condition='{}', dateTimeField='{}'", condition, dateTimeField);
        return Collections.emptyList();
    }

    private <T extends Record> List<Map.Entry<LocalDateTime, Integer>> getDateTimeRanges(Condition condition,
                                                                                         TableField<T, LocalDateTime> dateTimeField) {
        log.debug("Trying to get datetime ranges from the storage, condition='{}', dateTimeField='{}'", condition, dateTimeField);
        Field<LocalDateTime> spValField = DSL.field(
                DSL.sql("date_trunc('" + DatePart.HOUR.toSQL() + "', " + dateTimeField.getName() + ")"),
                LocalDateTime.class
        ).as("sp_val");
        Field countField = DSL.count().as("count");

        Query query = getDslContext().select(spValField, countField).from(INVOICE_EVENT_STAT)
                .where(condition)
                .groupBy(spValField)
                .orderBy(spValField.desc());

        List<Map.Entry<LocalDateTime, Integer>> dateRanges = fetch(query,
                (resultSet, i) -> new AbstractMap.SimpleEntry<>(
                        resultSet.getObject(spValField.getName(), LocalDateTime.class),
                        resultSet.getInt(countField.getName())
                )
        );
        log.debug("Datetime ranges was retrieved from the storage, rangesCount='{}', condition='{}', dateTimeField='{}'", dateRanges.size(), condition, dateTimeField);
        return dateRanges;
    }

    private <T extends Record> boolean checkBounds(Optional<LocalDateTime> toTime,
                                                   TableField<T, LocalDateTime> dateTimeField) {
        if (!toTime.isPresent()) {
            return false;
        }

        Boolean checkBounds = fetchOne(getDslContext().select(
                DSL.field(DSL.max(dateTimeField).ge(toTime.get())))
                        .from(dateTimeField.getTable()),
                Boolean.class);

        return checkBounds != null && checkBounds;
    }

    public static class DateTimeRange {

        private LocalDateTime fromTime;

        private LocalDateTime toTime;

        private int offset;

        public DateTimeRange(LocalDateTime fromTime, LocalDateTime toTime, int offset) {
            this.fromTime = fromTime;
            this.toTime = toTime;
            this.offset = offset;
        }

        public LocalDateTime getFromTime() {
            return fromTime;
        }

        public void setFromTime(LocalDateTime fromTime) {
            this.fromTime = fromTime;
        }

        public LocalDateTime getToTime() {
            return toTime;
        }

        public void setToTime(LocalDateTime toTime) {
            this.toTime = toTime;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        @Override
        public String toString() {
            return "DateTimeRange{" +
                    "fromTime=" + fromTime +
                    ", toTime=" + toTime +
                    ", offset=" + offset +
                    '}';
        }
    }

}
