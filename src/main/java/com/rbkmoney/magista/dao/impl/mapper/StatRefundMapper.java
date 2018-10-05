package com.rbkmoney.magista.dao.impl.mapper;

import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.domain.enums.RefundStatus;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.util.DamselUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Map;

import static com.rbkmoney.magista.domain.tables.Refund.REFUND;

public class StatRefundMapper implements RowMapper<Map.Entry<Long, StatRefund>> {

    @Override
    public Map.Entry<Long, StatRefund> mapRow(ResultSet rs, int i) throws SQLException {
        StatRefund statRefund = new StatRefund();
        statRefund.setId(rs.getString(REFUND.REFUND_ID.getName()));
        statRefund.setInvoiceId(rs.getString(REFUND.INVOICE_ID.getName()));
        statRefund.setPaymentId(rs.getString(REFUND.PAYMENT_ID.getName()));
        statRefund.setOwnerId(rs.getString(REFUND.PARTY_ID.getName()));
        statRefund.setShopId(rs.getString(REFUND.PARTY_SHOP_ID.getName()));
        statRefund.setCurrencySymbolicCode(rs.getString(REFUND.REFUND_CURRENCY_CODE.getName()));
        statRefund.setStatus(toRefundStatus(rs));
        statRefund.setAmount(rs.getLong(REFUND.REFUND_AMOUNT.getName()));
        statRefund.setFee(rs.getLong(REFUND.REFUND_FEE.getName()));
        statRefund.setReason(rs.getString(REFUND.REFUND_REASON.getName()));
        statRefund.setCreatedAt(TypeUtil.temporalToString(rs.getObject(REFUND.REFUND_CREATED_AT.getName(), LocalDateTime.class)));
        return new AbstractMap.SimpleEntry<>(0L, statRefund);
    }

    private InvoicePaymentRefundStatus toRefundStatus(ResultSet rs) throws SQLException {
        RefundStatus refundStatus = TypeUtil.toEnumField(rs.getString(REFUND.REFUND_STATUS.getName()), RefundStatus.class);
        switch (refundStatus) {
            case pending:
                return InvoicePaymentRefundStatus.pending(new InvoicePaymentRefundPending());
            case succeeded:
                return InvoicePaymentRefundStatus.succeeded(new InvoicePaymentRefundSucceeded(
                        TypeUtil.temporalToString(rs.getObject(REFUND.EVENT_CREATED_AT.getName(), LocalDateTime.class))
                ));
            case failed:
                return InvoicePaymentRefundStatus.failed(new InvoicePaymentRefundFailed(
                        DamselUtil.toOperationFailure(
                                TypeUtil.toEnumField(rs.getString(REFUND.REFUND_OPERATION_FAILURE_CLASS.getName()), FailureClass.class),
                                rs.getString(REFUND.REFUND_EXTERNAL_FAILURE.getName()),
                                rs.getString(REFUND.REFUND_EXTERNAL_FAILURE_REASON.getName())
                        ),
                        TypeUtil.temporalToString(rs.getObject(REFUND.EVENT_CREATED_AT.getName(), LocalDateTime.class))
                ));
            default:
                throw new NotFoundException(String.format("Refund status '%s' not found", refundStatus));
        }
    }

}
