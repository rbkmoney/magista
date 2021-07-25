package com.rbkmoney.magista.config.testcontainers.kafka.demo;

import com.rbkmoney.magista.config.testcontainers.spring.boot.test.context.KafkaProducerSpringBootTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * this is a demo example of filling in annotation, do not use unless absolutely necessary
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@DemoKafkaTestcontainerSingleton
@KafkaProducerSpringBootTest
public @interface DemoWithKafkaSingletonSpringBootITest {
}
