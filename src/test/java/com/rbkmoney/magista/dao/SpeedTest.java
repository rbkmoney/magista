package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.enums.InvoiceEventCategory;
import com.rbkmoney.magista.domain.enums.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.domain.tables.records.InvoiceEventStatRecord;
import org.jooq.Query;
import org.jooq.conf.ParamType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.rbkmoney.magista.domain.Tables.INVOICE_EVENT_STAT;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;

/**
 * Created by tolkonepiu on 16/06/2017.
 */
/*
Result insertion of 10000 events (without jooq), time = 139207 ms, average = 13.9207, min = 9, max = 425
Result updates of 10000 events (without jooq), time = 134775 ms, average = 13.4775, min = 9, max = 368
Result insertion of 10000 events (pojo), time = 137330 ms, average = 13.733, min = 9, max = 338
Result updates of 10000 events (pojo), time = 140430 ms, average = 14.043, min = 9, max = 1111
Result insertion of 10000 events (record), time = 147695 ms, average = 14.7695, min = 9, max = 452
Result updates of 10000 events (record), time = 136864 ms, average = 13.6864, min = 9, max = 45
 */
@Ignore
public class SpeedTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    InvoiceEventDaoImpl invoiceEventDao;

    List<InvoiceEventStat> invoiceEventStatList;
    List<InvoiceEventStatRecord> invoiceEventStatRecords;

    @Before
    public void setup() {
        invoiceEventStatList = randomListOf(10000, InvoiceEventStat.class);
        invoiceEventStatRecords = invoiceEventStatList.stream()
                .map(t -> invoiceEventDao.getDslContext().newRecord(INVOICE_EVENT_STAT, t))
                .collect(Collectors.toList());
    }

//    @Test
//    public void insertRecordQueries() {
//        log.info("Start the insertion of {} events (record)", invoiceEventStatRecords.size());
//        LongSummaryStatistics recordInsertStat = invoiceEventStatRecords.stream()
//                .flatMapToLong(
//                        t -> {
//                            long currentTime = System.currentTimeMillis();
//                            invoiceEventDao.insert(t);
//                            return LongStream.of(System.currentTimeMillis() - currentTime);
//                        }
//                ).summaryStatistics();
//        log.info("Result insertion of {} events (record), time = {} ms, average = {}, min = {}, max = {}",
//                recordInsertStat.getCount(), recordInsertStat.getSum(), recordInsertStat.getAverage(),
//                recordInsertStat.getMin(), recordInsertStat.getMax());
//
//        log.info("Start the update of {} events (record)", invoiceEventStatRecords.size());
//        LongSummaryStatistics recordUpdateStat = invoiceEventStatRecords.stream()
//                .flatMapToLong(
//                        t -> {
//                            InvoiceEventStatRecord invoiceEventStatRecord =
//                                    invoiceEventDao.getDslContext().newRecord(INVOICE_EVENT_STAT, t);
//                            invoiceEventStatRecord.setEventCategory(InvoiceEventCategory.PAYMENT);
//                            invoiceEventStatRecord.setPaymentStatus(InvoicePaymentStatus.captured);
//                            invoiceEventStatRecord.setEventCreatedAt(LocalDateTime.now());
//
//                            long currentTime = System.currentTimeMillis();
//                            invoiceEventDao.update(invoiceEventStatRecord);
//                            return LongStream.of(System.currentTimeMillis() - currentTime);
//                        }
//                ).summaryStatistics();
//        log.info("Result updates of {} events (record), time = {} ms, average = {}, min = {}, max = {}",
//                recordUpdateStat.getCount(), recordUpdateStat.getSum(), recordUpdateStat.getAverage(),
//                recordUpdateStat.getMin(), recordUpdateStat.getMax());
//
//        JdbcTestUtils.deleteFromTables(invoiceEventDao.getJdbcTemplate(), "mst.invoice_event_stat");
//
//    }

    @Test
    public void insertSqlQueries() {
        log.info("Start the insertion of {} events (without jooq)", invoiceEventStatRecords.size());
        LongSummaryStatistics sqlInsertStat = invoiceEventStatRecords.stream()
                .flatMapToLong(
                        t -> {
                            Query query = invoiceEventDao.getDslContext().insertInto(INVOICE_EVENT_STAT)
                                    .set(t);

                            String namedSql = query.getSQL(ParamType.NAMED);
                            SqlParameterSource parameterSource = invoiceEventDao.toSqlParameterSource(query.getParams());

                            long currentTime = System.currentTimeMillis();
                            invoiceEventDao.executeOne(namedSql, parameterSource);
                            return LongStream.of(System.currentTimeMillis() - currentTime);
                        }
                ).summaryStatistics();
        log.info("Result insertion of {} events (without jooq), time = {} ms, average = {}, min = {}, max = {}",
                sqlInsertStat.getCount(), sqlInsertStat.getSum(), sqlInsertStat.getAverage(),
                sqlInsertStat.getMin(), sqlInsertStat.getMax());

        log.info("Start the update of {} events (without jooq)", invoiceEventStatRecords.size());
        LongSummaryStatistics sqlUpdateStat = invoiceEventStatRecords.stream()
                .flatMapToLong(
                        t -> {
                            InvoiceEventStatRecord invoiceEventStatRecord =
                                    invoiceEventDao.getDslContext().newRecord(INVOICE_EVENT_STAT, t);
                            invoiceEventStatRecord.setEventCategory(InvoiceEventCategory.PAYMENT);
                            invoiceEventStatRecord.setPaymentStatus(InvoicePaymentStatus.captured);
                            invoiceEventStatRecord.setEventCreatedAt(LocalDateTime.now());

                            Query query = invoiceEventDao.getDslContext().update(INVOICE_EVENT_STAT)
                                    .set(invoiceEventStatRecord)
                                    .where(INVOICE_EVENT_STAT.INVOICE_ID.eq(invoiceEventStatRecord.getInvoiceId()))
                                    .and(INVOICE_EVENT_STAT.PAYMENT_ID.eq(invoiceEventStatRecord.getPaymentId()));

                            String namedSql = query.getSQL(ParamType.NAMED);
                            SqlParameterSource parameterSource = invoiceEventDao.toSqlParameterSource(query.getParams());

                            long currentTime = System.currentTimeMillis();
                            invoiceEventDao.executeOne(namedSql, parameterSource);
                            return LongStream.of(System.currentTimeMillis() - currentTime);
                        }
                ).summaryStatistics();
        log.info("Result updates of {} events (without jooq), time = {} ms, average = {}, min = {}, max = {}",
                sqlUpdateStat.getCount(), sqlUpdateStat.getSum(), sqlUpdateStat.getAverage(),
                sqlUpdateStat.getMin(), sqlUpdateStat.getMax());

        JdbcTestUtils.deleteFromTables(invoiceEventDao.getJdbcTemplate(), "mst.invoice_event_stat");
    }
}
