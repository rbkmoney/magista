package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.damsel.payout_processing.*;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.PayoutAccountType;
import com.rbkmoney.magista.domain.enums.PayoutEventType;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.PayoutType;
import com.rbkmoney.magista.domain.tables.pojos.PayoutData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.PayoutService;
import com.rbkmoney.magista.util.DamselUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PayoutCreatedHandler implements Handler<PayoutChange, StockEvent> {

    private final PayoutService payoutEventService;

    @Autowired
    public PayoutCreatedHandler(PayoutService payoutEventService) {
        this.payoutEventService = payoutEventService;
    }

    @Override
    public Processor handle(PayoutChange change, StockEvent parent) {
        Event event = parent.getSourceEvent().getPayoutEvent();
        PayoutData payoutData = new PayoutData();

        payoutData.setEventId(event.getId());
        payoutData.setEventCreatedAt(
                TypeUtil.stringToLocalDateTime(event.getCreatedAt())
        );
        payoutData.setPayoutId(event.getSource().getPayoutId());

        if (change.isSetPayoutCreated()) {
            payoutData.setEventType(PayoutEventType.PAYOUT_CREATED);

            Payout payout = change.getPayoutCreated().getPayout();
            payoutData.setPayoutId(payout.getId());
            payoutData.setPayoutType(
                    TBaseUtil.unionFieldToEnum(payout.getType(), PayoutType.class)
            );

            if (payout.getType().isSetWallet()) {
                payoutData.setPayoutWalletId(payout.getType().getWallet().getWalletId());
            }

            if (payout.getType().isSetBankAccount()) {
                PayoutAccount payoutAccount = payout.getType().getBankAccount();

                if (payoutAccount.isSetRussianPayoutAccount()) {
                    payoutData.setPayoutAccountType(PayoutAccountType.RUSSIAN_PAYOUT_ACCOUNT);
                    RussianPayoutAccount account = payoutAccount.getRussianPayoutAccount();
                    RussianBankAccount bankAccount = account.getBankAccount();
                    payoutData.setPayoutAccountBankId(bankAccount.getAccount());
                    payoutData.setPayoutAccountBankCorrId(bankAccount.getBankPostAccount());
                    payoutData.setPayoutAccountBankLocalCode(bankAccount.getBankBik());
                    payoutData.setPayoutAccountBankName(bankAccount.getBankName());
                    payoutData.setPayoutAccountPurpose(account.getPurpose());
                    payoutData.setPayoutAccountInn(account.getInn());
                    LegalAgreement legalAgreement = account.getLegalAgreement();
                    payoutData.setPayoutAccountLegalAgreementId(legalAgreement.getLegalAgreementId());
                    payoutData.setPayoutAccountLegalAgreementSignedAt(TypeUtil.stringToLocalDateTime(legalAgreement.getSignedAt()));
                } else if (payoutAccount.isSetInternationalPayoutAccount()) {
                    payoutData.setPayoutAccountType(PayoutAccountType.INTERNATIONAL_PAYOUT_ACCOUNT);
                    InternationalPayoutAccount account = payoutAccount.getInternationalPayoutAccount();
                    InternationalLegalEntity legalEntity = account.getLegalEntity();
                    payoutData.setPayoutAccountTradingName(legalEntity.getTradingName());
                    payoutData.setPayoutAccountLegalName(legalEntity.getLegalName());
                    payoutData.setPayoutAccountActualAddress(legalEntity.getActualAddress());
                    payoutData.setPayoutAccountRegisteredAddress(legalEntity.getRegisteredAddress());
                    payoutData.setPayoutAccountRegisteredNumber(legalEntity.getRegisteredNumber());
                    payoutData.setPayoutAccountPurpose(account.getPurpose());
                    InternationalBankAccount bankAccount = account.getBankAccount();
                    payoutData.setPayoutAccountBankId(bankAccount.getAccountHolder());
                    payoutData.setPayoutAccountBankIban(bankAccount.getIban());
                    if (bankAccount.isSetBank()) {
                        InternationalBankDetails bankDetails = bankAccount.getBank();
                        payoutData.setPayoutAccountBankName(bankDetails.getName());
                        payoutData.setPayoutAccountBankAddress(bankDetails.getAddress());
                        payoutData.setPayoutAccountBankBic(bankDetails.getBic());
                        payoutData.setPayoutAccountBankAbaRtn(bankDetails.getAbaRtn());
                        payoutData.setPayoutAccountBankCountryCode(
                                Optional.ofNullable(bankDetails.getCountry())
                                        .map(country -> country.toString())
                                        .orElse(null)
                        );
                    }

                    if (bankAccount.isSetCorrespondentAccount()) {
                        InternationalBankAccount correspondentAccount = bankAccount.getCorrespondentAccount();
                        payoutData.setPayoutInternationalCorrespondentAccountBankAccount(correspondentAccount.getAccountHolder());
                        payoutData.setPayoutInternationalCorrespondentAccountBankNumber(correspondentAccount.getNumber());
                        payoutData.setPayoutInternationalCorrespondentAccountBankIban(correspondentAccount.getIban());
                        if (correspondentAccount.isSetBank()) {
                            InternationalBankDetails corrBankDetails = correspondentAccount.getBank();
                            payoutData.setPayoutInternationalCorrespondentAccountBankName(corrBankDetails.getName());
                            payoutData.setPayoutInternationalCorrespondentAccountBankAddress(corrBankDetails.getAddress());
                            payoutData.setPayoutInternationalCorrespondentAccountBankBic(corrBankDetails.getBic());
                            payoutData.setPayoutInternationalCorrespondentAccountBankAbaRtn(corrBankDetails.getAbaRtn());
                            payoutData.setPayoutInternationalCorrespondentAccountBankCountryCode(
                                    Optional.ofNullable(corrBankDetails.getCountry())
                                            .map(country -> country.toString())
                                            .orElse(null)
                            );
                        }
                    }

                    LegalAgreement legalAgreement = account.getLegalAgreement();
                    payoutData.setPayoutAccountLegalAgreementId(legalAgreement.getLegalAgreementId());
                    payoutData.setPayoutAccountLegalAgreementSignedAt(TypeUtil.stringToLocalDateTime(legalAgreement.getSignedAt()));
                }
            }

            payoutData.setPayoutStatus(
                    TBaseUtil.unionFieldToEnum(payout.getStatus(), PayoutStatus.class)
            );
            payoutData.setPayoutCreatedAt(
                    TypeUtil.stringToLocalDateTime(payout.getCreatedAt())
            );

            payoutData.setPayoutAmount(payout.getAmount());
            payoutData.setPayoutFee(payout.getFee());
            payoutData.setPayoutCurrencyCode(payout.getCurrency().getSymbolicCode());

            payoutData.setPartyId(payout.getPartyId());
            payoutData.setPartyShopId(payout.getShopId());

            if (payout.isSetSummary()) {
                List<PayoutSummaryItem> payoutSummaryItems = payout.getSummary().stream()
                        .filter(payoutSummaryItem -> payoutSummaryItem.getOperationType() != OperationType.adjustment)
                        .collect(Collectors.toList());
                payoutData.setPayoutSummary(DamselUtil.toPayoutSummaryStatString(payoutSummaryItems));
            }
        }
        return () -> payoutEventService.savePayout(payoutData);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.PAYOUT_CREATED;
    }
}
