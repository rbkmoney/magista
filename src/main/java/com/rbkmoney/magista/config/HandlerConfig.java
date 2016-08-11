package com.rbkmoney.magista.config;

import com.rbkmoney.damsel.merch_stat.MerchantStatisticsSrv;
import com.rbkmoney.magista.query.impl.JsonQueryParser;
import com.rbkmoney.magista.query.impl.QueryContextBuilder;
import com.rbkmoney.magista.repository.dao.StatisticsDao;
import com.rbkmoney.magista.service.MerchantStatisticsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by vpankrashkin on 10.08.16.
 */
@Configuration
public class HandlerConfig {

    @Bean
    public MerchantStatisticsSrv.Iface merchantStatisticsHandler(StatisticsDao statisticsDao) {
        return new MerchantStatisticsHandler(new JsonQueryParser(), new QueryContextBuilder(statisticsDao));
    }
}
