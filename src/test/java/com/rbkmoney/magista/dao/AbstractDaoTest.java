package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.config.DaoConfig;
import org.flywaydb.core.Flyway;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;

/**
 * Created by jeckep on 08.02.17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableConfigurationProperties
@ContextConfiguration(classes = {DaoConfig.class},
        loader = AnnotationConfigContextLoader.class,
        initializers = AbstractDaoTest.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractDaoTest {

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
                    "datasource.master.configuration=" + postgres.getPassword(),
                    "datasource.master.configuration.maximum-pool-size=" + 3,
                    "datasource.master.configuration.idle-timeout=" + 10000,
                    "datasource.slave.url=" + postgres.getJdbcUrl(),
                    "datasource.slave.username=" + postgres.getUsername(),
                    "datasource.slave.password=" + postgres.getPassword(),
                    "datasource.slave.configuration.maximum-pool-size=" + 3,
                    "datasource.slave.configuration.idle-timeout=" + 10000
            ).applyTo(configurableApplicationContext);

            Flyway flyway = new Flyway();
            flyway.setDataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
            flyway.setSchemas("mst");
            flyway.migrate();
        }
    }

}
