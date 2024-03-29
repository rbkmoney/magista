package com.rbkmoney.magista.config;

import com.rbkmoney.kafka.common.retry.ConfigurableRetryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;

@Configuration
public class RetryConfig {

    @Value("${retry-policy.maxAttempts}")
    int maxAttempts;

    @Bean
    RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(
                new ConfigurableRetryPolicy(maxAttempts, Map.of(RuntimeException.class, true))
        );
        retryTemplate.setBackOffPolicy(new ExponentialBackOffPolicy());

        return retryTemplate;
    }

}
