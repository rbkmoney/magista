package com.rbkmoney.magista.dao.impl.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.rbkmoney.damsel.domain.Residence;
import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.PayoutAccountType;
import com.rbkmoney.magista.exception.NotFoundException;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.rbkmoney.magista.domain.tables.PayoutData.PAYOUT_DATA;
import static com.rbkmoney.magista.util.DamselUtil.jsonToTBase;

public class StatPayoutMapper implements RowMapper<Map.Entry<Long, StatPayout>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map.Entry<Long, StatPayout> mapRow(ResultSet rs, int i) throws SQLException {
        StatPayout statPayout = new StatPayout();
        statPayout.setId(rs.getString(PAYOUT_DATA.PAYOUT_ID.getName()));
        statPayout.setPartyId(rs.getString(PAYOUT_DATA.PARTY_ID.getName()));
        statPayout.setShopId(rs.getString(PAYOUT_DATA.PARTY_SHOP_ID.getName()));
        statPayout.setAmount(rs.getLong(PAYOUT_DATA.PAYOUT_AMOUNT.getName()));
        statPayout.setStatus(toPayoutStatus(rs));
        statPayout.setFee(rs.getLong(PAYOUT_DATA.PAYOUT_FEE.getName()));
        statPayout.setCurrencySymbolicCode(rs.getString(PAYOUT_DATA.PAYOUT_CURRENCY_CODE.getName()));
        statPayout.setCreatedAt(
                TypeUtil.temporalToString(rs.getObject(PAYOUT_DATA.PAYOUT_CREATED_AT.getName(), LocalDateTime.class))
        );
        statPayout.setType(toPayoutType(rs));
        statPayout.setSummary(toPayoutSummary(rs));

        return new AbstractMap.SimpleEntry(0L, statPayout);
    }

    private PayoutType toPayoutType(ResultSet rs) throws SQLException {
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

    private PayoutAccount toPayoutAccount(ResultSet rs) throws SQLException {
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

    private List<PayoutSummaryItem> toPayoutSummary(ResultSet rs) throws SQLException {
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

    private PayoutStatus toPayoutStatus(ResultSet rs) throws SQLException {
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

}
