package com.rbkmoney.magista.config;

import com.rbkmoney.magista.dao.*;
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
    public InvoiceDaoImpl invoiceDao(DataSource ds) {
        return new InvoiceDaoImpl(ds);
    }

    @Bean
    @DependsOn("dbInitializer")
    public PaymentDaoImpl paymentDao(DataSource ds) {
        return new PaymentDaoImpl(ds);
    }

    @Bean
    @DependsOn("dbInitializer")
    public CustomerDaoImpl customerDao(DataSource ds) {
        return new CustomerDaoImpl(ds);
    }
}
