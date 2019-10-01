package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.magista.dao.RefundDao;
import com.rbkmoney.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.magista.domain.tables.pojos.RefundData;
import com.rbkmoney.magista.domain.tables.records.RefundDataRecord;
import com.rbkmoney.magista.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.magista.domain.tables.RefundData.REFUND_DATA;

@Component
public class RefundDaoImpl extends AbstractDao implements RefundDao {

    private final RowMapper<RefundData> refundRowMapper;

    public RefundDaoImpl(DataSource dataSource) {
        super(dataSource);
        refundRowMapper = new RecordRowMapper<>(REFUND_DATA, RefundData.class);
    }

    @Override
    public RefundData get(String invoiceId, String paymentId, String refundId) throws DaoException {
        Query query = getDslContext().selectFrom(REFUND_DATA)
                .where(
                        REFUND_DATA.INVOICE_ID.eq(invoiceId)
                                .and(REFUND_DATA.PAYMENT_ID.eq(paymentId))
                                .and(REFUND_DATA.REFUND_ID.eq(refundId))
                );
        return fetchOne(query, refundRowMapper);
    }

    @Override
    public void save(List<RefundData> refunds) throws DaoException {
        List<Query> queries = refunds.stream()
                .map(
                        refundData -> {
                            RefundDataRecord refundDataRecord = getDslContext().newRecord(REFUND_DATA, refundData);
                            refundDataRecord.changed(true);
                            refundDataRecord.changed(REFUND_DATA.ID, refundDataRecord.getId() != null);
                            return refundDataRecord;
                        }
                )
                .map(
                        refundDataRecord ->
                                getDslContext().insertInto(REFUND_DATA)
                                        .set(refundDataRecord)
                                        .onConflict(REFUND_DATA.INVOICE_ID, REFUND_DATA.PAYMENT_ID, REFUND_DATA.REFUND_ID)
                                        .doUpdate()
                                        .set(refundDataRecord)
                                        .where(REFUND_DATA.EVENT_ID.le(refundDataRecord.getEventId()))
                )
                .collect(Collectors.toList());
        batchExecute(queries);
    }
}
