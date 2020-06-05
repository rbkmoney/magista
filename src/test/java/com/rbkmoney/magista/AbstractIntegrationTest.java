package com.rbkmoney.magista;

import com.rbkmoney.magista.dao.ReportDao;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Created by jeckep on 08.02.17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = {"payouter.pooling.enabled=false"})
@ContextConfiguration(classes = MagistaApplication.class, initializers = AbstractIntegrationTest.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractIntegrationTest {

    protected QueryProcessorImpl queryProcessor;

    @Autowired
    private StatisticsDao statisticsDao;

    @Autowired
    private SearchDao searchDao;

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private TokenGenService tokenGenService;

    @Value("${local.server.port}")
    protected int port;

    @Before
    public void before() {
        QueryContextFactoryImpl contextFactory = new QueryContextFactoryImpl(statisticsDao, searchDao, reportDao, tokenGenService);
        queryProcessor = new QueryProcessorImpl(JsonQueryParser.newWeakJsonQueryParser(), new QueryBuilderImpl(), contextFactory);
    }

    @ClassRule
    public static PostgreSQLContainer postgres = (PostgreSQLContainer) new PostgreSQLContainer("postgres:9.6")
            .withStartupTimeout(Duration.ofMinutes(5));

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "datasource.master.url=" + postgres.getJdbcUrl(),
                    "datasource.master.username=" + postgres.getUsername(),
                    "datasource.master.password=" + postgres.getPassword(),
                    "datasource.slave.url=" + postgres.getJdbcUrl(),
                    "datasource.slave.username=" + postgres.getUsername(),
                    "datasource.slave.password=" + postgres.getPassword(),
                    "flyway.url=" + postgres.getJdbcUrl(),
                    "flyway.user=" + postgres.getUsername(),
                    "flyway.password=" + postgres.getPassword()
            ).applyTo(configurableApplicationContext);
        }
    }
}
