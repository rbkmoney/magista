package com.rbkmoney.magista.config;

import com.rbkmoney.magista.retry.SimpleRetryPolicy;
import com.rbkmoney.woody.api.flow.error.WRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Collections;

@Configuration
public class RetryConfig {

    @Value("${retry-policy.maxAttempts}")
    int maxAttempts;

    @Bean
    RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(
                new SimpleRetryPolicy(maxAttempts, Collections.singletonMap(WRuntimeException.class, true))
        );
        retryTemplate.setBackOffPolicy(new ExponentialBackOffPolicy());

        return retryTemplate;
    }

}
