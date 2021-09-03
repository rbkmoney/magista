package com.rbkmoney.magista.event.mapper.impl;

import com.rbkmoney.damsel.domain.InternationalBankAccount;
import com.rbkmoney.damsel.domain.InternationalBankDetails;
import com.rbkmoney.damsel.domain.PayoutToolInfo;
import com.rbkmoney.damsel.domain.RussianBankAccount;
import com.rbkmoney.geck.common.util.TBaseUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.PayoutToolType;
import com.rbkmoney.magista.event.handler.PayoutHandler;
import com.rbkmoney.magista.service.PartyManagementService;
import com.rbkmoney.magista.service.PayoutService;
import com.rbkmoney.payout.manager.Event;
import com.rbkmoney.payout.manager.Payout;
import com.rbkmoney.payout.manager.PayoutChange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayoutCreatedHandler implements PayoutHandler {

    private final PayoutService payoutEventService;
    private final PartyManagementService partyManagementService;

    @Override
    public void handle(PayoutChange change, Event event) {
        var payout = new com.rbkmoney.magista.domain.tables.pojos.Payout();
        payout.setEventCreatedAt(TypeUtil.stringToLocalDateTime(event.getCreatedAt()));
        payout.setPayoutId(event.getPayoutId());
        payout.setSequenceId(event.getSequenceId());

        Payout payoutSource = change.getCreated().getPayout();
        payout.setStatus(TBaseUtil.unionFieldToEnum(payoutSource.getStatus(), PayoutStatus.class));
        payout.setCreatedAt(TypeUtil.stringToLocalDateTime(payoutSource.getCreatedAt()));

        payout.setPayoutToolId(payoutSource.getPayoutToolId());
        payout.setAmount(payoutSource.getAmount());
        payout.setFee(payoutSource.getFee());
        payout.setCurrencyCode(payoutSource.getCurrency().getSymbolicCode());

        payout.setPartyId(payoutSource.getPartyId());
        payout.setShopId(payoutSource.getShopId());
        PayoutToolInfo payoutToolInfo = partyManagementService.getPayoutToolInfo(
                payoutSource.getPartyId(), payoutSource.getShopId(), payoutSource.getPayoutToolId());
        PayoutToolType payoutToolType = TBaseUtil.unionFieldToEnum(payoutToolInfo, PayoutToolType.class);
        payout.setPayoutToolType(payoutToolType);
        if (payoutToolInfo.isSetRussianBankAccount()) {
            RussianBankAccount russianBankAccount = payoutToolInfo.getRussianBankAccount();
            payout.setPayoutToolRussianBankAccountAccount(russianBankAccount.getAccount());
            payout.setPayoutToolRussianBankAccountBankBik(russianBankAccount.getBankBik());
            payout.setPayoutToolRussianBankAccountBankName(russianBankAccount.getBankName());
            payout.setPayoutToolRussianBankAccountBankPostAccount(russianBankAccount.getBankPostAccount());
        } else if (payoutToolInfo.isSetInternationalBankAccount()) {
            InternationalBankAccount internationalBankAccount = payoutToolInfo.getInternationalBankAccount();
            payout.setPayoutToolInternationalBankAccountNumber(internationalBankAccount.getNumber());
            if (internationalBankAccount.isSetBank()) {
                InternationalBankDetails bank = internationalBankAccount.getBank();
                payout.setPayoutToolInternationalBankAccountBankBic(bank.getBic());
                if (bank.getCountry() != null) {
                    payout.setPayoutToolInternationalBankAccountBankCountryCode(bank.getCountry().name());
                }
                payout.setPayoutToolInternationalBankAccountBankName(bank.getName());
                payout.setPayoutToolInternationalBankAccountBankAddress(bank.getAddress());
                payout.setPayoutToolInternationalBankAccountBankAbaRtn(bank.getAbaRtn());
            }
            if (internationalBankAccount.isSetCorrespondentAccount()) {
                payout.setPayoutToolInternationalBankAccountCorrAccount(
                        internationalBankAccount.getCorrespondentAccount().getNumber());
            }
            payout.setPayoutToolInternationalBankAccountIban(internationalBankAccount.getIban());
        } else if (payoutToolInfo.isSetWalletInfo()) {
            payout.setPayoutToolWalletId(payoutToolInfo.getWalletInfo().getWalletId());
        }

        payoutEventService.savePayout(payout);
    }

    @Override
    public boolean accept(PayoutChange change) {
        return change.isSetCreated();
    }
}
