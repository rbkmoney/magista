package com.rbkmoney.magista.dao.impl.mapper;

import com.rbkmoney.magista.dark.messiah.EnrichedStatInvoice;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.ObjectUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.rbkmoney.magista.domain.tables.RefundData.REFUND_DATA;

/**
 * merchant OKKO-specific, in general shouldn't be touched.
 *
 * @author n.pospolita
 */
public class EnrichedStatInvoiceMapper implements RowMapper<EnrichedStatInvoice> {

    private final StatInvoiceMapper statInvoiceMapper;
    private final StatPaymentMapper statPaymentMapper;
    private final StatRefundMapper statRefundMapper;

    public EnrichedStatInvoiceMapper() {
        statInvoiceMapper = new StatInvoiceMapper();
        statPaymentMapper = new StatPaymentMapper();
        statRefundMapper = new StatRefundMapper();
    }

    @Override
    public EnrichedStatInvoice mapRow(ResultSet resultSet, int i) throws SQLException {
        return new EnrichedStatInvoice(
                statInvoiceMapper.mapRow(resultSet, i),
                List.of(statPaymentMapper.mapRow(resultSet, i)),
                ObjectUtils.isEmpty(resultSet.getString(REFUND_DATA.REFUND_ID.getName()))
                        ? List.of() : List.of(statRefundMapper.mapRow(resultSet, i))
        );
    }
}
