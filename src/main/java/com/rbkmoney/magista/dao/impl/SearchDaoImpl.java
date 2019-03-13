package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.damsel.merch_stat.StatInvoice;
import com.rbkmoney.damsel.merch_stat.StatPayment;
import com.rbkmoney.damsel.merch_stat.StatPayout;
import com.rbkmoney.damsel.merch_stat.StatRefund;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.dao.impl.field.ConditionParameterSource;
import com.rbkmoney.magista.dao.impl.mapper.StatInvoiceMapper;
import com.rbkmoney.magista.dao.impl.mapper.StatPaymentMapper;
import com.rbkmoney.magista.dao.impl.mapper.StatPayoutMapper;
import com.rbkmoney.magista.dao.impl.mapper.StatRefundMapper;
import com.rbkmoney.magista.domain.enums.*;
import com.rbkmoney.magista.domain.tables.InvoiceEvent;
import com.rbkmoney.magista.domain.tables.PaymentData;
import com.rbkmoney.magista.domain.tables.PaymentEvent;
import com.rbkmoney.magista.domain.tables.PayoutEventStat;
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
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.rbkmoney.geck.common.util.TypeUtil.*;
import static com.rbkmoney.magista.domain.Tables.PAYOUT_EVENT_STAT;
import static com.rbkmoney.magista.domain.Tables.REFUND;
import static com.rbkmoney.magista.domain.tables.InvoiceData.INVOICE_DATA;
import static com.rbkmoney.magista.domain.tables.InvoiceEvent.INVOICE_EVENT;
import static com.rbkmoney.magista.domain.tables.PaymentData.PAYMENT_DATA;
import static com.rbkmoney.magista.domain.tables.PaymentEvent.PAYMENT_EVENT;
import static org.jooq.Comparator.*;

@Component
public class SearchDaoImpl extends AbstractDao implements SearchDao {

    private final StatInvoiceMapper statInvoiceMapper;
    private final StatPaymentMapper statPaymentMapper;
    private final StatRefundMapper statRefundMapper;
    private final StatPayoutMapper statPayoutMapper;

    public SearchDaoImpl(DataSource ds) {
        super(ds);
        statInvoiceMapper = new StatInvoiceMapper();
        statPaymentMapper = new StatPaymentMapper();
        statRefundMapper = new StatRefundMapper();
        statPayoutMapper = new StatPayoutMapper();
    }

    @Override
    public Collection<Map.Entry<Long, StatInvoice>> getInvoices(
            InvoicesFunction.InvoicesParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            int limit
    ) throws DaoException {
        InvoiceEvent invoiceEvent = INVOICE_EVENT.as("invoice_event");

        SelectOnConditionStep selectOnConditionStep = getDslContext()
                .select()
                .from(INVOICE_DATA)
                .join(
                        DSL.lateral(
                                getDslContext()
                                        .selectFrom(INVOICE_EVENT)
                                        .where(
                                                INVOICE_DATA.INVOICE_ID.eq(INVOICE_EVENT.INVOICE_ID)
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
                .addValue(paymentData.PAYMENT_BANK_CARD_TOKEN_PROVIDER,
                        toEnumField(parameters.getPaymentBankCardTokenProvider(), com.rbkmoney.magista.domain.enums.BankCardTokenProvider.class),
                        EQUALS)
                .addValue(paymentData.PAYMENT_TERMINAL_PROVIDER, parameters.getPaymentTerminalProvider(), EQUALS)
                .addValue(paymentData.PAYMENT_EMAIL, parameters.getPaymentEmail(), EQUALS)
                .addValue(paymentData.PAYMENT_IP, parameters.getPaymentIp(), EQUALS)
                .addValue(paymentData.PAYMENT_FINGERPRINT, parameters.getPaymentFingerprint(), EQUALS)
                .addValue(paymentData.PAYMENT_BANK_CARD_BIN, parameters.getPaymentBankCardBin(), EQUALS)
                .addValue(paymentData.PAYMENT_BANK_CARD_MASKED_PAN, parameters.getPaymentBankCardLastDigits(), EQUALS)
                .addValue(paymentData.PAYMENT_CUSTOMER_ID, parameters.getPaymentCustomerId(), EQUALS);

        ConditionParameterSource paymentEventParameterSource = new ConditionParameterSource()
                .addValue(paymentEvent.PAYMENT_STATUS,
                        TypeUtil.toEnumField(parameters.getPaymentStatus(), com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class),
                        EQUALS)
                .addValue(paymentEvent.PAYMENT_DOMAIN_REVISION, parameters.getPaymentDomainRevision(), EQUALS)
                .addValue(paymentEvent.PAYMENT_AMOUNT, parameters.getPaymentAmount(), EQUALS)
                .addValue(paymentEvent.PAYMENT_DOMAIN_REVISION, parameters.getFromPaymentDomainRevision(), GREATER_OR_EQUAL)
                .addValue(paymentEvent.PAYMENT_DOMAIN_REVISION, parameters.getToPaymentDomainRevision(), LESS_OR_EQUAL);

        if (!paymentParameterSource.getConditionFields().isEmpty() || !paymentEventParameterSource.getConditionFields().isEmpty()) {
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
                    appendConditions(
                            DSL.trueCondition(),
                            Operator.AND,
                            paymentEventParameterSource
                    )
            );
        }

        Query query = selectOnConditionStep
                .orderBy(INVOICE_DATA.INVOICE_CREATED_AT.desc())
                .limit(limit);
        return fetch(query, statInvoiceMapper);
    }

    @Override
    public Collection<Map.Entry<Long, StatPayment>> getPayments(
            PaymentsFunction.PaymentsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            int limit
    ) throws DaoException {

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
                .addValue(PAYMENT_DATA.PAYMENT_EMAIL, parameters.getPaymentEmail(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_IP, parameters.getPaymentIp(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_FINGERPRINT, parameters.getPaymentFingerprint(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_BIN, parameters.getPaymentBankCardBin(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_SYSTEM, parameters.getPaymentBankCardSystem(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_BIN, parameters.getPaymentBankCardBin(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_BANK_CARD_MASKED_PAN, parameters.getPaymentBankCardLastDigits(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_CUSTOMER_ID, parameters.getPaymentCustomerId(), EQUALS)
                .addValue(paymentEvent.PAYMENT_AMOUNT, parameters.getPaymentAmount(), EQUALS)
                .addValue(paymentEvent.PAYMENT_DOMAIN_REVISION, parameters.getPaymentDomainRevision(), EQUALS)
                .addValue(paymentEvent.PAYMENT_DOMAIN_REVISION, parameters.getFromPaymentDomainRevision(), GREATER_OR_EQUAL)
                .addValue(paymentEvent.PAYMENT_DOMAIN_REVISION, parameters.getToPaymentDomainRevision(), LESS_OR_EQUAL);

        Query query = getDslContext()
                .select()
                .from(PAYMENT_DATA)
                .join(
                        DSL.lateral(
                                getDslContext()
                                        .selectFrom(PAYMENT_EVENT)
                                        .where(
                                                PAYMENT_DATA.INVOICE_ID.eq(PAYMENT_EVENT.INVOICE_ID)
                                                        .and(PAYMENT_DATA.PAYMENT_ID.eq(PAYMENT_EVENT.PAYMENT_ID))
                                        ).orderBy(PAYMENT_EVENT.ID.desc())
                                        .limit(1)
                        ).as(paymentEvent)
                ).on(
                        appendDateTimeRangeConditions(
                                appendConditions(DSL.trueCondition(), Operator.AND, conditionParameterSource),
                                PAYMENT_DATA.PAYMENT_CREATED_AT,
                                fromTime,
                                toTime
                        )
                ).orderBy(PAYMENT_DATA.PAYMENT_CREATED_AT.desc())
                .limit(limit);

        return fetch(query, statPaymentMapper);
    }

    @Override
    public Collection<Map.Entry<Long, StatRefund>> getRefunds(
            RefundsFunction.RefundsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            Optional<Integer> offset,
            int limit
    ) throws DaoException {
        Query query = getDslContext().selectFrom(REFUND)
                .where(buildRefundCondition(parameters, fromTime, toTime))
                .orderBy(REFUND.REFUND_CREATED_AT.desc())
                .limit(limit)
                .offset(offset.orElse(0));
        return fetch(query, statRefundMapper);
    }

    @Override
    public Integer getRefundsCount(
            RefundsFunction.RefundsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) throws DaoException {
        Query query = getDslContext().select(DSL.count()).from(REFUND)
                .where(buildRefundCondition(parameters, fromTime, toTime));
        return fetchOne(query, Integer.class);
    }

    @Override
    public Collection<Map.Entry<Long, StatPayout>> getPayouts(
            PayoutsFunction.PayoutsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime,
            Optional<Long> fromId,
            Optional<Integer> offset,
            int limit
    ) throws DaoException {
        Query query = buildPayoutSelectConditionStepQuery(parameters)
                .orderBy(PAYOUT_EVENT_STAT.PAYOUT_CREATED_AT.desc())
                .limit(limit)
                .offset(offset.orElse(0));

        return fetch(query, statPayoutMapper);
    }

    @Override
    public Integer getPayoutsCount(
            PayoutsFunction.PayoutsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) throws DaoException {
        Query query = buildPayoutSelectConditionStepQuery(parameters, DSL.count());
        return fetchOne(query, Integer.class);
    }

    private Condition buildRefundCondition(
            RefundsFunction.RefundsParameters parameters,
            Optional<LocalDateTime> fromTime,
            Optional<LocalDateTime> toTime
    ) {
        Condition condition = DSL.trueCondition();
        if (parameters.getMerchantId() != null) {
            condition = condition.and(REFUND.PARTY_ID.eq(parameters.getMerchantId()));
        }
        if (parameters.getShopId() != null) {
            condition = condition.and(REFUND.PARTY_SHOP_ID.eq(parameters.getShopId()));
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
        return appendConditions(condition, Operator.AND,
                new ConditionParameterSource()
                        .addValue(com.rbkmoney.magista.domain.tables.Refund.REFUND.PARTY_ID, parameters.getMerchantId(), EQUALS)
                        .addValue(com.rbkmoney.magista.domain.tables.Refund.REFUND.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                        .addValue(com.rbkmoney.magista.domain.tables.Refund.REFUND.INVOICE_ID, parameters.getInvoiceId(), EQUALS)
                        .addValue(com.rbkmoney.magista.domain.tables.Refund.REFUND.PAYMENT_ID, parameters.getPaymentId(), EQUALS)
                        .addValue(com.rbkmoney.magista.domain.tables.Refund.REFUND.REFUND_ID, parameters.getRefundId(), EQUALS)
                        .addValue(com.rbkmoney.magista.domain.tables.Refund.REFUND.REFUND_STATUS,
                                toEnumField(parameters.getRefundStatus(), RefundStatus.class),
                                EQUALS)
        );
    }

    private SelectConditionStep buildPayoutSelectConditionStepQuery(
            PayoutsFunction.PayoutsParameters parameters,
            SelectField<?>... fields) {
        Condition condition = PAYOUT_EVENT_STAT.EVENT_CATEGORY.eq(PayoutEventCategory.PAYOUT);

        condition = PAYOUT_EVENT_STAT.ID.in(
                getDslContext().select(DSL.max(PAYOUT_EVENT_STAT.ID)).from(PAYOUT_EVENT_STAT)
                        .where(condition).groupBy(PAYOUT_EVENT_STAT.PAYOUT_ID)
        );

        condition = appendConditions(condition, Operator.AND,
                new ConditionParameterSource()
                        .addValue(PayoutEventStat.PAYOUT_EVENT_STAT.PARTY_ID, parameters.getMerchantId(), EQUALS)
                        .addValue(PayoutEventStat.PAYOUT_EVENT_STAT.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                        .addValue(PayoutEventStat.PAYOUT_EVENT_STAT.PAYOUT_ID, parameters.getPayoutId(), EQUALS)
                        .addValue(PayoutEventStat.PAYOUT_EVENT_STAT.PAYOUT_STATUS,
                                toEnumField(parameters.getPayoutStatus(), PayoutStatus.class),
                                EQUALS)
                        .addInConditionValue(PayoutEventStat.PAYOUT_EVENT_STAT.PAYOUT_STATUS,
                                toEnumFields(parameters.getPayoutStatuses(), PayoutStatus.class))
                        .addValue(PayoutEventStat.PAYOUT_EVENT_STAT.PAYOUT_TYPE,
                                toEnumField(parameters.getPayoutType(), PayoutType.class),
                                EQUALS)
                        .addValue(PayoutEventStat.PAYOUT_EVENT_STAT.PAYOUT_CREATED_AT, toLocalDateTime(parameters.getFromTime()), GREATER_OR_EQUAL)
                        .addValue(PayoutEventStat.PAYOUT_EVENT_STAT.PAYOUT_CREATED_AT, toLocalDateTime(parameters.getToTime()), LESS)
        );

        return getDslContext().select(fields).from(PAYOUT_EVENT_STAT)
                .where(condition);
    }
}
