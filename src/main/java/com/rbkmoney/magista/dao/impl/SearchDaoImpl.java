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
import com.rbkmoney.magista.domain.enums.PaymentFlow;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.PayoutType;
import com.rbkmoney.magista.domain.enums.RefundStatus;
import com.rbkmoney.magista.domain.tables.PaymentData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.query.impl.InvoicesFunction;
import com.rbkmoney.magista.query.impl.PaymentsFunction;
import com.rbkmoney.magista.query.impl.PayoutsFunction;
import com.rbkmoney.magista.query.impl.RefundsFunction;
import org.jooq.Condition;
import org.jooq.Operator;
import org.jooq.Query;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.rbkmoney.geck.common.util.TypeUtil.*;
import static com.rbkmoney.magista.domain.tables.InvoiceData.INVOICE_DATA;
import static com.rbkmoney.magista.domain.tables.PaymentData.PAYMENT_DATA;
import static com.rbkmoney.magista.domain.tables.PayoutData.PAYOUT_DATA;
import static com.rbkmoney.magista.domain.tables.RefundData.REFUND_DATA;
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

        SelectJoinStep selectJoinStep = getDslContext()
                .select()
                .from(INVOICE_DATA);

        PaymentData paymentData = PAYMENT_DATA.as("payment_data");
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
                .addValue(paymentData.PAYMENT_CUSTOMER_ID, parameters.getPaymentCustomerId(), EQUALS)
                .addValue(paymentData.PAYMENT_STATUS,
                        TypeUtil.toEnumField(parameters.getPaymentStatus(), com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class),
                        EQUALS)
                .addValue(paymentData.PAYMENT_DOMAIN_REVISION, parameters.getPaymentDomainRevision(), EQUALS)
                .addValue(paymentData.PAYMENT_AMOUNT, parameters.getPaymentAmount(), EQUALS)
                .addValue(paymentData.PAYMENT_DOMAIN_REVISION, parameters.getFromPaymentDomainRevision(), GREATER_OR_EQUAL)
                .addValue(paymentData.PAYMENT_DOMAIN_REVISION, parameters.getToPaymentDomainRevision(), LESS_OR_EQUAL);

        if (!paymentParameterSource.getConditionFields().isEmpty()) {
            selectJoinStep = selectJoinStep.join(paymentData).on(
                    appendConditions(
                            INVOICE_DATA.INVOICE_ID.eq(paymentData.INVOICE_ID),
                            Operator.AND,
                            paymentParameterSource
                    )
            );
        }

        selectJoinStep.where(
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
                )
        );

        Query query = selectJoinStep
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
                .addValue(PAYMENT_DATA.PAYMENT_STATUS,
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
                .addValue(PAYMENT_DATA.PAYMENT_AMOUNT, parameters.getPaymentAmount(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getPaymentDomainRevision(), EQUALS)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getFromPaymentDomainRevision(), GREATER_OR_EQUAL)
                .addValue(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION, parameters.getToPaymentDomainRevision(), LESS_OR_EQUAL);

        Query query = getDslContext()
                .select()
                .from(PAYMENT_DATA)
                .where(
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
        Condition condition = DSL.trueCondition();
        if (parameters.getMerchantId() != null) {
            condition = condition.and(REFUND_DATA.PARTY_ID.eq(parameters.getMerchantId()));
        }
        if (parameters.getShopId() != null) {
            condition = condition.and(REFUND_DATA.PARTY_SHOP_ID.eq(parameters.getShopId()));
        }

        condition = appendDateTimeRangeConditions(
                condition,
                REFUND_DATA.EVENT_CREATED_AT,
                fromTime,
                toTime
        );

        Query query = getDslContext().selectFrom(REFUND_DATA)
                .where(
                        appendConditions(condition, Operator.AND,
                                new ConditionParameterSource()
                                        .addValue(REFUND_DATA.PARTY_ID, parameters.getMerchantId(), EQUALS)
                                        .addValue(REFUND_DATA.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                                        .addValue(REFUND_DATA.INVOICE_ID, parameters.getInvoiceId(), EQUALS)
                                        .addValue(REFUND_DATA.PAYMENT_ID, parameters.getPaymentId(), EQUALS)
                                        .addValue(REFUND_DATA.REFUND_ID, parameters.getRefundId(), EQUALS)
                                        .addValue(REFUND_DATA.REFUND_STATUS,
                                                toEnumField(parameters.getRefundStatus(), RefundStatus.class),
                                                EQUALS)
                        )
                )
                .orderBy(REFUND_DATA.REFUND_CREATED_AT.desc())
                .limit(limit)
                .offset(offset.orElse(0));
        return fetch(query, statRefundMapper);
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
}
