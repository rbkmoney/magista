package com.rbkmoney.magista.dao.impl.mapper;

import com.rbkmoney.damsel.merch_stat.EnrichedStatInvoice;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.ObjectUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import static com.rbkmoney.magista.domain.tables.PaymentData.PAYMENT_DATA;
import static com.rbkmoney.magista.domain.tables.RefundData.REFUND_DATA;

/**
 * merchant OKKO-specific, in general shouldn't be touched.
 *
 * @author n.pospolita
 */
@Deprecated
public class DeprecatedEnrichedStatInvoiceMapper implements RowMapper<Map.Entry<Long, EnrichedStatInvoice>> {

    private final DeprecatedStatInvoiceMapper statInvoiceMapper;
    private final DeprecatedStatPaymentMapper statPaymentMapper;
    private final DeprecatedStatRefundMapper statRefundMapper;

    public DeprecatedEnrichedStatInvoiceMapper() {
        statInvoiceMapper = new DeprecatedStatInvoiceMapper();
        statPaymentMapper = new DeprecatedStatPaymentMapper();
        statRefundMapper = new DeprecatedStatRefundMapper();
    }

    @Override
    public Map.Entry<Long, EnrichedStatInvoice> mapRow(ResultSet resultSet, int i) throws SQLException {
        return new AbstractMap.SimpleEntry<>(resultSet.getLong(PAYMENT_DATA.ID.getName()),
                new EnrichedStatInvoice(
                        statInvoiceMapper.mapRow(resultSet, i).getValue(),
                        List.of(statPaymentMapper.mapRow(resultSet, i).getValue()),
                        ObjectUtils.isEmpty(resultSet.getString(REFUND_DATA.REFUND_ID.getName()))
                                ? List.of() : List.of(statRefundMapper.mapRow(resultSet, i).getValue())
                ));
    }


}