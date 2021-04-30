package com.rbkmoney.magista.dao.impl;

import com.rbkmoney.magista.dao.AllocationDao;
import com.rbkmoney.magista.dao.impl.mapper.RecordRowMapper;
import com.rbkmoney.magista.domain.tables.pojos.AllocationTransactionData;
import com.rbkmoney.magista.domain.tables.records.AllocationTransactionDataRecord;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.magista.domain.Tables.ALLOCATION_TRANSACTION_DATA;

@Component
public class AllocationDaoImpl extends AbstractDao implements AllocationDao {

    private final RowMapper<AllocationTransactionData> allocationDataRowMapper;

    public AllocationDaoImpl(DataSource dataSource) {
        super(dataSource);
        this.allocationDataRowMapper =
                new RecordRowMapper<>(ALLOCATION_TRANSACTION_DATA, AllocationTransactionData.class);
    }

    @Override
    public AllocationTransactionData get(String invoiceId, String allocationId) {
        Query query = getDslContext().selectFrom(ALLOCATION_TRANSACTION_DATA)
                .where(ALLOCATION_TRANSACTION_DATA.INVOICE_ID.eq(invoiceId)
                        .and(ALLOCATION_TRANSACTION_DATA.ALLOCATION_ID.eq(allocationId)));
        return fetchOne(query, allocationDataRowMapper);
    }

    @Override
    public List<AllocationTransactionData> get(String invoiceId) {
        Query query = getDslContext().selectFrom(ALLOCATION_TRANSACTION_DATA)
                .where(ALLOCATION_TRANSACTION_DATA.INVOICE_ID.eq(invoiceId));
        return fetch(query, allocationDataRowMapper);
    }

    @Override
    public void save(List<AllocationTransactionData> allocationDataList) {
        List<Query> queries = allocationDataList.stream()
                .map(allocationData -> {
                    AllocationTransactionDataRecord allocationDataRecord =
                            getDslContext().newRecord(ALLOCATION_TRANSACTION_DATA, allocationData);
                    allocationDataRecord.changed(true);
                    allocationDataRecord.changed(ALLOCATION_TRANSACTION_DATA.ID, allocationDataRecord.getId() != null);
                    return allocationDataRecord;
                })
                .map(allocationDataRecord ->
                        getDslContext().insertInto(ALLOCATION_TRANSACTION_DATA)
                                .set(allocationDataRecord)
                                .onConflict(ALLOCATION_TRANSACTION_DATA.INVOICE_ID,
                                        ALLOCATION_TRANSACTION_DATA.ALLOCATION_ID)
                                .doUpdate()
                                .set(allocationDataRecord)
                )
                .collect(Collectors.toList());
        batchExecute(queries);
    }

    @Override
    public void update(List<AllocationTransactionData> allocationDataList) {
        List<Query> queries = allocationDataList.stream()
                .map(allocationData -> {
                    AllocationTransactionDataRecord allocationDataRecord =
                            getDslContext().newRecord(ALLOCATION_TRANSACTION_DATA, allocationData);
                    allocationDataRecord.changed(true);
                    allocationDataRecord.changed(ALLOCATION_TRANSACTION_DATA.ID, allocationDataRecord.getId() != null);
                    return allocationDataRecord;
                })
                .map(allocationDataRecord ->
                        getDslContext().update(ALLOCATION_TRANSACTION_DATA)
                                .set(allocationDataRecord)
                                .where(ALLOCATION_TRANSACTION_DATA.INVOICE_ID.eq(allocationDataRecord.getInvoiceId())
                                        .and(ALLOCATION_TRANSACTION_DATA.ALLOCATION_ID
                                                .eq(allocationDataRecord.getAllocationId()))
                                )
                )
                .collect(Collectors.toList());
        batchExecute(queries);
    }

}
