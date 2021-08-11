package com.rbkmoney.magista.dao.impl.mapper;

import com.rbkmoney.damsel.base.Rational;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.util.DamselUtil;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;

import static com.rbkmoney.magista.domain.Tables.ALLOCATION_TRANSACTION_DATA;

public class AllocationRowMapper implements RowMapper<Map.Entry<String, AllocationTransaction>> {

    @Override
    public Map.Entry<String, AllocationTransaction> mapRow(ResultSet rs, int i) throws SQLException {
        AllocationTransaction allocationTransaction = new AllocationTransaction();
        allocationTransaction.setId(rs.getString(ALLOCATION_TRANSACTION_DATA.ALLOCATION_ID.getName()));
        AllocationTransactionTarget allocationTransactionTarget = buildAllocationTransactionTarget(rs);
        allocationTransaction.setTarget(allocationTransactionTarget);
        allocationTransaction.setAmount(
                new Cash(
                        rs.getLong(ALLOCATION_TRANSACTION_DATA.AMOUNT.getName()),
                        new CurrencyRef(rs.getString(ALLOCATION_TRANSACTION_DATA.CURRENCY.getName()))
                )
        );
        AllocationTransactionBodyTotal allocationTransactionBody = buildAllocationTransactionTotal(rs);
        allocationTransaction.setBody(allocationTransactionBody);
        AllocationTransactionFeeShare allocationTransactionFee = buildAllocationTransactionFeeShare(rs);
        allocationTransactionBody.setFee(allocationTransactionFee);

        String invoiceCartJson = rs.getString(ALLOCATION_TRANSACTION_DATA.INVOICE_CART_JSON.getName());
        if (invoiceCartJson != null) {
            allocationTransaction.setDetails(
                    new AllocationTransactionDetails().setCart(DamselUtil.fromJson(invoiceCartJson, InvoiceCart.class))
            );
        }

        return new AbstractMap.SimpleEntry<>(
                rs.getString(ALLOCATION_TRANSACTION_DATA.INVOICE_ID.getName()),
                allocationTransaction);
    }

    private AllocationTransactionTarget buildAllocationTransactionTarget(ResultSet rs) throws SQLException {
        AllocationTransactionTarget allocationTransactionTarget = new AllocationTransactionTarget();
        allocationTransactionTarget.setShop(
                new AllocationTransactionTargetShop(
                        rs.getString(ALLOCATION_TRANSACTION_DATA.TARGET_OWNER_ID.getName()),
                        rs.getString(ALLOCATION_TRANSACTION_DATA.TARGET_SHOP_ID.getName()))
        );

        return allocationTransactionTarget;
    }

    private AllocationTransactionBodyTotal buildAllocationTransactionTotal(ResultSet rs) throws SQLException {
        AllocationTransactionBodyTotal allocationTransactionBody = new AllocationTransactionBodyTotal();
        allocationTransactionBody.setFeeAmount(
                new Cash(
                        rs.getLong(ALLOCATION_TRANSACTION_DATA.FEE_AMOUNT.getName()),
                        new CurrencyRef(rs.getString(ALLOCATION_TRANSACTION_DATA.FEE_CURRENCY.getName()))
                )
        );
        allocationTransactionBody.setTotal(
                new Cash(
                        rs.getLong(ALLOCATION_TRANSACTION_DATA.TOTAL_AMOUNT.getName()),
                        new CurrencyRef(rs.getString(ALLOCATION_TRANSACTION_DATA.TOTAL_CURRENCY.getName()))
                )
        );
        AllocationTransactionTarget allocationBodyTransactionTarget = new AllocationTransactionTarget();
        allocationBodyTransactionTarget.setShop(
                new AllocationTransactionTargetShop(
                        rs.getString(ALLOCATION_TRANSACTION_DATA.FEE_TARGET_OWNER_ID.getName()),
                        rs.getString(ALLOCATION_TRANSACTION_DATA.FEE_TARGET_SHOP_ID.getName())
                )
        );
        allocationTransactionBody.setFeeTarget(allocationBodyTransactionTarget);

        return allocationTransactionBody;
    }

    private AllocationTransactionFeeShare buildAllocationTransactionFeeShare(ResultSet rs) throws SQLException {
        AllocationTransactionFeeShare allocationTransactionFee = new AllocationTransactionFeeShare();
        allocationTransactionFee.setParts(
                new Rational(
                        rs.getLong(ALLOCATION_TRANSACTION_DATA.FEE_RATIONAL_P.getName()),
                        rs.getLong(ALLOCATION_TRANSACTION_DATA.FEE_RATIONAL_Q.getName())
                )
        );
        String feeRoundingMethod = rs.getString(ALLOCATION_TRANSACTION_DATA.FEE_ROUNDING_METHOD.getName());
        if (StringUtils.hasLength(feeRoundingMethod)) {
            allocationTransactionFee.setRoundingMethod(TypeUtil.toEnumField(feeRoundingMethod, RoundingMethod.class));
        }

        return allocationTransactionFee;
    }

}
