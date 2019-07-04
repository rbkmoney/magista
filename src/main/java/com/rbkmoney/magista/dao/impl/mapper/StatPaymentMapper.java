package com.rbkmoney.magista.dao.impl.mapper;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.AdditionalTransactionInfo;
import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.domain.BankCardTokenProvider;
import com.rbkmoney.damsel.domain.TransactionInfo;
import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.enums.FailureClass;
import com.rbkmoney.magista.domain.enums.PaymentFlow;
import com.rbkmoney.magista.domain.enums.PaymentPayerType;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.util.DamselUtil;
import org.springframework.jdbc.core.RowMapper;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.rbkmoney.magista.domain.tables.PaymentData.PAYMENT_DATA;

public class StatPaymentMapper implements RowMapper<Map.Entry<Long, StatPayment>> {

    @Override
    public Map.Entry<Long, StatPayment> mapRow(ResultSet rs, int i) throws SQLException {
        StatPayment statPayment = new StatPayment();
        statPayment.setId(rs.getString(PAYMENT_DATA.PAYMENT_ID.getName()));
        statPayment.setInvoiceId(rs.getString(PAYMENT_DATA.INVOICE_ID.getName()));
        statPayment.setOwnerId(rs.getString(PAYMENT_DATA.PARTY_ID.getName()));
        statPayment.setShopId(rs.getString(PAYMENT_DATA.PARTY_SHOP_ID.getName()));
        statPayment.setAmount(rs.getLong(PAYMENT_DATA.PAYMENT_AMOUNT.getName()));
        statPayment.setFee(rs.getLong(PAYMENT_DATA.PAYMENT_FEE.getName()));
        statPayment.setCurrencySymbolicCode(rs.getString(PAYMENT_DATA.PAYMENT_CURRENCY_CODE.getName()));
        statPayment.setCreatedAt(
                TypeUtil.temporalToString(
                        rs.getObject(PAYMENT_DATA.PAYMENT_CREATED_AT.getName(), LocalDateTime.class)
                )
        );
        statPayment.setDomainRevision(rs.getLong(PAYMENT_DATA.PAYMENT_DOMAIN_REVISION.getName()));

        String eventCreatedAtString = TypeUtil.temporalToString(
                rs.getObject(PAYMENT_DATA.EVENT_CREATED_AT.getName(), LocalDateTime.class)
        );
        com.rbkmoney.magista.domain.enums.InvoicePaymentStatus invoicePaymentStatus = TypeUtil.toEnumField(
                rs.getString(PAYMENT_DATA.PAYMENT_STATUS.getName()),
                com.rbkmoney.magista.domain.enums.InvoicePaymentStatus.class
        );

        InvoicePaymentStatus paymentStatus;
        paymentStatus = MapperHelper.buildInvoicePaymentStatus(rs, eventCreatedAtString, invoicePaymentStatus);
        statPayment.setStatus(paymentStatus);
        statPayment.setPayer(MapperHelper.buildPayer(rs));

        PaymentFlow paymentFlow = TypeUtil.toEnumField(rs.getString(PAYMENT_DATA.PAYMENT_FLOW.getName()), PaymentFlow.class);

        MapperHelper.buildStatPaymentFlow(rs, statPayment, paymentFlow);

        statPayment.setMakeRecurrent(rs.getBoolean(PAYMENT_DATA.PAYMENT_MAKE_RECURRENT_FLAG.getName()));

        statPayment.setShortId(rs.getString(PAYMENT_DATA.PAYMENT_SHORT_ID.getName()));

        byte[] context = rs.getBytes(PAYMENT_DATA.PAYMENT_CONTEXT.getName());
        if (context != null) {
            statPayment.setContext(
                    new Content(
                            rs.getString(PAYMENT_DATA.PAYMENT_CONTEXT_TYPE.getName()),
                            ByteBuffer.wrap(context)
                    )
            );
        }

        AdditionalTransactionInfo additionalTransactionInfo = new AdditionalTransactionInfo();
        additionalTransactionInfo.setRrn(rs.getString(PAYMENT_DATA.PAYMENT_RRN.getName()));
        additionalTransactionInfo.setApprovalCode(rs.getString(PAYMENT_DATA.PAYMENT_APPROVAL_CODE.getName()));
        statPayment.setAdditionalTransactionInfo(additionalTransactionInfo);

        return new AbstractMap.SimpleEntry<>(rs.getLong(PAYMENT_DATA.ID.getName()), statPayment);
    }


}
