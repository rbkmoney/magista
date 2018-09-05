package com.rbkmoney.magista.dao.mapper;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.BankCardTokenProvider;
import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.domain.enums.PaymentFlow;
import com.rbkmoney.magista.domain.enums.RecurrentTokenSourceType;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.util.DamselUtil;
import org.jooq.TableField;
import org.springframework.jdbc.core.RowMapper;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;

import static com.rbkmoney.magista.domain.Tables.PAYMENT_EVENT;
import static com.rbkmoney.magista.domain.tables.PaymentData.PAYMENT_DATA;

public class StatPaymentMapper implements RowMapper<Map.Entry<Long, StatPayment>> {

    private final TableField idField;

    public StatPaymentMapper() {
        this.idField = PAYMENT_DATA.ID;
    }

    public StatPaymentMapper(TableField idField) {
        this.idField = idField;
    }


    @Override
    public Map.Entry<Long, StatPayment> mapRow(ResultSet rs, int i) throws SQLException {
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
        statPayment.setIsRecurring(rs.getBoolean(PAYMENT_DATA.PAYMENT_RECURRENT_FLAG.getName()));
        RecurrentTokenSourceType recurrentTokenSourceType = TypeUtil.toEnumField(rs.getString(PAYMENT_DATA.PAYMENT_RECURRENT_TOKEN_SOURCE_TYPE.getName()), RecurrentTokenSourceType.class);
        if (recurrentTokenSourceType != null) {
            RecurrentIntention recurrentIntention = new RecurrentIntention();
            switch (recurrentTokenSourceType) {
                case payment:
                    recurrentIntention.setTokenSource(RecurrentTokenSource.payment(
                            new PaymentRecurrentTokenSource(
                                    rs.getString(PAYMENT_DATA.PAYMENT_RECURRENT_TOKEN_SOURCE_INVOICE_ID.getName()),
                                    rs.getString(PAYMENT_DATA.PAYMENT_RECURRENT_TOKEN_SOURCE_PAYMENT_ID.getName())
                            )
                    ));
                    break;
                default:
                    throw new NotFoundException(String.format("Recurrent token source type '%s' not found", recurrentTokenSourceType.getLiteral()));
            }
            statPayment.setRecurrentIntention(recurrentIntention);
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

        return new AbstractMap.SimpleEntry<>(rs.getLong(idField.getName()), statPayment);
    }
}
