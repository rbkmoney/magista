package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.dao.impl.field.ConditionParameterSource;
import com.rbkmoney.magista.dao.impl.mapper.*;
import com.rbkmoney.magista.domain.enums.PaymentFlow;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.PayoutType;
import com.rbkmoney.magista.domain.enums.RefundStatus;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.query.impl.InvoicesFunction;
import com.rbkmoney.magista.query.impl.PaymentsFunction;
import com.rbkmoney.magista.query.impl.PayoutsFunction;
import com.rbkmoney.magista.query.impl.RefundsFunction;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.*;

import static com.rbkmoney.geck.common.util.TypeUtil.*;
import static com.rbkmoney.magista.domain.tables.InvoiceData.INVOICE_DATA;
import static com.rbkmoney.magista.domain.tables.PaymentData.PAYMENT_DATA;
import static com.rbkmoney.magista.domain.tables.PayoutData.PAYOUT_DATA;
import static com.rbkmoney.magista.domain.tables.RefundData.REFUND_DATA;
import static com.rbkmoney.magista.query.impl.Parameters.SHOP_ID_PARAM;
import static org.jooq.Comparator.*;

@Component
public class SearchDaoImpl extends AbstractDao implements SearchDao {

    private final StatInvoiceMapper statInvoiceMapper;
    private final StatPaymentMapper statPaymentMapper;
    private final StatRefundMapper statRefundMapper;
    private final StatPayoutMapper statPayoutMapper;
    private final EnrichedStatInvoiceMapper enrichedStatInvoiceMapper;

    public SearchDaoImpl(DataSource ds) {
        super(ds);
        statInvoiceMapper = new StatInvoiceMapper();
        statPaymentMapper = new StatPaymentMapper();
        statRefundMapper = new StatRefundMapper();
        statPayoutMapper = new StatPayoutMapper();
        enrichedStatInvoiceMapper = new EnrichedStatInvoiceMapper();
    }

    @Override
    public Collection<Map.Entry<Long, StatInvoice>> getInvoices(
            InvoicesFunction.InvoicesParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<LocalDateTime> whereTime,
            int limit
    ) throws DaoException {

        Condition condition = appendDateTimeRangeConditions(
                appendConditions(DSL.trueCondition(), Operator.AND,
                        new ConditionParameterSource()
                                .addValue(INVOICE_DATA.PARTY_ID,
                                        Optional.ofNullable(parameters.getMerchantId())
                                                .map(UUID::fromString)
                                                .orElse(null),
                                        EQUALS)
                                .addValue(INVOICE_DATA.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                                .addValue(INVOICE_DATA.INVOICE_ID, parameters.getInvoiceId(), EQUALS)
                                .addValue(INVOICE_DATA.INVOICE_CREATED_AT, whereTime.orElse(null), LESS)
                                .addValue(INVOICE_DATA.INVOICE_STATUS,
                                        toEnumField(
                                                parameters.getInvoiceStatus(),
                                                com.rbkmoney.magista.domain.enums.InvoiceStatus.class
                                        ),
                                        EQUALS)
                                .addValue(INVOICE_DATA.INVOICE_AMOUNT, parameters.getInvoiceAmount(), EQUALS)),
                INVOICE_DATA.INVOICE_CREATED_AT,
                fromTime,
                toTime
        );

        ConditionParameterSource paymentParameterSource = new ConditionParameterSource()
                .addValue(PAYMENT_DATA.PAYMENT_ID, parameters.getPaymentId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FLOW,
                        TypeUtil.toEnumField(parameters.getPaymentFlow(), PaymentFlow.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TOOL, parameters.getPaymentMethod(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_TOKEN_PROVIDER,
                        toEnumField(parameters.getPaymentBankCardTokenProvider(), com.rbkmoney.magista.domain.enums.BankCardTokenProvider.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TERMINAL_PROVIDER, parameters.getPaymentTerminalProvider(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_EMAIL, parameters.getPaymentEmail(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_IP, parameters.getPaymentIp(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FINGERPRINT, parameters.getPaymentFingerprint(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_FIRST6, parameters.getPaymentBankCardFirst6(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_LAST4, parameters.getPaymentBankCardLast4(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_CUSTOMER_ID, parameters.getPaymentCustomerId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_STATUS,
                        TypeUtil.toEnumField(parameters.getPaymentStatus(), com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getPaymentDomainRevision(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_AMOUNT, parameters.getPaymentAmount(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getFromPaymentDomainRevision(), GREATER_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getToPaymentDomainRevision(), LESS_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_RRN, parameters.getPaymentRrn(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_APPROVAL_CODE, parameters.getPaymentApproveCode(), EQUALS);

        if (!paymentParameterSource.getConditionFields().isEmpty()) {
            condition = condition.and(
              DSL.exists(
                      getDslContext().select(DSL.field("1")).from(PAYMENT_DATA)
                      .where(
                              appendDateTimeRangeConditions(
                                      appendConditions(
                                              INVOICE_DATA.INVOICE_ID.eq(PAYMENT_DATA.INVOICE_ID),
                                              Operator.AND,
                                              paymentParameterSource
                                                      .addValue(PAYMENT_DATA.PARTY_ID,
                                                              Optional.ofNullable(parameters.getMerchantId())
                                                                      .map(UUID::fromString)
                                                                      .orElse(null),
                                                              EQUALS)
                                                      .addValue(PAYMENT_DATA.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                                      ),
                                      PAYMENT_DATA.PAYMENT_CREATED_AT,
                                      fromTime,
                                      toTime
                              )
                      )
              )
            );
        }

        Query query = getDslContext()
                .selectFrom(INVOICE_DATA)
                .where(condition)
                .orderBy(INVOICE_DATA.INVOICE_CREATED_AT.desc())
                .limit(limit);
        return fetch(query, statInvoiceMapper);
    }

    @Override
    public Collection<Map.Entry<Long, StatPayment>> getPayments(
            PaymentsFunction.PaymentsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<LocalDateTime> whereTime,
            int limit
    ) throws DaoException {
        ConditionParameterSource conditionParameterSource = preparePaymentsCondition(parameters, whereTime);

        SelectConditionStep<Record> conditionStep = getDslContext()
                .select()
                .from(PAYMENT_DATA)
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND, conditionParameterSource),
                                PAYMENT_DATA.PAYMENT_CREATED_AT,
                                fromTime,
                                toTime
                        )
                );
        Condition excludeCondition = prepareExcludeCondition(parameters);
        if (excludeCondition != null) {
            conditionStep.and(excludeCondition);
        }
        Query query = conditionStep.orderBy(PAYMENT_DATA.PAYMENT_CREATED_AT.desc())
                .limit(limit);

        return fetch(query, statPaymentMapper);
    }

    @Override
    public Collection<Map.Entry<Long, StatRefund>> getRefunds(
            RefundsFunction.RefundsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<LocalDateTime> whereTime,
            Optional<Integer> offset,
            int limit
    ) throws DaoException {
        ConditionParameterSource refundParameterSource = prepareRefundCondition(parameters, whereTime);

        Query query = getDslContext().selectFrom(REFUND_DATA)
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND, refundParameterSource),
                                REFUND_DATA.REFUND_CREATED_AT,
                                fromTime,
                                toTime
                        )
                )
                .orderBy(REFUND_DATA.REFUND_CREATED_AT.desc())
                .limit(limit)
                .offset(offset.orElse(0));
        return fetch(query, statRefundMapper);
    }

    private ConditionParameterSource prepareRefundCondition(RefundsFunction.RefundsParameters parameters, Optional<LocalDateTime> whereTime) {
        return new ConditionParameterSource()
                .addValue(REFUND_DATA.PARTY_ID, parameters.getMerchantId(), EQUALS)
                .addValue(REFUND_DATA.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                .addValue(REFUND_DATA.INVOICE_ID, parameters.getInvoiceId(), EQUALS)
                .addValue(REFUND_DATA.PAYMENT_ID, parameters.getPaymentId(), EQUALS)
                .addValue(REFUND_DATA.REFUND_ID, parameters.getRefundId(), EQUALS)
                .addValue(REFUND_DATA.REFUND_CREATED_AT, whereTime.orElse(null), LESS)
                .addValue(REFUND_DATA.REFUND_STATUS,
                        toEnumField(parameters.getRefundStatus(), RefundStatus.class),
                        EQUALS);
    }

    @Override
    public Collection<Map.Entry<Long, StatPayout>> getPayouts(
            PayoutsFunction.PayoutsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<LocalDateTime> whereTime,
            Optional<Integer> offset,
            int limit
    ) throws DaoException {
        Query query = getDslContext().selectFrom(PAYOUT_DATA)
                .where(
                        appendConditions(DSL.trueCondition(), Operator.AND,
                                new ConditionParameterSource()
                                        .addValue(PAYOUT_DATA.PARTY_ID, parameters.getMerchantId(), EQUALS)
                                        .addValue(PAYOUT_DATA.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                                        .addValue(PAYOUT_DATA.PAYOUT_ID, parameters.getPayoutId(), EQUALS)
                                        .addValue(PAYOUT_DATA.PAYOUT_STATUS,
                                                toEnumField(parameters.getPayoutStatus(), PayoutStatus.class),
                                                EQUALS)
                                        .addInConditionValue(PAYOUT_DATA.PAYOUT_STATUS,
                                                toEnumFields(parameters.getPayoutStatuses(), PayoutStatus.class))
                                        .addValue(PAYOUT_DATA.PAYOUT_TYPE,
                                                toEnumField(parameters.getPayoutType(), PayoutType.class),
                                                EQUALS)
                                        .addValue(PAYOUT_DATA.PAYOUT_CREATED_AT, toLocalDateTime(parameters.getFromTime()), GREATER_OR_EQUAL)
                                        .addValue(PAYOUT_DATA.PAYOUT_CREATED_AT, toLocalDateTime(parameters.getToTime()), LESS)
                        )
                )
                .orderBy(PAYOUT_DATA.PAYOUT_CREATED_AT.desc())
                .limit(limit)
                .offset(offset.orElse(0));

        return fetch(query, statPayoutMapper);
    }

    @Override
    public Collection<Map.Entry<Long, EnrichedStatInvoice>> getEnrichedInvoices(RefundsFunction.RefundsParameters parameters, Optional<LocalDateTime> fromTime, Optional<LocalDateTime> toTime, Optional<LocalDateTime> whereTime, int limit) throws DaoException {
        ConditionParameterSource refundParameterSource = prepareRefundCondition(parameters, whereTime);

        Query query = getDslContext()
                .selectFrom(REFUND_DATA
                        .join(PAYMENT_DATA).on(PAYMENT_DATA.INVOICE_ID.eq(REFUND_DATA.INVOICE_ID), PAYMENT_DATA.PAYMENT_ID.eq(REFUND_DATA.PAYMENT_ID))
                        .join(INVOICE_DATA).on(INVOICE_DATA.INVOICE_ID.eq(REFUND_DATA.INVOICE_ID))
                )
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND, refundParameterSource),
                                REFUND_DATA.REFUND_CREATED_AT,
                                fromTime,
                                toTime
                        )
                )
                .orderBy(REFUND_DATA.REFUND_CREATED_AT.desc())
                .limit(limit);

        return fetch(query, enrichedStatInvoiceMapper);
    }

    @Override
    public Collection<Map.Entry<Long, EnrichedStatInvoice>> getEnrichedInvoices(PaymentsFunction.PaymentsParameters parameters, Optional<LocalDateTime> fromTime, Optional<LocalDateTime> toTime, Optional<LocalDateTime> whereTime, int limit) throws DaoException {
        ConditionParameterSource conditionParameterSource = preparePaymentsCondition(parameters, whereTime);

        Query query = getDslContext()
                .select()
                .from(PAYMENT_DATA
                        .leftJoin(REFUND_DATA).on(REFUND_DATA.INVOICE_ID.eq(PAYMENT_DATA.INVOICE_ID), REFUND_DATA.PAYMENT_ID.eq(PAYMENT_DATA.PAYMENT_ID))
                        .join(INVOICE_DATA).on(INVOICE_DATA.INVOICE_ID.eq(PAYMENT_DATA.INVOICE_ID))
                )
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND, conditionParameterSource),
                                PAYMENT_DATA.PAYMENT_CREATED_AT,
                                fromTime,
                                toTime
                        )
                )
                .orderBy(PAYMENT_DATA.PAYMENT_CREATED_AT.desc())
                .limit(limit);

        return fetch(query, enrichedStatInvoiceMapper);
    }

    private ConditionParameterSource preparePaymentsCondition(PaymentsFunction.PaymentsParameters parameters,
                                                              Optional<LocalDateTime> whereTime) {
        return new ConditionParameterSource()
                .addValue(
                        PAYMENT_DATA.PARTY_ID,
                        Optional.ofNullable(parameters.getMerchantId())
                                .map(UUID::fromString)
                                .orElse(null),
                        EQUALS
                )
                .addValue(PAYMENT_DATA.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                .addValue(PAYMENT_DATA.INVOICE_ID, parameters.getInvoiceId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_ID, parameters.getPaymentId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_STATUS,
                        toEnumField(parameters.getPaymentStatus(), com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FLOW,
                        toEnumField(parameters.getPaymentFlow(), PaymentFlow.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TOOL, parameters.getPaymentMethod(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_TOKEN_PROVIDER,
                        toEnumField(parameters.getPaymentBankCardTokenProvider(), com.rbkmoney.magista.domain.enums.BankCardTokenProvider.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TERMINAL_PROVIDER, parameters.getPaymentTerminalProvider(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_EMAIL, parameters.getPaymentEmail(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_IP, parameters.getPaymentIp(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FINGERPRINT, parameters.getPaymentFingerprint(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_FIRST6, parameters.getPaymentBankCardFirst6(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_SYSTEM, parameters.getPaymentBankCardSystem(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_LAST4, parameters.getPaymentBankCardLast4(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_CUSTOMER_ID, parameters.getPaymentCustomerId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_AMOUNT, parameters.getPaymentAmount(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getPaymentDomainRevision(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getFromPaymentDomainRevision(), GREATER_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getToPaymentDomainRevision(), LESS_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_CREATED_AT, whereTime.orElse(null), LESS)
                .addValue(PAYMENT_DATA.PAYMENT_RRN, parameters.getPaymentRrn(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_APPROVAL_CODE, parameters.getPaymentApproveCode(), EQUALS);
    }

    private Condition prepareExcludeCondition(PaymentsFunction.PaymentsParameters parameters) {
        Object paramObject = parameters.getExclude();
        Map excludeParam = paramObject instanceof Map ? ((Map) paramObject) : null;
        if (excludeParam != null) {
            Object shopIdParam = excludeParam.get(SHOP_ID_PARAM);
            List<String> excludeShopIds = shopIdParam instanceof List ? ((List) shopIdParam) : null;
            if (excludeShopIds != null && !excludeShopIds.isEmpty()) {
                return PAYMENT_DATA.PARTY_SHOP_ID.notIn(excludeShopIds);
            }
        }
        return null;
    }
}
