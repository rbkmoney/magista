package com.rbkmoney.magista;

import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.query.impl.QueryContextFactoryImpl;
import com.rbkmoney.magista.query.impl.QueryProcessorImpl;
import com.rbkmoney.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query.impl.parser.JsonQueryParser;
import com.rbkmoney.magista.service.TokenGenService;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;

/**
 * Created by jeckep on 08.02.17.
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MagistaApplication.class, initializers = AbstractIntegrationTest.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractIntegrationTest {

    @ClassRule
    public static PostgreSQLContainer postgres = (PostgreSQLContainer) new PostgreSQLContainer("postgres:11.4")
            .withStartupTimeout(Duration.ofMinutes(5));
    protected QueryProcessorImpl queryProcessor;
    @Autowired
    private StatisticsDao statisticsDao;
    @Autowired
    private SearchDao searchDao;
    @Autowired
    private TokenGenService tokenGenService;

    @Before
    public void before() {
        QueryContextFactoryImpl contextFactory = new QueryContextFactoryImpl(statisticsDao, searchDao, tokenGenService);
        queryProcessor = new QueryProcessorImpl(JsonQueryParser.newWeakJsonQueryParser(), new QueryBuilderImpl(),
                contextFactory);
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword(),
                    "flyway.url=" + postgres.getJdbcUrl(),
                    "flyway.user=" + postgres.getUsername(),
                    "flyway.password=" + postgres.getPassword()
            ).applyTo(configurableApplicationContext);
        }
    }

}
