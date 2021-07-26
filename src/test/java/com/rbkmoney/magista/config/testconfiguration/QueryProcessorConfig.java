package com.rbkmoney.magista.config.testconfiguration;

import com.rbkmoney.damsel.merch_stat.StatRequest;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.query.QueryProcessor;
import com.rbkmoney.magista.query.impl.QueryContextFactoryImpl;
import com.rbkmoney.magista.query.impl.QueryProcessorImpl;
import com.rbkmoney.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import com.rbkmoney.magista.service.TokenGenService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class QueryProcessorConfig {

    @Bean
    public QueryProcessor<StatRequest, StatResponse> queryProcessor(
            StatisticsDao statisticsDao,
            SearchDao searchDao,
            TokenGenService tokenGenService) {
        var contextFactory = new QueryContextFactoryImpl(statisticsDao, searchDao, tokenGenService);
        return new QueryProcessorImpl(
                JsonQueryParser.newWeakJsonQueryParser(),
                new QueryBuilderImpl(),
                contextFactory);
    }
}
