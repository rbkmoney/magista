package com.rbkmoney.magista.config;

import com.rbkmoney.magista.MagistaApplication;
import com.rbkmoney.magista.config.initializer.CommonPropertiesInitializer;
import com.rbkmoney.magista.config.initializer.PostgresqlTestcontainerAndPropertiesInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@ContextConfiguration(
        classes = MagistaApplication.class,
        initializers = {
                CommonPropertiesInitializer.class,
                PostgresqlTestcontainerAndPropertiesInitializer.class})
@TestPropertySource("classpath:application.yml")
@DirtiesContext
@Transactional
public @interface WithPostgresqlSpringBootITest {
}
