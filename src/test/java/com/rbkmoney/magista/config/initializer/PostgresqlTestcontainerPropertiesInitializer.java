package com.rbkmoney.magista.config.initializer;

import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;

import static com.rbkmoney.magista.config.testcontainer.PostgresqlTestcontainerFactory.container;

public class PostgresqlTestcontainerPropertiesInitializer extends ConfigDataApplicationContextInitializer {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=" + container().getJdbcUrl(),
                "spring.datasource.username=" + container().getUsername(),
                "spring.datasource.password=" + container().getPassword(),
                "spring.flyway.url=" + container().getJdbcUrl(),
                "spring.flyway.user=" + container().getUsername(),
                "spring.flyway.password=" + container().getPassword(),
                "flyway.url=" + container().getJdbcUrl(),
                "flyway.user=" + container().getUsername(),
                "flyway.password=" + container().getPassword())
                .applyTo(configurableApplicationContext);
    }
}
