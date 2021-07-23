package com.rbkmoney.magista.config;

import com.rbkmoney.magista.MagistaApplication;
import com.rbkmoney.magista.config.initializer.CommonPropertiesInitializer;
import com.rbkmoney.magista.config.initializer.KafkaTestcontainerAndPropertiesInitializer;
import com.rbkmoney.magista.config.initializer.PostgresqlTestcontainerAndPropertiesInitializer;
import com.rbkmoney.magista.config.testconfiguration.KafkaProducerConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
        classes = {
                MagistaApplication.class,
                KafkaProducerConfig.class},
        initializers = {
                CommonPropertiesInitializer.class,
                PostgresqlTestcontainerAndPropertiesInitializer.class,
                KafkaTestcontainerAndPropertiesInitializer.class})
@TestPropertySource("classpath:application.yml")
@DirtiesContext
@Transactional
public @interface PostgresqlKafkaMagistaIntegrationTest {
}
