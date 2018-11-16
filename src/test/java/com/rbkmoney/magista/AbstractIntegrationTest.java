package com.rbkmoney.magista;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.magista.dao.ReportDao;
import com.rbkmoney.magista.dao.SearchDao;
import com.rbkmoney.magista.dao.StatisticsDao;
import com.rbkmoney.magista.query.impl.QueryContextFactoryImpl;
import com.rbkmoney.magista.query.impl.QueryProcessorImpl;
import com.rbkmoney.magista.query.impl.builder.QueryBuilderImpl;
import com.rbkmoney.magista.query.impl.parser.QueryParserImpl;
import com.rbkmoney.magista.dsl.parser.JsonQueryParser;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.EnvironmentTestUtils;
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
@TestPropertySource(properties = {"bm.pooling.enabled=false"})
@ContextConfiguration(classes = MagistaApplication.class, initializers = AbstractIntegrationTest.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AbstractIntegrationTest {

    protected QueryProcessorImpl queryProcessor;

    @Autowired
    private StatisticsDao statisticsDao;

    @Autowired
    private SearchDao searchDao;

    @Autowired
    private ReportDao reportDao;

    @Value("${local.server.port}")
    protected int port;

    @Before
    public void before() {
        QueryContextFactoryImpl contextFactory = new QueryContextFactoryImpl(statisticsDao, searchDao, reportDao);
        queryProcessor = new QueryProcessorImpl(new JsonQueryParser() {
            @Override
            protected ObjectMapper getMapper() {
                ObjectMapper mapper = super.getMapper();
                mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                return mapper;
            }
        }.withQueryParser(new QueryParserImpl()), new QueryBuilderImpl(), contextFactory);
    }

    @ClassRule
    public static PostgreSQLContainer postgres = (PostgreSQLContainer) new PostgreSQLContainer("postgres:9.6")
            .withStartupTimeout(Duration.ofMinutes(5));

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            EnvironmentTestUtils.addEnvironment("testcontainers", configurableApplicationContext.getEnvironment(),
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword(),
                    "flyway.url=" + postgres.getJdbcUrl(),
                    "flyway.user=" + postgres.getUsername(),
                    "flyway.password=" + postgres.getPassword()
            );
        }
    }
}
