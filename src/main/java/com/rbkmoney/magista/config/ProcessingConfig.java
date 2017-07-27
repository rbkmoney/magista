package com.rbkmoney.magista.config;

import com.rbkmoney.magista.event.EventSaver;
import com.rbkmoney.magista.event.Processor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ProcessingConfig {

    @Value("${bm.pooling.handler.maxPoolSize}")
    int maxPoolSize;

    @Value("${bm.pooling.handler.queue.limit}")
    int queueLimit;

    @Value("${bm.pooling.handler.timeout}")
    long timeout;

    @Bean
    BlockingQueue<Future<Processor>> queue() {
        return new LinkedBlockingQueue(queueLimit);
    }

    @Bean
    EventSaver eventSaver(BlockingQueue<Future<Processor>> queue) {
        return new EventSaver(queue, queueLimit);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(maxPoolSize, new ThreadFactory() {
            AtomicInteger counter = new AtomicInteger();
            ThreadGroup group = new ThreadGroup("HandleGroup");

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(group, r, "EventHandler-" + counter.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        });
    }

}
