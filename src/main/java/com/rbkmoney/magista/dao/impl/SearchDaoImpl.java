package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.*;
import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.dao.impl.field.ConditionParameterSource;
import com.rbkmoney.magista.dao.impl.mapper.*;
import com.rbkmoney.magista.domain.enums.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.enums.PaymentTool;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.*;
import com.rbkmoney.magista.okko.EnrichedStatInvoice;
import com.rbkmoney.magista.service.TimeHolder;
import com.rbkmoney.magista.service.TokenGenService;
import org.jooq.Condition;
import org.jooq.Operator;
import org.jooq.Query;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.rbkmoney.geck.common.util.TypeUtil.toEnumField;
import static com.rbkmoney.geck.common.util.TypeUtil.toEnumFields;
import static com.rbkmoney.magista.domain.tables.ChargebackData.CHARGEBACK_DATA;
import static com.rbkmoney.magista.domain.tables.InvoiceData.INVOICE_DATA;
import static com.rbkmoney.magista.domain.tables.PaymentData.PAYMENT_DATA;
import static com.rbkmoney.magista.domain.tables.Payout.PAYOUT;
import static com.rbkmoney.magista.domain.tables.RefundData.REFUND_DATA;
import static org.jooq.Comparator.*;

@Component
public class SearchDaoImpl extends AbstractDao implements SearchDao {

    private final StatInvoiceMapper statInvoiceMapper;
    private final StatPaymentMapper statPaymentMapper;
    private final StatRefundMapper statRefundMapper;
    private final StatPayoutMapper statPayoutMapper;
    private final StatChargebackMapper statChargebackMapper;
    private final EnrichedStatInvoiceMapper enrichedStatInvoiceMapper;
    private final TokenGenService tokenGenService;

    public SearchDaoImpl(DataSource ds, TokenGenService tokenGenService) {
        super(ds);
        this.tokenGenService = tokenGenService;
        statInvoiceMapper = new StatInvoiceMapper();
        statPaymentMapper = new StatPaymentMapper();
        statRefundMapper = new StatRefundMapper();
        statPayoutMapper = new StatPayoutMapper();
        statChargebackMapper = new StatChargebackMapper();
        enrichedStatInvoiceMapper = new EnrichedStatInvoiceMapper();
    }

    @Override
    public List<StatInvoice> getInvoices(InvoiceSearchQuery searchQuery) {
        CommonSearchQueryParams commonParams = searchQuery.getCommonSearchQueryParams();
        TimeHolder timeHolder = buildTimeHolder(commonParams);
        Condition condition = appendDateTimeRangeConditions(
                appendConditions(DSL.trueCondition(), Operator.AND,
                        new ConditionParameterSource()
                                .addValue(
                                        INVOICE_DATA.PARTY_ID,
                                        Optional.ofNullable(commonParams.getPartyId())
                                                .map(UUID::fromString)
                                                .orElse(null),
                                        EQUALS)
                                .addInConditionValue(INVOICE_DATA.PARTY_SHOP_ID, commonParams.getShopIds())
                                .addInConditionValue(INVOICE_DATA.INVOICE_ID, searchQuery.getInvoiceIds())
                                .addValue(INVOICE_DATA.EXTERNAL_ID, searchQuery.getExternalId(), EQUALS)
                                .addValue(INVOICE_DATA.INVOICE_CREATED_AT, timeHolder.getWhereTime(), LESS)
                                .addValue(
                                        INVOICE_DATA.INVOICE_STATUS,
                                        searchQuery.isSetInvoiceStatus()
                                                ? TBaseUtil.unionFieldToEnum(
                                                searchQuery.getInvoiceStatus(),
                                                com.rbkmoney.magista.domain.enums.InvoiceStatus.class)
                                                : null,
                                        EQUALS)
                                .addValue(INVOICE_DATA.INVOICE_AMOUNT,
                                        searchQuery.isSetInvoiceAmount() ? searchQuery.getInvoiceAmount() : null,
                                        EQUALS)
                ),
                INVOICE_DATA.INVOICE_CREATED_AT,
                timeHolder.getFromTime(),
                timeHolder.getToTime()
        );

        ConditionParameterSource paymentParameterSource = new ConditionParameterSource();
        preparePaymentsCondition(paymentParameterSource, searchQuery.getPaymentParams(), searchQuery.getExternalId());
        if (!paymentParameterSource.getConditionFields().isEmpty()
                || !paymentParameterSource.getOrConditions().isEmpty()) {
            prepareInvoicePaymentsCondition(paymentParameterSource, commonParams, searchQuery.getInvoiceIds());
            condition = condition.and(DSL.exists(getDslContext()
                    .select(DSL.field("1"))
                    .from(PAYMENT_DATA)
                    .where(
                            appendDateTimeRangeConditions(
                                    appendConditions(
                                            INVOICE_DATA.INVOICE_ID.eq(PAYMENT_DATA.INVOICE_ID),
                                            Operator.AND,
                                            paymentParameterSource),
                                    PAYMENT_DATA.PAYMENT_CREATED_AT,
                                    timeHolder.getFromTime(),
                                    timeHolder.getToTime()))));
        }

        Query query = getDslContext()
                .selectFrom(INVOICE_DATA)
                .where(condition)
                .orderBy(INVOICE_DATA.INVOICE_CREATED_AT.desc())
                .limit(commonParams.getLimit());
        return fetch(query, statInvoiceMapper);
    }

    @Override
    public List<StatPayment> getPayments(PaymentSearchQuery searchQuery) {
        CommonSearchQueryParams commonParams = searchQuery.getCommonSearchQueryParams();
        TimeHolder timeHolder = buildTimeHolder(commonParams);
        PaymentParams paymentParams = searchQuery.getPaymentParams();
        ConditionParameterSource conditionParameterSource = new ConditionParameterSource();
        prepareInvoicePaymentsCondition(conditionParameterSource, commonParams, searchQuery.getInvoiceIds());
        preparePaymentsCondition(conditionParameterSource, paymentParams, searchQuery.getExternalId());
        conditionParameterSource.addValue(PAYMENT_DATA.PAYMENT_CREATED_AT, timeHolder.getWhereTime(), LESS);

        SelectConditionStep<org.jooq.Record> conditionStep = getDslContext()
                .select()
                .from(PAYMENT_DATA)
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND, conditionParameterSource),
                                PAYMENT_DATA.PAYMENT_CREATED_AT,
                                timeHolder.getFromTime(),
                                timeHolder.getToTime()
                        )
                );
        if (searchQuery.isSetExcludedShopIds()) {
            conditionStep.and(PAYMENT_DATA.PARTY_SHOP_ID.notIn(searchQuery.getExcludedShopIds()));
        }
        Query query = conditionStep.orderBy(PAYMENT_DATA.PAYMENT_CREATED_AT.desc())
                .limit(commonParams.getLimit());

        return fetch(query, statPaymentMapper);
    }

    @Override
    public List<StatRefund> getRefunds(RefundSearchQuery refundSearchQuery) {
        CommonSearchQueryParams commonParams = refundSearchQuery.getCommonSearchQueryParams();
        TimeHolder timeHolder = buildTimeHolder(commonParams);
        ConditionParameterSource refundParameterSource = prepareRefundCondition(refundSearchQuery, timeHolder);
        Query query = getDslContext().selectFrom(REFUND_DATA)
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND, refundParameterSource),
                                REFUND_DATA.REFUND_CREATED_AT,
                                timeHolder.getFromTime(),
                                timeHolder.getToTime()
                        )
                )
                .orderBy(REFUND_DATA.REFUND_CREATED_AT.desc())
                .limit(commonParams.getLimit());
        return fetch(query, statRefundMapper);
    }

    private ConditionParameterSource prepareRefundCondition(RefundSearchQuery searchQuery, TimeHolder timeHolder) {
        CommonSearchQueryParams commonParams = searchQuery.getCommonSearchQueryParams();
        return new ConditionParameterSource()
                .addValue(REFUND_DATA.PARTY_ID, commonParams.getPartyId(), EQUALS)
                .addInConditionValue(REFUND_DATA.PARTY_SHOP_ID, commonParams.getShopIds())
                .addInConditionValue(REFUND_DATA.INVOICE_ID, searchQuery.getInvoiceIds())
                .addValue(REFUND_DATA.PAYMENT_ID, searchQuery.getPaymentId(), EQUALS)
                .addValue(REFUND_DATA.REFUND_ID, searchQuery.getRefundId(), EQUALS)
                .addValue(REFUND_DATA.EXTERNAL_ID, searchQuery.getExternalId(), EQUALS)
                .addValue(REFUND_DATA.REFUND_CREATED_AT, timeHolder.getWhereTime(), LESS)
                .addValue(REFUND_DATA.REFUND_STATUS,
                        searchQuery.isSetRefundStatus()
                                ? TBaseUtil.unionFieldToEnum(searchQuery.getRefundStatus(), RefundStatus.class)
                                : null,
                        EQUALS);
    }

    @Override
    public List<StatPayout> getPayouts(PayoutSearchQuery payoutSearchQuery) {
        CommonSearchQueryParams commonParams = payoutSearchQuery.getCommonSearchQueryParams();
        TimeHolder timeHolder = buildTimeHolder(commonParams);
        Query query = getDslContext().selectFrom(PAYOUT)
                .where(
                        appendDateTimeRangeConditions(
                                appendConditions(
                                        DSL.trueCondition(),
                                        Operator.AND,
                                        preparePayoutCondition(payoutSearchQuery, timeHolder)
                                ),
                                PAYOUT.CREATED_AT,
                                timeHolder.getFromTime(),
                                timeHolder.getToTime()
                        )
                ).orderBy(PAYOUT.CREATED_AT.desc())
                .limit(commonParams.getLimit());

        return fetch(query, statPayoutMapper);
    }

    private ConditionParameterSource preparePayoutCondition(PayoutSearchQuery payoutSearchQuery,
                                                            TimeHolder timeHolder) {
        CommonSearchQueryParams commonParams = payoutSearchQuery.getCommonSearchQueryParams();
        ConditionParameterSource conditionParameterSource = new ConditionParameterSource()
                .addValue(PAYOUT.PARTY_ID, commonParams.getPartyId(), EQUALS)
                .addInConditionValue(PAYOUT.SHOP_ID, commonParams.getShopIds())
                .addValue(PAYOUT.PAYOUT_ID, payoutSearchQuery.getPayoutId(), EQUALS)
                .addInConditionValue(PAYOUT.STATUS,
                        payoutSearchQuery.isSetPayoutStatuses()
                                ? toEnumFields(payoutSearchQuery.getPayoutStatuses().stream()
                                        .map(ps -> ps.getSetField().getFieldName())
                                        .collect(Collectors.toList()),
                                PayoutStatus.class)
                                : null)
                .addValue(PAYOUT.CREATED_AT, timeHolder.getWhereTime(), LESS);
        if (payoutSearchQuery.getPayoutType() != null) {
            switch (payoutSearchQuery.getPayoutType().getSetField().getFieldName()) {
                case "bank_account" -> conditionParameterSource.addOrCondition(
                        PAYOUT.PAYOUT_TOOL_TYPE.eq(PayoutToolType.russian_bank_account),
                        PAYOUT.PAYOUT_TOOL_TYPE.eq(PayoutToolType.international_bank_account));
                case "wallet_info",
                        "payment_institution_account",
                        "russian_bank_account",
                        "international_bank_account" -> conditionParameterSource.addValue(PAYOUT.PAYOUT_TOOL_TYPE,
                        TBaseUtil.unionFieldToEnum(payoutSearchQuery.getPayoutType(), PayoutToolType.class),
                        EQUALS);
                default -> throw new IllegalArgumentException("Unknown payout_type " +
                        payoutSearchQuery.getPayoutType());
            }
        }
        return conditionParameterSource;
    }

    @Override
    public List<StatChargeback> getChargebacks(ChargebackSearchQuery chargebackSearchQuery) {
        CommonSearchQueryParams commonParams = chargebackSearchQuery.getCommonSearchQueryParams();
        TimeHolder timeHolder = buildTimeHolder(commonParams);
        Query query = getDslContext().selectFrom(CHARGEBACK_DATA).where(
                appendDateTimeRangeConditions(appendConditions(
                        DSL.trueCondition(),
                        Operator.AND,
                        new ConditionParameterSource()
                                .addValue(CHARGEBACK_DATA.PARTY_ID, commonParams.getPartyId(), EQUALS)
                                .addInConditionValue(CHARGEBACK_DATA.PARTY_SHOP_ID,
                                        commonParams.getShopIds())
                                .addInConditionValue(
                                        CHARGEBACK_DATA.INVOICE_ID,
                                        chargebackSearchQuery.getInvoiceIds())
                                .addValue(CHARGEBACK_DATA.PAYMENT_ID,
                                        chargebackSearchQuery.getPaymentId(), EQUALS)
                                .addValue(CHARGEBACK_DATA.CHARGEBACK_ID,
                                        chargebackSearchQuery.getChargebackId(),
                                        EQUALS)
                                .addInConditionValue(CHARGEBACK_DATA.CHARGEBACK_STATUS,
                                        chargebackSearchQuery.isSetChargebackStatuses()
                                                ? toEnumFields(
                                                        chargebackSearchQuery.getChargebackStatuses()
                                                                .stream()
                                                                .map(cs -> cs.getSetField().getFieldName())
                                                                .collect(Collectors.toList()),
                                                        ChargebackStatus.class)
                                                : null)
                                .addInConditionValue(CHARGEBACK_DATA.CHARGEBACK_STAGE,
                                        chargebackSearchQuery.isSetChargebackStages()
                                                ? toEnumFields(chargebackSearchQuery.getChargebackStages()
                                                                .stream()
                                                                .map(cs -> cs.getSetField().getFieldName())
                                                                .collect(Collectors.toList()),
                                                        ChargebackStage.class)
                                                : null)
                                .addInConditionValue(CHARGEBACK_DATA.CHARGEBACK_REASON_CATEGORY,
                                        chargebackSearchQuery.isSetChargebackCategories()
                                                ? toEnumFields(chargebackSearchQuery.getChargebackCategories()
                                                                .stream()
                                                                .map(cc -> cc.getSetField().getFieldName())
                                                                .collect(Collectors.toList()),
                                                        ChargebackCategory.class)
                                                : null)
                                .addValue(CHARGEBACK_DATA.CHARGEBACK_CREATED_AT, timeHolder.getWhereTime(), LESS)
                        ),
                        CHARGEBACK_DATA.CHARGEBACK_CREATED_AT,
                        timeHolder.getFromTime(),
                        timeHolder.getToTime()
                )
        ).orderBy(CHARGEBACK_DATA.CHARGEBACK_CREATED_AT.desc())
                .limit(commonParams.getLimit());

        return fetch(query, statChargebackMapper);
    }

    /**
     * merchant OKKO-specific, in general shouldn't be touched.
     *
     * @author n.pospolitych
     */
    @Override
    public List<EnrichedStatInvoice> getEnrichedInvoices(
            com.rbkmoney.magista.okko.RefundSearchQuery refundSearchQuery) {
        CommonSearchQueryParams commonParams = refundSearchQuery.getCommonSearchQueryParams();
        TimeHolder timeHolder = buildTimeHolder(commonParams);
        ConditionParameterSource refundParameterSource = prepareEnrichedRefundCondition(refundSearchQuery, timeHolder);
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
                                timeHolder.getFromTime(),
                                timeHolder.getToTime()
                        )
                )
                .orderBy(REFUND_DATA.EVENT_CREATED_AT.desc())
                .limit(commonParams.getLimit());

        return fetch(query, enrichedStatInvoiceMapper);
    }

    /**
     * merchant OKKO-specific, in general shouldn't be touched.
     *
     * @author n.pospolita
     */
    @Override
    public List<EnrichedStatInvoice> getEnrichedInvoices(
            com.rbkmoney.magista.okko.PaymentSearchQuery paymentSearchQuery) {
        CommonSearchQueryParams commonParams = paymentSearchQuery.getCommonSearchQueryParams();
        TimeHolder timeHolder = buildTimeHolder(commonParams);
        var conditionParameterSource = prepareEnrichedPaymentsCondition(paymentSearchQuery, timeHolder);
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
                                timeHolder.getFromTime(),
                                timeHolder.getToTime()
                        )
                )
                .orderBy(PAYMENT_DATA.EVENT_CREATED_AT.desc())
                .limit(commonParams.getLimit());

        return fetch(query, enrichedStatInvoiceMapper);
    }

    private void preparePaymentsCondition(ConditionParameterSource conditionParameterSource,
                                                              PaymentParams paymentParams,
                                                              String externalId) {
        conditionParameterSource
                .addValue(PAYMENT_DATA.PAYMENT_ID, paymentParams.getPaymentId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_STATUS,
                        paymentParams.isSetPaymentStatus()
                                ? TBaseUtil.unionFieldToEnum(paymentParams.getPaymentStatus(),
                                InvoicePaymentStatus.class)
                                : null,
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FLOW,
                        TBaseUtil.unionFieldToEnum(paymentParams.getPaymentFlow(), PaymentFlow.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TOOL,
                        TBaseUtil.unionFieldToEnum(paymentParams.getPaymentTool(), PaymentTool.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TERMINAL_PROVIDER,
                        paymentParams.getPaymentTerminalProvider().name(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_EMAIL, paymentParams.getPaymentEmail(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_IP, paymentParams.getPaymentIp(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FINGERPRINT, paymentParams.getPaymentFingerprint(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_FIRST6, paymentParams.getPaymentFirst6(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_SYSTEM, paymentParams.getPaymentSystem().name(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_LAST4, paymentParams.getPaymentLast4(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_CUSTOMER_ID, paymentParams.getPaymentCustomerId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_PROVIDER_ID, paymentParams.getPaymentProviderId() != null
                        ? Integer.valueOf(paymentParams.getPaymentProviderId())
                        : null, EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TERMINAL_ID, paymentParams.getPaymentTerminalId() != null
                        ? Integer.valueOf(paymentParams.getPaymentTerminalId())
                        : null, EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_AMOUNT, paymentParams.getPaymentAmount(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, paymentParams.getPaymentDomainRevision(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, paymentParams.getFromPaymentDomainRevision(),
                        GREATER_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION,
                        paymentParams.getToPaymentDomainRevision(),
                        LESS_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_RRN, paymentParams.getPaymentRrn(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_APPROVAL_CODE, paymentParams.getPaymentApprovalCode(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_AMOUNT, paymentParams.getPaymentAmountFrom(), GREATER_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_AMOUNT, paymentParams.getPaymentAmountTo(), LESS_OR_EQUAL)
                .addValue(PAYMENT_DATA.EXTERNAL_ID, externalId, EQUALS);
        if (paymentParams.isSetPaymentTokenProvider()) {
            conditionParameterSource.addOrCondition(
                    PAYMENT_DATA.PAYMENT_BANK_CARD_TOKEN_PROVIDER
                            .eq(paymentParams.getPaymentTokenProvider().name()),
                    PAYMENT_DATA.PAYMENT_BANK_CARD_TOKEN_PROVIDER_LEGACY.eq(
                            toEnumField(paymentParams.getPaymentTokenProvider().name(),
                                    com.rbkmoney.magista.domain.enums.BankCardTokenProvider.class)));
        }
    }

    private void prepareInvoicePaymentsCondition(ConditionParameterSource paymentParameterSource,
                                                                     CommonSearchQueryParams commonParams,
                                                                     List<String> invoiceIds) {
        paymentParameterSource
                .addValue(PAYMENT_DATA.PARTY_ID, UUID.fromString(commonParams.getPartyId()), EQUALS)
                .addInConditionValue(PAYMENT_DATA.PARTY_SHOP_ID, commonParams.getShopIds())
                .addInConditionValue(PAYMENT_DATA.INVOICE_ID, invoiceIds);
    }

    /**
     * merchant OKKO-specific, in general shouldn't be touched.
     *
     * @author n.pospolita
     */
    private ConditionParameterSource prepareEnrichedPaymentsCondition(
            com.rbkmoney.magista.okko.PaymentSearchQuery paymentSearchQuery,
            TimeHolder timeHolder) {
        CommonSearchQueryParams commonParams = paymentSearchQuery.getCommonSearchQueryParams();
        ConditionParameterSource conditionParameterSource = new ConditionParameterSource()
                .addValue(
                        PAYMENT_DATA.PARTY_ID,
                        Optional.ofNullable(commonParams.getPartyId())
                                .map(UUID::fromString)
                                .orElse(null),
                        EQUALS
                )
                .addInConditionValue(PAYMENT_DATA.PARTY_SHOP_ID, commonParams.getShopIds())
                .addValue(PAYMENT_DATA.INVOICE_ID, paymentSearchQuery.getInvoiceId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_ID, paymentSearchQuery.getPaymentId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_STATUS,
                        TBaseUtil.unionFieldToEnum(paymentSearchQuery.getPaymentStatus(),
                                com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FLOW,
                        TBaseUtil.unionFieldToEnum(paymentSearchQuery.getPaymentFlow(), PaymentFlow.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TOOL,
                        TBaseUtil.unionFieldToEnum(paymentSearchQuery.getPaymentTool(), PaymentTool.class),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_TERMINAL_PROVIDER,
                        paymentSearchQuery.getPaymentTerminalProvider().name(),
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_EMAIL, paymentSearchQuery.getPaymentEmail(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_IP, paymentSearchQuery.getPaymentIp(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FINGERPRINT, paymentSearchQuery.getPaymentFingerprint(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_FIRST6, paymentSearchQuery.getPaymentFirst6(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_SYSTEM, paymentSearchQuery.getPaymentSystem().name(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_LAST4, paymentSearchQuery.getPaymentLast4(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_CUSTOMER_ID, paymentSearchQuery.getPaymentCustomerId(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_AMOUNT,
                        paymentSearchQuery.isSetPaymentAmount()
                                ? paymentSearchQuery.getPaymentAmount()
                                : null,
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION,
                        paymentSearchQuery.isSetPaymentDomainRevision()
                                ? paymentSearchQuery.getPaymentDomainRevision()
                                : null,
                        EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION,
                        paymentSearchQuery.isSetFromPaymentDomainRevision()
                                ? paymentSearchQuery.getFromPaymentDomainRevision()
                                : null,
                        GREATER_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION,
                        paymentSearchQuery.isSetToPaymentDomainRevision()
                                ? paymentSearchQuery.getToPaymentDomainRevision()
                                : null,
                        LESS_OR_EQUAL)
                .addValue(PAYMENT_DATA.EVENT_CREATED_AT, timeHolder.getWhereTime(), LESS)
                .addValue(PAYMENT_DATA.PAYMENT_RRN, paymentSearchQuery.getPaymentRrn(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_APPROVAL_CODE, paymentSearchQuery.getPaymentApprovalCode(), EQUALS)
                .addValue(PAYMENT_DATA.EXTERNAL_ID, paymentSearchQuery.getExternalId(), EQUALS);
        if (!ObjectUtils.isEmpty(paymentSearchQuery.getPaymentTokenProvider())) {
            conditionParameterSource.addOrCondition(
                    PAYMENT_DATA.PAYMENT_BANK_CARD_TOKEN_PROVIDER
                            .eq(paymentSearchQuery.getPaymentTokenProvider().name()),
                    PAYMENT_DATA.PAYMENT_BANK_CARD_TOKEN_PROVIDER_LEGACY.eq(
                            toEnumField(paymentSearchQuery.getPaymentTokenProvider().name(),
                                    com.rbkmoney.magista.domain.enums.BankCardTokenProvider.class)));
        }

        return conditionParameterSource;
    }

    /**
     * merchant OKKO-specific, in general shouldn't be touched
     *
     * @author n.pospolita
     */
    private ConditionParameterSource prepareEnrichedRefundCondition(
            com.rbkmoney.magista.okko.RefundSearchQuery refundSearchQuery,
            TimeHolder timeHolder) {
        CommonSearchQueryParams commonParams = refundSearchQuery.getCommonSearchQueryParams();
        return new ConditionParameterSource()
                .addValue(REFUND_DATA.PARTY_ID, commonParams.getPartyId(), EQUALS)
                .addInConditionValue(REFUND_DATA.PARTY_SHOP_ID, commonParams.getShopIds())
                .addValue(REFUND_DATA.INVOICE_ID, refundSearchQuery.getInvoiceId(), EQUALS)
                .addValue(REFUND_DATA.PAYMENT_ID, refundSearchQuery.getPaymentId(), EQUALS)
                .addValue(REFUND_DATA.REFUND_ID, refundSearchQuery.getRefundId(), EQUALS)
                .addValue(REFUND_DATA.EXTERNAL_ID, refundSearchQuery.getExternalId(), EQUALS)
                .addValue(REFUND_DATA.EVENT_CREATED_AT, timeHolder.getWhereTime(), LESS)
                .addValue(REFUND_DATA.REFUND_STATUS,
                        refundSearchQuery.isSetRefundStatus()
                                ? TBaseUtil.unionFieldToEnum(refundSearchQuery.getRefundStatus(), RefundStatus.class)
                                : null,
                        EQUALS);
    }

    private TimeHolder buildTimeHolder(CommonSearchQueryParams commonParams) {
        return TimeHolder.builder()
                .fromTime(TypeUtil.stringToLocalDateTime(commonParams.getFromTime()))
                .toTime(TypeUtil.stringToLocalDateTime(commonParams.getToTime()))
                .whereTime(tokenGenService.extractTime(commonParams.getContinuationToken()))
                .build();
    }
}
