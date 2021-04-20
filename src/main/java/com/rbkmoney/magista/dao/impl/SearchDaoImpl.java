package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.dao.impl.field.ConditionParameterSource;
import com.rbkmoney.magista.dao.impl.mapper.*;
import com.rbkmoney.magista.domain.enums.*;
import com.rbkmoney.magista.domain.enums.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.PayoutType;
import com.rbkmoney.magista.query.impl.*;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.time.LocalDateTime;
import java.util.*;

import static com.rbkmoney.geck.common.util.TypeUtil.toEnumField;
import static com.rbkmoney.geck.common.util.TypeUtil.toEnumFields;
import static com.rbkmoney.magista.domain.tables.ChargebackData.CHARGEBACK_DATA;
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
    private final StatChargebackMapper statChargebackMapper;
    private final EnrichedStatInvoiceMapper enrichedStatInvoiceMapper;

    public SearchDaoImpl(DataSource ds) {
        super(ds);
        statInvoiceMapper = new StatInvoiceMapper();
        statPaymentMapper = new StatPaymentMapper();
        statRefundMapper = new StatRefundMapper();
        statPayoutMapper = new StatPayoutMapper();
        statChargebackMapper = new StatChargebackMapper();
        enrichedStatInvoiceMapper = new EnrichedStatInvoiceMapper();
    }

    @Override
    public Collection<Map.Entry<Long, StatInvoice>> getInvoices(
            InvoicesFunction.InvoicesParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            LocalDateTime whereTime,
            int limit
    ) {

        Condition condition = appendDateTimeRangeConditions(
                appendConditions(DSL.trueCondition(), Operator.AND,
                        new ConditionParameterSource()
                                .addValue(INVOICE_DATA.PARTY_ID,
                                        Optional.ofNullable(parameters.getMerchantId())
                                                .map(UUID::fromString)
                                                .orElse(null),
                                        EQUALS)
                                .addValue(INVOICE_DATA.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                                .addInConditionValue(INVOICE_DATA.PARTY_SHOP_ID, parameters.getShopIds())
                                .addValue(INVOICE_DATA.INVOICE_ID, parameters.getInvoiceId(), EQUALS)
                                .addInConditionValue(INVOICE_DATA.INVOICE_ID, parameters.getInvoiceIds())
                                .addValue(INVOICE_DATA.EXTERNAL_ID, parameters.getExternalId(), EQUALS)
                                .addValue(INVOICE_DATA.INVOICE_CREATED_AT, whereTime, LESS)
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
                        toEnumField(parameters.getPaymentBankCardTokenProvider(), BankCardTokenProvider.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TERMINAL_PROVIDER, parameters.getPaymentTerminalProvider(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_EMAIL, parameters.getPaymentEmail(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_IP, parameters.getPaymentIp(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FINGERPRINT, parameters.getPaymentFingerprint(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_FIRST6, parameters.getPaymentBankCardFirst6(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_LAST4, parameters.getPaymentBankCardLast4(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_CUSTOMER_ID, parameters.getPaymentCustomerId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_STATUS,
                        TypeUtil.toEnumField(parameters.getPaymentStatus(), InvoicePaymentStatus.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getPaymentDomainRevision(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_AMOUNT, parameters.getPaymentAmount(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getFromPaymentDomainRevision(),
                        GREATER_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getToPaymentDomainRevision(), LESS_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_RRN, parameters.getPaymentRrn(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_APPROVAL_CODE, parameters.getPaymentApproveCode(), EQUALS)
                .addValue(PAYMENT_DATA.EXTERNAL_ID, parameters.getExternalId(), EQUALS);

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
                                                                            Optional.ofNullable(
                                                                                    parameters.getMerchantId())
                                                                                    .map(UUID::fromString)
                                                                                    .orElse(null),
                                                                            EQUALS)
                                                                    .addValue(PAYMENT_DATA.PARTY_SHOP_ID,
                                                                            parameters.getShopId(), EQUALS)
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
            LocalDateTime fromTime,
            LocalDateTime toTime,
            LocalDateTime whereTime,
            int limit
    ) {
        ConditionParameterSource conditionParameterSource = preparePaymentsCondition(parameters, whereTime);

        SelectConditionStep<org.jooq.Record> conditionStep = getDslContext()
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
            LocalDateTime fromTime,
            LocalDateTime toTime,
            LocalDateTime whereTime,
            int limit
    ) {
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
                .limit(limit);
        return fetch(query, statRefundMapper);
    }

    private ConditionParameterSource prepareRefundCondition(RefundsFunction.RefundsParameters parameters,
                                                            LocalDateTime whereTime) {
        return new ConditionParameterSource()
                .addValue(REFUND_DATA.PARTY_ID, parameters.getMerchantId(), EQUALS)
                .addValue(REFUND_DATA.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                .addInConditionValue(REFUND_DATA.PARTY_SHOP_ID, parameters.getShopIds())
                .addValue(REFUND_DATA.INVOICE_ID, parameters.getInvoiceId(), EQUALS)
                .addInConditionValue(REFUND_DATA.INVOICE_ID, parameters.getInvoiceIds())
                .addValue(REFUND_DATA.PAYMENT_ID, parameters.getPaymentId(), EQUALS)
                .addValue(REFUND_DATA.REFUND_ID, parameters.getRefundId(), EQUALS)
                .addValue(REFUND_DATA.EXTERNAL_ID, parameters.getExternalId(), EQUALS)
                .addValue(REFUND_DATA.REFUND_CREATED_AT, whereTime, LESS)
                .addValue(REFUND_DATA.REFUND_STATUS,
                        toEnumField(parameters.getRefundStatus(), RefundStatus.class),
                        EQUALS);
    }

    @Override
    public Collection<Map.Entry<Long, StatPayout>> getPayouts(
            PayoutsFunction.PayoutsParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            LocalDateTime whereTime,
            int limit
    ) {
        Query query = getDslContext().selectFrom(PAYOUT_DATA)
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(
                                        DSL.trueCondition(),
                                        Operator.AND,
                                        new ConditionParameterSource()
                                                .addValue(PAYOUT_DATA.PARTY_ID, parameters.getMerchantId(), EQUALS)
                                                .addValue(PAYOUT_DATA.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                                                .addInConditionValue(PAYOUT_DATA.PARTY_SHOP_ID, parameters.getShopIds())
                                                .addValue(PAYOUT_DATA.PAYOUT_ID, parameters.getPayoutId(), EQUALS)
                                                .addValue(PAYOUT_DATA.PAYOUT_STATUS,
                                                        toEnumField(parameters.getPayoutStatus(), PayoutStatus.class),
                                                        EQUALS)
                                                .addInConditionValue(PAYOUT_DATA.PAYOUT_STATUS,
                                                        toEnumFields(parameters.getPayoutStatuses(),
                                                                PayoutStatus.class))
                                                .addValue(PAYOUT_DATA.PAYOUT_TYPE,
                                                        toEnumField(parameters.getPayoutType(), PayoutType.class),
                                                        EQUALS)
                                                .addValue(PAYOUT_DATA.PAYOUT_CREATED_AT, whereTime, LESS)
                                ),
                                PAYOUT_DATA.PAYOUT_CREATED_AT,
                                fromTime,
                                toTime
                        )
                ).orderBy(PAYOUT_DATA.PAYOUT_CREATED_AT.desc())
                .limit(limit);

        return fetch(query, statPayoutMapper);
    }

    @Override
    public Collection<Map.Entry<Long, StatChargeback>> getChargebacks(
            ChargebacksFunction.ChargebacksParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            LocalDateTime whereTime,
            int limit) {
        Query query = getDslContext().selectFrom(CHARGEBACK_DATA)
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(
                                        DSL.trueCondition(),
                                        Operator.AND,
                                        new ConditionParameterSource()
                                                .addValue(CHARGEBACK_DATA.PARTY_ID, parameters.getMerchantId(), EQUALS)
                                                .addValue(CHARGEBACK_DATA.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                                                .addInConditionValue(CHARGEBACK_DATA.PARTY_SHOP_ID,
                                                        parameters.getShopIds())
                                                .addValue(CHARGEBACK_DATA.INVOICE_ID, parameters.getInvoiceId(), EQUALS)
                                                .addValue(CHARGEBACK_DATA.PAYMENT_ID, parameters.getPaymentId(), EQUALS)
                                                .addValue(CHARGEBACK_DATA.CHARGEBACK_ID, parameters.getChargebackId(),
                                                        EQUALS)
                                                .addInConditionValue(CHARGEBACK_DATA.CHARGEBACK_STATUS,
                                                        toEnumFields(parameters.getChargebackStatuses(),
                                                                ChargebackStatus.class))
                                                .addInConditionValue(CHARGEBACK_DATA.CHARGEBACK_STAGE,
                                                        toEnumFields(parameters.getChargebackStages(),
                                                                ChargebackStage.class))
                                                .addInConditionValue(CHARGEBACK_DATA.CHARGEBACK_REASON_CATEGORY,
                                                        toEnumFields(parameters.getChargebackCategories(),
                                                                ChargebackCategory.class))
                                                .addValue(CHARGEBACK_DATA.CHARGEBACK_CREATED_AT, whereTime, LESS)
                                ),
                                CHARGEBACK_DATA.CHARGEBACK_CREATED_AT,
                                fromTime,
                                toTime
                        )
                ).orderBy(CHARGEBACK_DATA.CHARGEBACK_CREATED_AT.desc())
                .limit(limit);

        return fetch(query, statChargebackMapper);
    }

    /**
     * merchant OKKO-specific, in general shouldn't be touched.
     *
     * @author n.pospolita
     */
    @Override
    public Collection<Map.Entry<Long, EnrichedStatInvoice>> getEnrichedInvoices(
            RefundsFunction.RefundsParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            LocalDateTime whereTime,
            int limit
    ) {
        ConditionParameterSource refundParameterSource = prepareEnrichedRefundCondition(parameters, whereTime);

        Query query = getDslContext()
                .selectFrom(REFUND_DATA
                        .join(PAYMENT_DATA).on(PAYMENT_DATA.INVOICE_ID.eq(REFUND_DATA.INVOICE_ID),
                                PAYMENT_DATA.PAYMENT_ID.eq(REFUND_DATA.PAYMENT_ID))
                        .join(INVOICE_DATA).on(INVOICE_DATA.INVOICE_ID.eq(REFUND_DATA.INVOICE_ID))
                )
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND, refundParameterSource),
                                REFUND_DATA.EVENT_CREATED_AT,
                                fromTime,
                                toTime
                        )
                )
                .orderBy(REFUND_DATA.EVENT_CREATED_AT.desc())
                .limit(limit);

        return fetch(query, enrichedStatInvoiceMapper);
    }

    /**
     * merchant OKKO-specific, in general shouldn't be touched.
     *
     * @author n.pospolita
     */
    @Override
    public Collection<Map.Entry<Long, EnrichedStatInvoice>> getEnrichedInvoices(
            PaymentsFunction.PaymentsParameters parameters,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            LocalDateTime whereTime,
            int limit
    ) {
        ConditionParameterSource conditionParameterSource = prepareEnrichedPaymentsCondition(parameters, whereTime);

        Query query = getDslContext()
                .select()
                .from(PAYMENT_DATA
                        .leftJoin(REFUND_DATA).on(REFUND_DATA.INVOICE_ID.eq(PAYMENT_DATA.INVOICE_ID),
                                REFUND_DATA.PAYMENT_ID.eq(PAYMENT_DATA.PAYMENT_ID))
                        .join(INVOICE_DATA).on(INVOICE_DATA.INVOICE_ID.eq(PAYMENT_DATA.INVOICE_ID))
                )
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND, conditionParameterSource),
                                PAYMENT_DATA.EVENT_CREATED_AT,
                                fromTime,
                                toTime
                        )
                )
                .orderBy(PAYMENT_DATA.EVENT_CREATED_AT.desc())
                .limit(limit);

        return fetch(query, enrichedStatInvoiceMapper);
    }

    private ConditionParameterSource preparePaymentsCondition(PaymentsFunction.PaymentsParameters parameters,
                                                              LocalDateTime whereTime) {
        return new ConditionParameterSource()
                .addValue(
                        PAYMENT_DATA.PARTY_ID,
                        Optional.ofNullable(parameters.getMerchantId())
                                .map(UUID::fromString)
                                .orElse(null),
                        EQUALS
                )
                .addValue(PAYMENT_DATA.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                .addInConditionValue(PAYMENT_DATA.PARTY_SHOP_ID, parameters.getShopIds())
                .addValue(PAYMENT_DATA.INVOICE_ID, parameters.getInvoiceId(), EQUALS)
                .addInConditionValue(PAYMENT_DATA.INVOICE_ID, parameters.getInvoiceIds())
                .addValue(PAYMENT_DATA.PAYMENT_ID, parameters.getPaymentId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_STATUS,
                        toEnumField(parameters.getPaymentStatus(),
                                com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FLOW,
                        toEnumField(parameters.getPaymentFlow(), PaymentFlow.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TOOL, parameters.getPaymentMethod(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_TOKEN_PROVIDER,
                        toEnumField(parameters.getPaymentBankCardTokenProvider(),
                                com.rbkmoney.magista.domain.enums.BankCardTokenProvider.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TERMINAL_PROVIDER, parameters.getPaymentTerminalProvider(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_EMAIL, parameters.getPaymentEmail(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_IP, parameters.getPaymentIp(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FINGERPRINT, parameters.getPaymentFingerprint(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_FIRST6, parameters.getPaymentBankCardFirst6(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_SYSTEM, parameters.getPaymentBankCardSystem(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_LAST4, parameters.getPaymentBankCardLast4(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_CUSTOMER_ID, parameters.getPaymentCustomerId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_PROVIDER_ID, parameters.getPaymentProviderId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TERMINAL_ID, parameters.getPaymentTerminalId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_AMOUNT, parameters.getPaymentAmount(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getPaymentDomainRevision(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getFromPaymentDomainRevision(),
                        GREATER_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getToPaymentDomainRevision(), LESS_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_CREATED_AT, whereTime, LESS)
                .addValue(PAYMENT_DATA.PAYMENT_RRN, parameters.getPaymentRrn(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_APPROVAL_CODE, parameters.getPaymentApproveCode(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_AMOUNT, parameters.getPaymentAmountFrom(), GREATER_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_AMOUNT, parameters.getPaymentAmountTo(), LESS_OR_EQUAL)
                .addValue(PAYMENT_DATA.EXTERNAL_ID, parameters.getExternalId(), EQUALS);
    }

    /**
     * merchant OKKO-specific, in general shouldn't be touched.
     *
     * @author n.pospolita
     */
    private ConditionParameterSource prepareEnrichedPaymentsCondition(PaymentsFunction.PaymentsParameters parameters,
                                                                      LocalDateTime whereTime) {
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
                        toEnumField(parameters.getPaymentStatus(),
                                com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FLOW,
                        toEnumField(parameters.getPaymentFlow(), PaymentFlow.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TOOL, parameters.getPaymentMethod(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_TOKEN_PROVIDER,
                        toEnumField(parameters.getPaymentBankCardTokenProvider(),
                                com.rbkmoney.magista.domain.enums.BankCardTokenProvider.class),
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
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getFromPaymentDomainRevision(),
                        GREATER_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getToPaymentDomainRevision(), LESS_OR_EQUAL)
                .addValue(PAYMENT_DATA.EVENT_CREATED_AT, whereTime, LESS)
                .addValue(PAYMENT_DATA.PAYMENT_RRN, parameters.getPaymentRrn(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_APPROVAL_CODE, parameters.getPaymentApproveCode(), EQUALS)
                .addValue(PAYMENT_DATA.EXTERNAL_ID, parameters.getExternalId(), EQUALS);
    }

    /**
     * merchant OKKO-specific, in general shouldn't be touched
     *
     * @author n.pospolita
     */
    private ConditionParameterSource prepareEnrichedRefundCondition(RefundsFunction.RefundsParameters parameters,
                                                                    LocalDateTime whereTime) {
        return new ConditionParameterSource()
                .addValue(REFUND_DATA.PARTY_ID, parameters.getMerchantId(), EQUALS)
                .addValue(REFUND_DATA.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                .addValue(REFUND_DATA.INVOICE_ID, parameters.getInvoiceId(), EQUALS)
                .addValue(REFUND_DATA.PAYMENT_ID, parameters.getPaymentId(), EQUALS)
                .addValue(REFUND_DATA.REFUND_ID, parameters.getRefundId(), EQUALS)
                .addValue(REFUND_DATA.EXTERNAL_ID, parameters.getExternalId(), EQUALS)
                .addValue(REFUND_DATA.EVENT_CREATED_AT, whereTime, LESS)
                .addValue(REFUND_DATA.REFUND_STATUS,
                        toEnumField(parameters.getRefundStatus(), RefundStatus.class),
                        EQUALS);
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
