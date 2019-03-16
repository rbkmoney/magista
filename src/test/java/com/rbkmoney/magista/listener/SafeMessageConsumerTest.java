package com.rbkmoney.magista.listener;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.exception.ParseException;
import com.rbkmoney.magista.service.SleepService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.Acknowledgment;

import java.util.function.BiConsumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

public class SafeMessageConsumerTest {

    @Mock
    private SleepService sleepService;
    @Mock
    private Acknowledgment ack;
    @Mock
    private BiConsumer<MachineEvent, Acknowledgment> biConsumer;
    private SafeMessageConsumer safeMessageConsumer;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        safeMessageConsumer = new SafeMessageConsumer(1000L, 1.5, sleepService);
    }

    @Test
    public void listenEmptyChanges() {
        safeMessageConsumer.safeMessageHandler(biConsumer, new MachineEvent(), ack);
        Mockito.verify(ack, Mockito.times(1)).acknowledge();
    }

    @Test
    public void listenWithTwoRetryAckAndOneMoreRetry() {
        doThrow(ParseException.class)
                .when(biConsumer)
                .accept(any(), any());

        safeMessageConsumer.safeMessageHandler(biConsumer, new MachineEvent(), ack);
        Mockito.verify(ack, Mockito.times(0)).acknowledge();
        Mockito.verify(sleepService, Mockito.times(1)).safeSleep(1000L);

        safeMessageConsumer.safeMessageHandler(biConsumer, new MachineEvent(), ack);
        Mockito.verify(sleepService, Mockito.times(1)).safeSleep(1500L);

        Mockito.reset(biConsumer);
        safeMessageConsumer.safeMessageHandler(biConsumer, new MachineEvent(), ack);
        Mockito.verify(ack, Mockito.times(1)).acknowledge();

        Mockito.reset(biConsumer);
        Mockito.reset(sleepService);
        doThrow(ParseException.class)
                .when(biConsumer)
                .accept(any(), any());
        safeMessageConsumer.safeMessageHandler(biConsumer, new MachineEvent(), ack);
        Mockito.verify(sleepService, Mockito.times(1)).safeSleep(1000L);
    }


    @Test
    public void listenWithTwoRetryMultithreads() {
        doThrow(ParseException.class)
                .when(biConsumer)
                .accept(any(), any());
        Runnable thread = () -> {
            safeMessageConsumer.safeMessageHandler(biConsumer, new MachineEvent(), ack);
            Mockito.verify(ack, Mockito.times(0)).acknowledge();
            Mockito.verify(sleepService, Mockito.times(1)).safeSleep(1000L);
        };

        thread.run();
        thread.run();
    }
}