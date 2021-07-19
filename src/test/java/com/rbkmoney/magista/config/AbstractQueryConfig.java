package com.rbkmoney.magista.config;

import com.rbkmoney.magista.MagistaApplication;
import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.query.impl.QueryContextFactoryImpl;
import com.rbkmoney.magista.query.impl.QueryProcessorImpl;
import com.rbkmoney.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import com.rbkmoney.magista.service.TokenGenService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ContextConfiguration(
        classes = MagistaApplication.class,
        initializers = AbstractQueryConfig.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource("classpath:application.yml")
public abstract class AbstractQueryConfig extends AbstractDaoConfig {

    protected QueryProcessorImpl queryProcessor;

    @Autowired
    private StatisticsDao statisticsDao;

    @Autowired
    private SearchDao searchDao;

    @Autowired
    private TokenGenService tokenGenService;

    @BeforeEach
    public void setup() {
        QueryContextFactoryImpl contextFactory = new QueryContextFactoryImpl(statisticsDao, searchDao, tokenGenService);
        queryProcessor = new QueryProcessorImpl(
                JsonQueryParser.newWeakJsonQueryParser(),
                new QueryBuilderImpl(),
                contextFactory);
    }
}
