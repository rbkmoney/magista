package com.rbkmoney.magista.listener;

import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.ParseException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.service.SleepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.BackOffExecution;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class SafeMessageConsumer {

    private SleepService sleepService;
    private ExponentialBackOff exponentialBackOff;
    private ThreadLocal<BackOffExecution> backOffExecution;

    public SafeMessageConsumer(@Value("${invoice.listener.timeout:1000}") Long timeout, @Value("${invoice.listener.multiplier:1.5}") Double multiplier,
                               SleepService sleepService) {
        this.sleepService = sleepService;
        this.exponentialBackOff = new ExponentialBackOff(timeout, multiplier);
    }

    public <T> void safeMessageHandler(BiConsumer<T, Acknowledgment> biFunction, T message, Acknowledgment ack) {
        try {
            biFunction.accept(message, ack);
            ack.acknowledge();
            backOffExecution = null;
        } catch (NotFoundException | StorageException | ParseException ex) {
            if (backOffExecution == null) {
                backOffExecution = ThreadLocal.withInitial(() -> exponentialBackOff.start());
            }
            long timeout = backOffExecution.get().nextBackOff();
            log.warn("Failed to save event after handling, retrying (timeout = {})...", timeout, ex);
            sleepService.safeSleep(timeout);
            throw ex;
        }
    }

}
