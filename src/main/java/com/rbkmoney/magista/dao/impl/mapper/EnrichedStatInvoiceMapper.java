package com.rbkmoney.magista.dao.impl.mapper;

import com.rbkmoney.damsel.merch_stat.EnrichedStatInvoice;
import com.sun.tools.javac.util.List;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class EnrichedStatInvoiceMapper implements RowMapper<Map.Entry<Long, EnrichedStatInvoice>> {

    private final StatInvoiceMapper statInvoiceMapper;
    private final StatPaymentMapper statPaymentMapper;
    private final StatRefundMapper statRefundMapper;

    public EnrichedStatInvoiceMapper() {
        statInvoiceMapper = new StatInvoiceMapper();
        statPaymentMapper = new StatPaymentMapper();
        statRefundMapper = new StatRefundMapper();
    }

    @Override
    public Map.Entry<Long, EnrichedStatInvoice> mapRow(ResultSet resultSet, int i) throws SQLException {
        return new AbstractMap.SimpleEntry<>(0L,
                new EnrichedStatInvoice(
                        statInvoiceMapper.mapRow(resultSet, i).getValue(),
                        new ArrayList<>(List.of(statPaymentMapper.mapRow(resultSet, i).getValue())),
                        new ArrayList<>(List.of(statRefundMapper.mapRow(resultSet, i).getValue()))
                ));
    }


}
