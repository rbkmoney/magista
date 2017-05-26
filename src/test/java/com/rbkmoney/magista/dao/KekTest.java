package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Map;

import static com.rbkmoney.magista.domain.Tables.INVOICE_EVENT_STAT;
import static io.github.benas.randombeans.api.EnhancedRandom.random;

/**
 * Created by tolkonepiu on 26/05/2017.
 */
public class KekTest {

    @Test
    public void kekTest() {

        InvoiceEventStat invoiceEventStat = random(InvoiceEventStat.class);

        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.POSTGRES_9_5);
        DSLContext dslContext = DSL.using(configuration);

        Query query = dslContext.insertInto(INVOICE_EVENT_STAT)
                .set(dslContext.newRecord(INVOICE_EVENT_STAT, invoiceEventStat));

//        System.out.println(query.getSQL(ParamType.NAMED));
//        System.out.println(dslContext.renderNamedParams(query));
//        System.out.println(query.getParams());
        System.out.println(toSqlParameterSource(query.getParams()));
    }

    private SqlParameterSource toSqlParameterSource(Map<String, Param<?>> params) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        for (Map.Entry<String, Param<?>> entry : params.entrySet()) {
            System.out.println(entry);
            Param<?> param = entry.getValue();
            if (param.getDataType().getType().isAssignableFrom(LocalDateTime.class)) {
                sqlParameterSource.addValue(entry.getKey(), param.getValue(), Types.OTHER);
            } else if (Enum.class.isAssignableFrom(param.getDataType().getType())) {
                sqlParameterSource.addValue(entry.getKey(), param.getValue(), Types.VARCHAR);
            } else {
                sqlParameterSource.addValue(entry.getKey(), param.getValue());
            }
        }
        return sqlParameterSource;
    }

}
