package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.domain.BankAccount;
import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.LegalAgreement;
import com.rbkmoney.damsel.payout_processing.*;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.PayoutEventCategory;
import com.rbkmoney.magista.domain.enums.PayoutEventType;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.PayoutType;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.PayoutEventContext;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.FeeType;

import java.util.Map;

public class PayoutMapper implements Mapper<PayoutEventContext> {
    @Override
    public PayoutEventContext fill(PayoutEventContext context) {
        Event event = context.getSource().getSourceEvent().getPayoutEvent();
        PayoutEventStat payoutEventStat = context.getPayoutEventStat();

        payoutEventStat.setEventId(event.getId());
        payoutEventStat.setEventCreatedAt(
                TypeUtil.stringToLocalDateTime(event.getCreatedAt())
        );
        payoutEventStat.setPayoutId(event.getSource().getPayoutId());

        PayoutChange change = context.getPayoutChange();
        if (change.isSetPayoutCreated()) {
            payoutEventStat.setEventCategory(PayoutEventCategory.PAYOUT);
            payoutEventStat.setEventType(PayoutEventType.PAYOUT_CREATED);

            Payout payout = change.getPayoutCreated().getPayout();
            payoutEventStat.setPayoutId(payout.getId());
            payoutEventStat.setPayoutType(
                    TBaseUtil.unionFieldToEnum(payout.getType(), PayoutType.class)
            );

            if (payout.getType().isSetBankAccount()) {
                PayoutAccount payoutAccount = payout.getType().getBankAccount();
                BankAccount bankAccount = payoutAccount.getAccount();

                payoutEventStat.setPayoutAccountBankId(bankAccount.getAccount());
                payoutEventStat.setPayoutAccountBankCorrId(bankAccount.getBankPostAccount());
                payoutEventStat.setPayoutAccountBankBik(bankAccount.getBankBik());
                payoutEventStat.setPayoutAccountBankName(bankAccount.getBankName());
                payoutEventStat.setPayoutAccountPurpose(payoutAccount.getPurpose());
                payoutEventStat.setPayoutAccountInn(payoutAccount.getInn());

                LegalAgreement legalAgreement = payoutAccount.getLegalAgreement();
                payoutEventStat.setPayoutAccountLegalAgreementId(legalAgreement.getLegalAgreementId());
                payoutEventStat.setPayoutAccountLegalAgreementSignedAt(TypeUtil.stringToLocalDateTime(legalAgreement.getSignedAt()));
            }

            if (payout.getType().isSetBankCard()) {
                PayoutCard payoutCard = payout.getType().getBankCard();
                BankCard bankCard = payoutCard.getCard();
                payoutEventStat.setPayoutCardToken(bankCard.getToken());
                payoutEventStat.setPayoutCardMaskedPan(bankCard.getMaskedPan());
                payoutEventStat.setPayoutCardBin(bankCard.getBin());
                payoutEventStat.setPayoutCardPaymentSystem(bankCard.getPaymentSystem().name());
            }

            payoutEventStat.setPayoutStatus(
                    TBaseUtil.unionFieldToEnum(payout.getStatus(), PayoutStatus.class)
            );
            payoutEventStat.setPayoutCreatedAt(
                    TypeUtil.stringToLocalDateTime(payout.getCreatedAt())
            );

            payoutEventStat.setPayoutAmount(DamselUtil.getAmount(payout.getPayoutFlow(),
                    posting -> posting.getSource().getAccountType().isSetMerchant()
                            && posting.getDestination().getAccountType().isSetMerchant()));

            //TODO merchant -> provider
            Map<FeeType, Long> commissions = DamselUtil.getFees(payout.getPayoutFlow());
            payoutEventStat.setPayoutFee(commissions.get(FeeType.FEE));

            //TODO Shit
            payoutEventStat.setPayoutCurrencyCode(payout.getPayoutFlow().get(0).getVolume().getCurrency().getSymbolicCode());
            payoutEventStat.setPartyId(payout.getPartyId());
            payoutEventStat.setPartyShopId(payout.getShopId());
        }

        return context.setPayoutEventStat(payoutEventStat);
    }
}
