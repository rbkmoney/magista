package com.rbkmoney.magista.dao.impl.mapper;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.BankCardTokenProvider;
import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.domain.enums.PaymentFlow;
import com.rbkmoney.magista.domain.enums.PaymentPayerType;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.util.DamselUtil;
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

    @Override
    public Map.Entry<Long, StatPayment> mapRow(ResultSet rs, int i) throws SQLException {
        StatPayment statPayment = new StatPayment();
        statPayment.setId(rs.getString(PAYMENT_DATA.PAYMENT_ID.getName()));
        statPayment.setInvoiceId(rs.getString(PAYMENT_DATA.INVOICE_ID.getName()));
        statPayment.setOwnerId(rs.getString(PAYMENT_DATA.PARTY_ID.getName()));
        statPayment.setShopId(rs.getString(PAYMENT_DATA.PARTY_SHOP_ID.getName()));
        statPayment.setAmount(rs.getLong(PAYMENT_EVENT.PAYMENT_AMOUNT.getName()));
        statPayment.setFee(rs.getLong(PAYMENT_EVENT.PAYMENT_FEE.getName()));
        statPayment.setCurrencySymbolicCode(rs.getString(PAYMENT_DATA.PAYMENT_CURRENCY_CODE.getName()));
        statPayment.setCreatedAt(
                TypeUtil.temporalToString(
                        rs.getObject(PAYMENT_DATA.PAYMENT_CREATED_AT.getName(), LocalDateTime.class)
                )
        );
        statPayment.setDomainRevision(rs.getLong(PAYMENT_EVENT.PAYMENT_DOMAIN_REVISION.getName()));

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
        statPayment.setPayer(buildPayer(rs));

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
        statPayment.setMakeRecurrent(rs.getBoolean(PAYMENT_DATA.PAYMENT_MAKE_RECURRENT_FLAG.getName()));

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
    }

    private Payer buildPayer(ResultSet rs) throws SQLException {
        PaymentPayerType paymentPayerType = TypeUtil.toEnumField(
                rs.getString(PAYMENT_DATA.PAYMENT_PAYER_TYPE.getName()),
                PaymentPayerType.class
        );

        switch (paymentPayerType) {
            case payment_resource:
                PaymentResourcePayer paymentResourcePayer = new PaymentResourcePayer();
                paymentResourcePayer.setIpAddress(rs.getString(PAYMENT_DATA.PAYMENT_IP.getName()));
                paymentResourcePayer.setFingerprint(rs.getString(PAYMENT_DATA.PAYMENT_FINGERPRINT.getName()));
                paymentResourcePayer.setPhoneNumber(rs.getString(PAYMENT_DATA.PAYMENT_PHONE_NUMBER.getName()));
                paymentResourcePayer.setEmail(rs.getString(PAYMENT_DATA.PAYMENT_EMAIL.getName()));
                paymentResourcePayer.setSessionId(rs.getString(PAYMENT_DATA.PAYMENT_SESSION_ID.getName()));

                paymentResourcePayer.setPaymentTool(buildPaymentTool(rs));
                return Payer.payment_resource(paymentResourcePayer);
            case customer:
                CustomerPayer customerPayer = new CustomerPayer();
                customerPayer.setCustomerId(rs.getString(PAYMENT_DATA.PAYMENT_CUSTOMER_ID.getName()));
                customerPayer.setPaymentTool(buildPaymentTool(rs));
                customerPayer.setEmail(rs.getString(PAYMENT_DATA.PAYMENT_EMAIL.getName()));
                customerPayer.setPhoneNumber(rs.getString(PAYMENT_DATA.PAYMENT_PHONE_NUMBER.getName()));
                return Payer.customer(customerPayer);
            case recurrent:
                RecurrentPayer recurrentPayer = new RecurrentPayer();
                recurrentPayer.setEmail(rs.getString(PAYMENT_DATA.PAYMENT_EMAIL.getName()));
                recurrentPayer.setPhoneNumber(rs.getString(PAYMENT_DATA.PAYMENT_PHONE_NUMBER.getName()));

                recurrentPayer.setPaymentTool(buildPaymentTool(rs));

                RecurrentParentPayment recurrentParentPayment = new RecurrentParentPayment();
                recurrentParentPayment.setInvoiceId(rs.getString(PAYMENT_DATA.PAYMENT_RECURRENT_PAYER_PARENT_INVOICE_ID.getName()));
                recurrentParentPayment.setPaymentId(rs.getString(PAYMENT_DATA.PAYMENT_RECURRENT_PAYER_PARENT_PAYMENT_ID.getName()));
                recurrentPayer.setRecurrentParent(recurrentParentPayment);

                return Payer.recurrent(recurrentPayer);
            default:
                throw new NotFoundException(String.format("Payment type '%s' not found", paymentPayerType));
        }
    }

    private PaymentTool buildPaymentTool(ResultSet rs) throws SQLException {
        com.rbkmoney.magista.domain.enums.PaymentTool paymentToolType = TypeUtil.toEnumField(
                rs.getString(PAYMENT_DATA.PAYMENT_TOOL.getName()),
                com.rbkmoney.magista.domain.enums.PaymentTool.class
        );

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
                return PaymentTool.bank_card(bankCard);
            case payment_terminal:
                return PaymentTool.payment_terminal(new PaymentTerminal(
                        TypeUtil.toEnumField(rs.getString(PAYMENT_DATA.PAYMENT_TERMINAL_PROVIDER.getName()), TerminalPaymentProvider.class)
                ));
            case digital_wallet:
                return PaymentTool.digital_wallet(new DigitalWallet(
                        TypeUtil.toEnumField(rs.getString(PAYMENT_DATA.PAYMENT_DIGITAL_WALLET_PROVIDER.getName()), DigitalWalletProvider.class),
                        rs.getString(PAYMENT_DATA.PAYMENT_DIGITAL_WALLET_ID.getName())
                ));
            case crypto_currency:
                return PaymentTool.crypto_currency(TypeUtil.toEnumField(rs.getString(PAYMENT_DATA.CRYPTO_CURRENCY.getName()), CryptoCurrency.class));
            case mobile_commerce:
                MobilePhone mobilePhone = new MobilePhone(rs.getString(PAYMENT_DATA.PAYMENT_MOBILE_PHONE_CC.getName()),
                        rs.getString(PAYMENT_DATA.PAYMENT_MOBILE_PHONE_CTN.getName()));
                MobileOperator mobileOperator = TypeUtil.toEnumField(rs.getString(PAYMENT_DATA.PAYMENT_MOBILE_OPERATOR.getName()),
                        MobileOperator.class);
                return PaymentTool.mobile_commerce(new MobileCommerce(mobileOperator, mobilePhone));
            default:
                throw new NotFoundException(String.format("Payment tool '%s' not found", paymentToolType));
        }
    }
}
