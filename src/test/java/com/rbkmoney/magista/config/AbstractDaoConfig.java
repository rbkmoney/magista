package com.rbkmoney.magista.config;

import com.rbkmoney.magista.MagistaApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.Stream;

import static io.github.benas.randombeans.EnhancedRandomBuilder.aNewEnhancedRandom;

@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = MagistaApplication.class,
        initializers = AbstractDaoConfig.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource("classpath:application.yml")
public abstract class AbstractDaoConfig {

    private static final String POSTGRESQL_IMAGE_NAME = "postgres";
    private static final String POSTGRESQL_VERSION = "11.4";

    @Container
    public static final PostgreSQLContainer POSTGRESQL_CONTAINER = new PostgreSQLContainer(
            DockerImageName
                    .parse(POSTGRESQL_IMAGE_NAME)
                    .withTag(POSTGRESQL_VERSION));

    public static class Initializer extends ConfigDataApplicationContextInitializer {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + POSTGRESQL_CONTAINER.getJdbcUrl(),
                    "spring.datasource.username=" + POSTGRESQL_CONTAINER.getUsername(),
                    "spring.datasource.password=" + POSTGRESQL_CONTAINER.getPassword(),
                    "spring.flyway.url=" + POSTGRESQL_CONTAINER.getJdbcUrl(),
                    "spring.flyway.user=" + POSTGRESQL_CONTAINER.getUsername(),
                    "spring.flyway.password=" + POSTGRESQL_CONTAINER.getPassword(),
                    "flyway.url=" + POSTGRESQL_CONTAINER.getJdbcUrl(),
                    "flyway.user=" + POSTGRESQL_CONTAINER.getUsername(),
                    "flyway.password=" + POSTGRESQL_CONTAINER.getPassword(),
                    "token-gen.key=" + "jXnZr4u7x!A%D*G-KaPvSgVkYp3s5v8t/B?E(H+MbQeThWmZq4t7w9z$C&F)J@Nc",
                    "cache.invoiceData.size=10000",
                    "cache.paymentData.size=10000",
                    "payouter.pooling.enabled=false")
                    .applyTo(configurableApplicationContext);
        }
    }

    public static <T> T random(Class<T> type, String... excludedFields) {
        return aNewEnhancedRandom().nextObject(type, excludedFields);
    }

    public static <T> Stream<T> randomStreamOf(int amount, Class<T> type, String... excludedFields) {
        return aNewEnhancedRandom().objects(type, amount, excludedFields);
    }
}
