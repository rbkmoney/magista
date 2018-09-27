package com.rbkmoney.magista.config;

import com.rbkmoney.magista.dao.*;
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
    public PayoutEventDao payoutEventDao(DataSource ds) {
        return new PayoutEventDaoImpl(ds);
    }

    @Bean
    @DependsOn("dbInitializer")
    public AdjustmentDao adjustmentDao(DataSource ds) {
        return new AdjustmentDaoImpl(ds);
    }

    @Bean
    @DependsOn("dbInitializer")
    public RefundDao refundDao(DataSource ds) {
        return new RefundDaoImpl(ds);
    }

    @Bean
    public Schema dbSchema() {
        return Mst.MST;
    }

}
