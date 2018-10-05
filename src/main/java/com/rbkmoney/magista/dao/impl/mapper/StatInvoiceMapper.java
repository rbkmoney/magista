package com.rbkmoney.magista.dao.impl.mapper;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.InvoiceCart;
import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.util.DamselUtil;
import org.springframework.jdbc.core.RowMapper;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Map;

import static com.rbkmoney.magista.domain.tables.InvoiceData.INVOICE_DATA;
import static com.rbkmoney.magista.domain.tables.InvoiceEvent.INVOICE_EVENT;

public class StatInvoiceMapper implements RowMapper<Map.Entry<Long, StatInvoice>> {

    @Override
    public Map.Entry<Long, StatInvoice> mapRow(ResultSet rs, int i) throws SQLException {
        StatInvoice statInvoice = new StatInvoice();
        statInvoice.setId(rs.getString(INVOICE_DATA.INVOICE_ID.getName()));
        statInvoice.setOwnerId(rs.getString(INVOICE_DATA.PARTY_ID.getName()));
        statInvoice.setShopId(rs.getString(INVOICE_DATA.PARTY_SHOP_ID.getName()));
        statInvoice.setAmount(rs.getLong(INVOICE_DATA.INVOICE_AMOUNT.getName()));
        statInvoice.setCurrencySymbolicCode(rs.getString(INVOICE_DATA.INVOICE_CURRENCY_CODE.getName()));
        statInvoice.setProduct(rs.getString(INVOICE_DATA.INVOICE_PRODUCT.getName()));
        statInvoice.setDescription(rs.getString(INVOICE_DATA.INVOICE_DESCRIPTION.getName()));
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
                rs.getString(INVOICE_EVENT.INVOICE_STATUS.getName()),
                com.rbkmoney.magista.domain.enums.InvoiceStatus.class
        );

        String eventCreatedAtString = TypeUtil.temporalToString(
                rs.getObject(INVOICE_EVENT.EVENT_CREATED_AT.getName(), LocalDateTime.class)
        );

        InvoiceStatus invoiceStatus;
        switch (invoiceStatusType) {
            case cancelled:
                InvoiceCancelled invoiceCancelled = new InvoiceCancelled();
                invoiceCancelled.setDetails(rs.getString(INVOICE_EVENT.INVOICE_STATUS_DETAILS.getName()));
                invoiceCancelled.setAt(eventCreatedAtString);
                invoiceStatus = InvoiceStatus.cancelled(invoiceCancelled);
                break;
            case unpaid:
                invoiceStatus = InvoiceStatus.unpaid(new InvoiceUnpaid());
                break;
            case paid:
                InvoicePaid invoicePaid = new InvoicePaid();
                invoicePaid.setAt(eventCreatedAtString);
                invoiceStatus = InvoiceStatus.paid(invoicePaid);
                break;
            case fulfilled:
                InvoiceFulfilled invoiceFulfilled = new InvoiceFulfilled();
                invoiceFulfilled.setAt(eventCreatedAtString);
                invoiceFulfilled.setDetails(rs.getString(INVOICE_EVENT.INVOICE_STATUS_DETAILS.getName()));
                invoiceStatus = InvoiceStatus.fulfilled(invoiceFulfilled);
                break;
            default:
                throw new NotFoundException(String.format("Invoice status '%s' not found", invoiceStatusType.getLiteral()));
        }
        statInvoice.setStatus(invoiceStatus);

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
        return new AbstractMap.SimpleEntry<>(rs.getLong(INVOICE_DATA.ID.getName()), statInvoice);
    }

}
