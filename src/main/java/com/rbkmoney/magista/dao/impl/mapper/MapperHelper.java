package com.rbkmoney.magista.dao.impl.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.BankCardTokenProvider;
import com.rbkmoney.damsel.domain.Residence;
import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.damsel.merch_stat.InvoicePaymentStatus;
import com.rbkmoney.damsel.merch_stat.InvoiceStatus;
import com.rbkmoney.damsel.merch_stat.OnHoldExpiration;
import com.rbkmoney.damsel.merch_stat.PaymentTool;
import com.rbkmoney.damsel.merch_stat.PayoutStatus;
import com.rbkmoney.damsel.merch_stat.PayoutType;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.*;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.util.DamselUtil;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.rbkmoney.magista.domain.tables.InvoiceData.INVOICE_DATA;
import static com.rbkmoney.magista.domain.tables.PaymentData.PAYMENT_DATA;
import static com.rbkmoney.magista.domain.tables.PayoutData.PAYOUT_DATA;
import static com.rbkmoney.magista.domain.tables.RefundData.REFUND_DATA;
import static com.rbkmoney.magista.util.DamselUtil.jsonToTBase;

public class MapperHelper {

    static InvoiceStatus mapInvoiceStatus(ResultSet rs, com.rbkmoney.magista.domain.enums.InvoiceStatus invoiceStatusType, String eventCreatedAtString) throws SQLException {
        InvoiceStatus invoiceStatus;
        switch (invoiceStatusType) {
            case cancelled:
                InvoiceCancelled invoiceCancelled = new InvoiceCancelled();
                invoiceCancelled.setDetails(rs.getString(INVOICE_DATA.INVOICE_STATUS_DETAILS.getName()));
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
                invoiceFulfilled.setDetails(rs.getString(INVOICE_DATA.INVOICE_STATUS_DETAILS.getName()));
                invoiceStatus = InvoiceStatus.fulfilled(invoiceFulfilled);
                break;
            default:
                throw new NotFoundException(String.format("Invoice status '%s' not found", invoiceStatusType.getLiteral()));
        }
        return invoiceStatus;
    }

    static PaymentTool buildPaymentTool(ResultSet rs) throws SQLException {
        com.rbkmoney.magista.domain.enums.PaymentTool paymentToolType = TypeUtil.toEnumField(
                rs.getString(PAYMENT_DATA.PAYMENT_TOOL.getName()),
                com.rbkmoney.magista.domain.enums.PaymentTool.class
        );

        switch (paymentToolType) {
            case bank_card:
                BankCard bankCard = new BankCard(
                        rs.getString(PAYMENT_DATA.PAYMENT_BANK_CARD_TOKEN.getName()),
                        TypeUtil.toEnumField(rs.getString(PAYMENT_DATA.PAYMENT_BANK_CARD_SYSTEM.getName()), BankCardPaymentSystem.class),
                        rs.getString(PAYMENT_DATA.PAYMENT_BANK_CARD_FIRST6.getName()),
                        rs.getString(PAYMENT_DATA.PAYMENT_BANK_CARD_LAST4.getName())
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
                MobilePhone mobilePhone = new MobilePhone(rs.getString(PAYMENT_DATA.PAYMENT_MOBILE_CC.getName()),
                        rs.getString(PAYMENT_DATA.PAYMENT_MOBILE_CTN.getName()));
                MobileOperator mobileOperator = TypeUtil.toEnumField(rs.getString(PAYMENT_DATA.PAYMENT_MOBILE_OPERATOR.getName()),
                        MobileOperator.class);
                return PaymentTool.mobile_commerce(new MobileCommerce(mobileOperator, mobilePhone));
            default:
                throw new NotFoundException(String.format("Payment tool '%s' not found", paymentToolType));
        }
    }

    static Payer buildPayer(ResultSet rs) throws SQLException {
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

                paymentResourcePayer.setPaymentTool(MapperHelper.buildPaymentTool(rs));
                return Payer.payment_resource(paymentResourcePayer);
            case customer:
                CustomerPayer customerPayer = new CustomerPayer();
                customerPayer.setCustomerId(rs.getString(PAYMENT_DATA.PAYMENT_CUSTOMER_ID.getName()));
                customerPayer.setPaymentTool(MapperHelper.buildPaymentTool(rs));
                customerPayer.setEmail(rs.getString(PAYMENT_DATA.PAYMENT_EMAIL.getName()));
                customerPayer.setPhoneNumber(rs.getString(PAYMENT_DATA.PAYMENT_PHONE_NUMBER.getName()));
                return Payer.customer(customerPayer);
            case recurrent:
                RecurrentPayer recurrentPayer = new RecurrentPayer();
                recurrentPayer.setEmail(rs.getString(PAYMENT_DATA.PAYMENT_EMAIL.getName()));
                recurrentPayer.setPhoneNumber(rs.getString(PAYMENT_DATA.PAYMENT_PHONE_NUMBER.getName()));

                recurrentPayer.setPaymentTool(MapperHelper.buildPaymentTool(rs));

                RecurrentParentPayment recurrentParentPayment = new RecurrentParentPayment();
                recurrentParentPayment.setInvoiceId(rs.getString(PAYMENT_DATA.PAYMENT_RECURRENT_PAYER_PARENT_INVOICE_ID.getName()));
                recurrentParentPayment.setPaymentId(rs.getString(PAYMENT_DATA.PAYMENT_RECURRENT_PAYER_PARENT_PAYMENT_ID.getName()));
                recurrentPayer.setRecurrentParent(recurrentParentPayment);

                return Payer.recurrent(recurrentPayer);
            default:
                throw new NotFoundException(String.format("Payment type '%s' not found", paymentPayerType));
        }
    }

    static void buildStatPaymentFlow(ResultSet rs, StatPayment statPayment, PaymentFlow paymentFlow) throws SQLException {
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
    }

    static InvoicePaymentStatus buildInvoicePaymentStatus(ResultSet rs, String eventCreatedAtString, com.rbkmoney.magista.domain.enums.InvoicePaymentStatus invoicePaymentStatus) throws SQLException {
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
                        TypeUtil.toEnumField(rs.getString(PAYMENT_DATA.PAYMENT_OPERATION_FAILURE_CLASS.getName()), FailureClass.class),
                        rs.getString(PAYMENT_DATA.PAYMENT_EXTERNAL_FAILURE.getName()),
                        rs.getString(PAYMENT_DATA.PAYMENT_EXTERNAL_FAILURE_REASON.getName())
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
        return paymentStatus;
    }

    static PayoutType toPayoutType(ResultSet rs) throws SQLException {
        com.rbkmoney.magista.domain.enums.PayoutType payoutType = TypeUtil.toEnumField(rs.getString(PAYOUT_DATA.PAYOUT_TYPE.getName()), com.rbkmoney.magista.domain.enums.PayoutType.class);
        switch (payoutType) {
            case bank_account:
                return PayoutType.bank_account(toPayoutAccount(rs));
            case wallet:
                return PayoutType.wallet(new Wallet(rs.getString(PAYOUT_DATA.PAYOUT_WALLET_ID.getName())));
            default:
                throw new NotFoundException(String.format("Payout type '%s' not found", payoutType));
        }
    }

    static PayoutAccount toPayoutAccount(ResultSet rs) throws SQLException {
        PayoutAccountType payoutAccountType = TypeUtil.toEnumField(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_TYPE.getName()), PayoutAccountType.class);
        switch (payoutAccountType) {
            case RUSSIAN_PAYOUT_ACCOUNT:
                RussianBankAccount russianBankAccount = new RussianBankAccount();
                russianBankAccount.setAccount(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_BANK_ID.getName()));
                russianBankAccount.setBankBik(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_BANK_LOCAL_CODE.getName()));
                russianBankAccount.setBankPostAccount(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_BANK_CORR_ID.getName()));
                russianBankAccount.setBankName(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_BANK_NAME.getName()));

                RussianPayoutAccount russianPayoutAccount = new RussianPayoutAccount();
                russianPayoutAccount.setBankAccount(russianBankAccount);
                russianPayoutAccount.setInn(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_INN.getName()));
                russianPayoutAccount.setPurpose(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_PURPOSE.getName()));
                return PayoutAccount.russian_payout_account(russianPayoutAccount);
            case INTERNATIONAL_PAYOUT_ACCOUNT:
                InternationalBankAccount internationalBankAccount = new InternationalBankAccount();
                internationalBankAccount.setAccountHolder(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_BANK_ID.getName()));
                internationalBankAccount.setIban(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_BANK_IBAN.getName()));

                InternationalBankDetails bankDetails = new InternationalBankDetails();
                bankDetails.setName(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_BANK_NAME.getName()));
                bankDetails.setBic(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_BANK_BIC.getName()));
                bankDetails.setAbaRtn(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_BANK_ABA_RTN.getName()));
                bankDetails.setAddress(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_BANK_ADDRESS.getName()));
                bankDetails.setCountry(TypeUtil.toEnumField(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_BANK_COUNTRY_CODE.getName()), Residence.class));
                internationalBankAccount.setBank(bankDetails);

                InternationalBankAccount correspondentBankAccount = new InternationalBankAccount();
                correspondentBankAccount.setAccountHolder(rs.getString(PAYOUT_DATA.PAYOUT_INTERNATIONAL_CORRESPONDENT_ACCOUNT_BANK_ACCOUNT.getName()));
                correspondentBankAccount.setNumber(rs.getString(PAYOUT_DATA.PAYOUT_INTERNATIONAL_CORRESPONDENT_ACCOUNT_BANK_NUMBER.getName()));
                correspondentBankAccount.setIban(rs.getString(PAYOUT_DATA.PAYOUT_INTERNATIONAL_CORRESPONDENT_ACCOUNT_BANK_IBAN.getName()));
                InternationalBankDetails correspondentBankDetails = new InternationalBankDetails();
                correspondentBankDetails.setName(rs.getString(PAYOUT_DATA.PAYOUT_INTERNATIONAL_CORRESPONDENT_ACCOUNT_BANK_NAME.getName()));
                correspondentBankDetails.setBic(rs.getString(PAYOUT_DATA.PAYOUT_INTERNATIONAL_CORRESPONDENT_ACCOUNT_BANK_BIC.getName()));
                correspondentBankDetails.setAddress(rs.getString(PAYOUT_DATA.PAYOUT_INTERNATIONAL_CORRESPONDENT_ACCOUNT_BANK_ADDRESS.getName()));
                correspondentBankDetails.setAbaRtn(rs.getString(PAYOUT_DATA.PAYOUT_INTERNATIONAL_CORRESPONDENT_ACCOUNT_BANK_ABA_RTN.getName()));
                correspondentBankDetails.setCountry(TypeUtil.toEnumField(rs.getString(PAYOUT_DATA.PAYOUT_INTERNATIONAL_CORRESPONDENT_ACCOUNT_BANK_COUNTRY_CODE.getName()), Residence.class));
                correspondentBankAccount.setBank(correspondentBankDetails);
                internationalBankAccount.setCorrespondentAccount(correspondentBankAccount);

                InternationalPayoutAccount internationalPayoutAccount = new InternationalPayoutAccount();
                internationalPayoutAccount.setBankAccount(internationalBankAccount);
                internationalPayoutAccount.setPurpose(rs.getString(PAYOUT_DATA.PAYOUT_ACCOUNT_PURPOSE.getName()));
                return PayoutAccount.international_payout_account(internationalPayoutAccount);
            default:
                throw new NotFoundException(String.format("Payout account type '%s' not found", payoutAccountType));
        }
    }

    static List<PayoutSummaryItem> toPayoutSummary(ResultSet rs, ObjectMapper objectMapper) throws SQLException {
        String payoutSummaryString = rs.getString(PAYOUT_DATA.PAYOUT_SUMMARY.getName());
        if (payoutSummaryString == null) {
            return null;
        }

        List<PayoutSummaryItem> payoutSummaryItems = new ArrayList<>();
        try {
            for (JsonNode jsonNode : objectMapper.readTree(payoutSummaryString)) {
                PayoutSummaryItem payoutSummaryItem = jsonToTBase(jsonNode, PayoutSummaryItem.class);
                payoutSummaryItems.add(payoutSummaryItem);
            }
        } catch (IOException ex) {
            throw new RuntimeJsonMappingException(ex.getMessage());
        }
        return payoutSummaryItems;
    }

    static PayoutStatus toPayoutStatus(ResultSet rs) throws SQLException {
        com.rbkmoney.magista.domain.enums.PayoutStatus payoutStatus = TypeUtil.toEnumField(rs.getString(PAYOUT_DATA.PAYOUT_STATUS.getName()), com.rbkmoney.magista.domain.enums.PayoutStatus.class);
        switch (payoutStatus) {
            case unpaid:
                return PayoutStatus.unpaid(new PayoutUnpaid());
            case paid:
                return PayoutStatus.paid(new PayoutPaid());
            case cancelled:
                return PayoutStatus.cancelled(new PayoutCancelled(rs.getString(PAYOUT_DATA.PAYOUT_CANCEL_DETAILS.getName())));
            case confirmed:
                return PayoutStatus.confirmed(new PayoutConfirmed());
            default:
                throw new NotFoundException(String.format("Payout status '%s' not found", payoutStatus));
        }
    }

    static InvoicePaymentRefundStatus toRefundStatus(ResultSet rs) throws SQLException {
        RefundStatus refundStatus = TypeUtil.toEnumField(rs.getString(REFUND_DATA.REFUND_STATUS.getName()), RefundStatus.class);
        switch (refundStatus) {
            case pending:
                return InvoicePaymentRefundStatus.pending(new InvoicePaymentRefundPending());
            case succeeded:
                return InvoicePaymentRefundStatus.succeeded(new InvoicePaymentRefundSucceeded(
                        TypeUtil.temporalToString(rs.getObject(REFUND_DATA.EVENT_CREATED_AT.getName(), LocalDateTime.class))
                ));
            case failed:
                return InvoicePaymentRefundStatus.failed(new InvoicePaymentRefundFailed(
                        DamselUtil.toOperationFailure(
                                TypeUtil.toEnumField(rs.getString(REFUND_DATA.REFUND_OPERATION_FAILURE_CLASS.getName()), FailureClass.class),
                                rs.getString(REFUND_DATA.REFUND_EXTERNAL_FAILURE.getName()),
                                rs.getString(REFUND_DATA.REFUND_EXTERNAL_FAILURE_REASON.getName())
                        ),
                        TypeUtil.temporalToString(rs.getObject(REFUND_DATA.EVENT_CREATED_AT.getName(), LocalDateTime.class))
                ));
            default:
                throw new NotFoundException(String.format("Refund status '%s' not found", refundStatus));
        }
    }
}
