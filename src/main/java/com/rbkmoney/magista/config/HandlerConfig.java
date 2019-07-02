package com.rbkmoney.magista.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.endpoint.StatisticsServletIface;
import com.rbkmoney.magista.query.impl.QueryContextFactoryImpl;
import com.rbkmoney.magista.query.impl.QueryProcessorImpl;
import com.rbkmoney.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import com.rbkmoney.magista.service.MerchantStatisticsHandler;
import com.rbkmoney.magista.service.TokenGenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by vpankrashkin on 10.08.16.
 */
@Configuration
public class HandlerConfig {

    @Bean
    public StatisticsServletIface statisticsHandler(
            StatisticsDao statisticsDao,
            SearchDao searchDao,
            TokenGenService tokenGenService
    ) {
        return new MerchantStatisticsHandler(new QueryProcessorImpl(new JsonQueryParser() {
            @Override
            protected ObjectMapper getMapper() {
                ObjectMapper mapper = super.getMapper();
                mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                return mapper;
            }
        }, new QueryBuilderImpl(), new QueryContextFactoryImpl(statisticsDao, searchDao, tokenGenService)));
    }
}
