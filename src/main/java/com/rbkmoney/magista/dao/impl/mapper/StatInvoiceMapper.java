package com.rbkmoney.magista.dao.impl.mapper;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.InvoiceCart;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.InvoiceStatus;
import com.rbkmoney.magista.StatInvoice;
import com.rbkmoney.magista.util.DamselUtil;
import org.springframework.jdbc.core.RowMapper;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static com.rbkmoney.magista.domain.tables.InvoiceData.INVOICE_DATA;

public class StatInvoiceMapper implements RowMapper<StatInvoice> {

    @Override
    public StatInvoice mapRow(ResultSet rs, int i) throws SQLException {
        StatInvoice statInvoice = new StatInvoice();
        statInvoice.setId(rs.getString(INVOICE_DATA.INVOICE_ID.getName()));
        statInvoice.setOwnerId(rs.getString(INVOICE_DATA.PARTY_ID.getName()));
        statInvoice.setShopId(rs.getString(INVOICE_DATA.PARTY_SHOP_ID.getName()));
        statInvoice.setAmount(rs.getLong(INVOICE_DATA.INVOICE_AMOUNT.getName()));
        statInvoice.setCurrencySymbolicCode(rs.getString(INVOICE_DATA.INVOICE_CURRENCY_CODE.getName()));
        statInvoice.setProduct(rs.getString(INVOICE_DATA.INVOICE_PRODUCT.getName()));
        statInvoice.setDescription(rs.getString(INVOICE_DATA.INVOICE_DESCRIPTION.getName()));
        statInvoice.setExternalId(rs.getString(INVOICE_DATA.EXTERNAL_ID.getName()));
        statInvoice.setCreatedAt(
                TypeUtil.temporalToString(
                        rs.getObject(INVOICE_DATA.INVOICE_CREATED_AT.getName(), LocalDateTime.class)
                )
        );
        statInvoice.setDue(
                TypeUtil.temporalToString(
                        rs.getObject(INVOICE_DATA.INVOICE_DUE.getName(), LocalDateTime.class)
                )
        );

        com.rbkmoney.magista.domain.enums.InvoiceStatus invoiceStatusType = TypeUtil.toEnumField(
                rs.getString(INVOICE_DATA.INVOICE_STATUS.getName()),
                com.rbkmoney.magista.domain.enums.InvoiceStatus.class
        );

        statInvoice.setStatusChangedAt(
                TypeUtil.temporalToString(rs.getObject(INVOICE_DATA.EVENT_CREATED_AT.getName(), LocalDateTime.class)
        ));
        statInvoice.setStatus(MapperHelper.mapInvoiceStatus(rs, invoiceStatusType));

        String invoiceCartJson = rs.getString(INVOICE_DATA.INVOICE_CART_JSON.getName());
        if (invoiceCartJson != null) {
            statInvoice.setCart(DamselUtil.fromJson(invoiceCartJson, InvoiceCart.class));
        }

        byte[] context = rs.getBytes(INVOICE_DATA.INVOICE_CONTEXT.getName());
        if (context != null) {
            statInvoice.setContext(
                    new Content(
                            rs.getString(INVOICE_DATA.INVOICE_CONTEXT_TYPE.getName()),
                            ByteBuffer.wrap(context)
                    )
            );
        }
        return statInvoice;
    }
}
