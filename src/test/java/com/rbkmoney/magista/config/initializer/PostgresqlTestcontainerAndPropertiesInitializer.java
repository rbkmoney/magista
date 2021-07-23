package com.rbkmoney.magista.config.initializer;

import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;

import static com.rbkmoney.magista.config.testcontainer.SingletonPostgresqlTestcontainer.POSTGRESQL_CONTAINER;

public class PostgresqlTestcontainerAndPropertiesInitializer extends ConfigDataApplicationContextInitializer {

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
