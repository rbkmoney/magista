package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.domain.tables.records.InvoiceEventStatRecord;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.util.TypeUtil;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSetMetaData;

import static com.rbkmoney.magista.domain.Tables.INVOICE_EVENT_STAT;

/**
 * Created by tolkonepiu on 26/05/2017.
 */
public class InvoiceEventDaoImpl extends AbstractDao implements InvoiceEventDao {

    public InvoiceEventDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Long getLastEventId() throws DaoException {
        Query query = getDslContext().select(INVOICE_EVENT_STAT.EVENT_ID.max()).from(INVOICE_EVENT_STAT);
        return fetchOne(query, Long.class);
    }

    @Override
    public InvoiceEventStat findPaymentByInvoiceAndPaymentId(String invoiceId, String paymentId) throws DaoException {
        Query query = getDslContext().selectFrom(INVOICE_EVENT_STAT)
                .where(INVOICE_EVENT_STAT.INVOICE_ID.eq(invoiceId)
                        .and(INVOICE_EVENT_STAT.PAYMENT_ID.eq(paymentId))
                        .and(INVOICE_EVENT_STAT.EVENT_CATEGORY.eq(InvoiceEventCategory.PAYMENT)));
        return fetchOne(query, getRowMapper());
    }

    @Override
    public InvoiceEventStat findInvoiceById(String invoiceId) throws DaoException {
        Query query = getDslContext().selectFrom(INVOICE_EVENT_STAT)
                .where(INVOICE_EVENT_STAT.INVOICE_ID.eq(invoiceId)
                        .and(INVOICE_EVENT_STAT.EVENT_CATEGORY.eq(InvoiceEventCategory.INVOICE)));
        return fetchOne(query, getRowMapper(), getNamedParameterJdbcTemplate());
    }

    @Override
    public void insert(InvoiceEventStat invoiceEventStat) throws DaoException {
        Query query = getDslContext().insertInto(INVOICE_EVENT_STAT)
                .set(getDslContext().newRecord(INVOICE_EVENT_STAT, invoiceEventStat));

        executeOne(query);
    }

    @Override
    public void update(InvoiceEventStat invoiceEventStat) throws DaoException {
        Condition condition;
        if (invoiceEventStat.getEventCategory() == InvoiceEventCategory.INVOICE) {
            condition = INVOICE_EVENT_STAT.PAYMENT_ID.isNull();
        } else {
            condition = INVOICE_EVENT_STAT.PAYMENT_ID.eq(invoiceEventStat.getPaymentId());
        }

        Query query = getDslContext().update(INVOICE_EVENT_STAT)
                .set(getDslContext().newRecord(INVOICE_EVENT_STAT, invoiceEventStat))
                .where(INVOICE_EVENT_STAT.INVOICE_ID.eq(invoiceEventStat.getInvoiceId()))
                .and(condition);

        executeOne(query);
    }

    public static RowMapper<InvoiceEventStat> getRowMapper() {
        return (rs, i) -> {
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();

            InvoiceEventStatRecord invoiceEventStatRecord = new InvoiceEventStatRecord();
            for (int column = 1; column <= columnCount; column++) {
                String columnName = rsMetaData.getColumnName(column);
                Field field = invoiceEventStatRecord.field(columnName);

                Object value;
                if (field.getDataType().isBinary()) {
                    value = rs.getBytes(field.getName());
                } else if (field.getType().isEnum()) {
                    value = TypeUtil.toEnumField(rs.getString(field.getName()), field.getType());
                } else {
                    value = rs.getObject(field.getName(), field.getType());
                }
                if (value != null) {
                    invoiceEventStatRecord.set(field, value);
                }
            }
            return invoiceEventStatRecord.into(InvoiceEventStat.class);
        };
    }
}
