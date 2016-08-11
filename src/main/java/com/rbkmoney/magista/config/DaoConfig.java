package com.rbkmoney.magista.config;

import com.rbkmoney.magista.repository.dao.StatisticsDao;
import com.rbkmoney.magista.repository.dao.StatisticsDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by vpankrashkin on 10.08.16.
 */
@Configuration
public class DaoConfig {

    @Bean
    public StatisticsDao statisticsDao(DataSource ds) {
        return new StatisticsDaoImpl(ds);
    }
}
