package com.rbkmoney.magista.config;

import com.rbkmoney.magista.dao.InvoiceEventDao;
import com.rbkmoney.magista.dao.InvoiceEventDaoImpl;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.dao.StatisticsDaoImpl;
import com.rbkmoney.magista.domain.Mst;
import org.jooq.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

/**
 * Created by vpankrashkin on 10.08.16.
 */
@Configuration
public class DaoConfig {

    @Bean
    @DependsOn("dbInitializer")
    public StatisticsDao statisticsDao(DataSource ds) {
        return new StatisticsDaoImpl(ds);
    }

    @Bean
    @DependsOn("dbInitializer")
    public InvoiceEventDao invoiceEventDao(DataSource ds) {
        return new InvoiceEventDaoImpl(ds);
    }

    @Bean
    public Schema dbSchema() {
        return Mst.MST;
    }

}
