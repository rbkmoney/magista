package com.rbkmoney.magista.config;

import com.rbkmoney.magista.dao.*;
import com.rbkmoney.magista.domain.Mst;
import org.jooq.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Created by vpankrashkin on 10.08.16.
 */
@Configuration
public class DaoConfig {

    @Bean
    @DependsOn("dbInitializer")
    public StatisticsDao statisticsDao(DataSource ds,
                                       PlatformTransactionManager transactionManager,
                                       @Value("${cache.maxSize}") long cacheMaxSize,
                                       @Value("${cache.expireTime}") long expireTime) {
        return new StatisticsDaoImpl(ds, transactionManager, cacheMaxSize, expireTime);
    }

    @Bean
    @DependsOn("dbInitializer")
    public InvoiceEventDao invoiceEventDao(DataSource ds) {
        return new InvoiceEventDaoImpl(ds);
    }

    @Bean
    @DependsOn("dbInitializer")
    public PayoutEventDao payoutEventDao(DataSource ds) {
        return new PayoutEventDaoImpl(ds);
    }

    @Bean
    public Schema dbSchema() {
        return Mst.MST;
    }

}
