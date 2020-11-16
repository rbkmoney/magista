package com.rbkmoney.magista.dao.impl.mapper;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.merch_stat.StatChargeback;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Map;

import static com.rbkmoney.magista.domain.Tables.CHARGEBACK_DATA;

public class StatChargebackMapper implements RowMapper<Map.Entry<Long, StatChargeback>> {

    @Override
    public Map.Entry<Long, StatChargeback> mapRow(ResultSet rs, int i) throws SQLException {
        StatChargeback chargeback = new StatChargeback()
                .setInvoiceId(rs.getString(CHARGEBACK_DATA.INVOICE_ID.getName()))
                .setPaymentId(rs.getString(CHARGEBACK_DATA.PAYMENT_ID.getName()))
                .setChargebackId(rs.getString(CHARGEBACK_DATA.CHARGEBACK_ID.getName()))
                .setExternalId(rs.getString(CHARGEBACK_DATA.EXTERNAL_ID.getName()))
                .setPartyId(rs.getString(CHARGEBACK_DATA.PARTY_ID.getName()))
                .setShopId(rs.getString(CHARGEBACK_DATA.PARTY_SHOP_ID.getName()))
                .setChargebackStatus(MapperHelper.toInvoicePaymentChargebackStatus(rs))
                .setCreatedAt(TypeUtil.temporalToString(
                        rs.getObject(CHARGEBACK_DATA.CHARGEBACK_CREATED_AT.getName(), LocalDateTime.class))
                )
                .setChargebackReason(MapperHelper.toInvoicePaymentChargebackReason(rs))
                .setLevyAmount(rs.getLong(CHARGEBACK_DATA.CHARGEBACK_LEVY_AMOUNT.getName()))
                .setLevyCurrencyCode(new com.rbkmoney.damsel.domain.Currency()
                        .setName(rs.getString(CHARGEBACK_DATA.CHARGEBACK_LEVY_CURRENCY_CODE.getName()))
                        .setNumericCode((short) 0)
                        .setExponent((short) 0)
                        .setSymbolicCode(rs.getString(CHARGEBACK_DATA.CHARGEBACK_LEVY_CURRENCY_CODE.getName())))
                .setAmount(rs.getLong(CHARGEBACK_DATA.CHARGEBACK_AMOUNT.getName()))
                .setCurrencyCode(new com.rbkmoney.damsel.domain.Currency()
                        .setName(rs.getString(CHARGEBACK_DATA.CHARGEBACK_CURRENCY_CODE.getName()))
                        .setNumericCode((short) 0)
                        .setExponent((short) 0)
                        .setSymbolicCode(rs.getString(CHARGEBACK_DATA.CHARGEBACK_CURRENCY_CODE.getName())))
                .setFee(rs.getLong(CHARGEBACK_DATA.CHARGEBACK_FEE.getName()))
                .setProviderFee(rs.getLong(CHARGEBACK_DATA.CHARGEBACK_PROVIDER_FEE.getName()))
                .setExternalFee(rs.getLong(CHARGEBACK_DATA.CHARGEBACK_EXTERNAL_FEE.getName()))
                .setStage(MapperHelper.toInvoicePaymentChargebackStage(rs));

        byte[] content = rs.getBytes(CHARGEBACK_DATA.CHARGEBACK_CONTEXT.getName());
        if (content != null) {
            chargeback.setContent(new Content().setData(content).setType(""));
        }
        return new AbstractMap.SimpleEntry<>(rs.getLong(CHARGEBACK_DATA.ID.getName()), chargeback);
    }

}
