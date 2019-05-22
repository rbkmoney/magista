package com.rbkmoney.magista.dao.impl.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.damsel.merch_stat.StatPayout;
import com.rbkmoney.geck.common.util.TypeUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Map;

import static com.rbkmoney.magista.domain.tables.PayoutData.PAYOUT_DATA;

public class StatPayoutMapper implements RowMapper<Map.Entry<Long, StatPayout>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map.Entry<Long, StatPayout> mapRow(ResultSet rs, int i) throws SQLException {
        StatPayout statPayout = new StatPayout();
        statPayout.setId(rs.getString(PAYOUT_DATA.PAYOUT_ID.getName()));
        statPayout.setPartyId(rs.getString(PAYOUT_DATA.PARTY_ID.getName()));
        statPayout.setShopId(rs.getString(PAYOUT_DATA.PARTY_SHOP_ID.getName()));
        statPayout.setAmount(rs.getLong(PAYOUT_DATA.PAYOUT_AMOUNT.getName()));
        statPayout.setStatus(MapperHelper.toPayoutStatus(rs));
        statPayout.setFee(rs.getLong(PAYOUT_DATA.PAYOUT_FEE.getName()));
        statPayout.setCurrencySymbolicCode(rs.getString(PAYOUT_DATA.PAYOUT_CURRENCY_CODE.getName()));
        statPayout.setCreatedAt(
                TypeUtil.temporalToString(rs.getObject(PAYOUT_DATA.PAYOUT_CREATED_AT.getName(), LocalDateTime.class))
        );
        statPayout.setType(MapperHelper.toPayoutType(rs));
        statPayout.setSummary(MapperHelper.toPayoutSummary(rs, objectMapper));

        return new AbstractMap.SimpleEntry<>(0L, statPayout);
    }

}
