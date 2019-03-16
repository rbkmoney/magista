package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.event_stock.SourceEvent;
import com.rbkmoney.damsel.payout_processing.Event;
import com.rbkmoney.damsel.payout_processing.EventPayload;
import com.rbkmoney.damsel.payout_processing.PayoutChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.exception.ParseException;
import com.rbkmoney.magista.service.HandlerManager;
import com.rbkmoney.magista.service.SleepService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.Acknowledgment;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;

public class PayoutListenerTest {

    @Mock
    private HandlerManager handlerManager;
    @Mock
    private Handler handler;
    @Mock
    private Processor processor;
    @Mock
    private SleepService sleepService;
    @Mock
    private SourceEventParser eventParser;
    @Mock
    private Acknowledgment ack;

    private PayoutListener payoutListener;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        payoutListener = new PayoutListener(handlerManager, new SafeMessageConsumer(1000L, 1.5, sleepService), eventParser);
    }

    @Test
    public void listenEmptyChanges() {
        MachineEvent message = new MachineEvent();
        SourceEvent sourceEvent = new SourceEvent();
        Event event = new Event();
        EventPayload payload = new EventPayload();
        payload.setPayoutChanges(new ArrayList<>());
        event.setPayload(payload);
        sourceEvent.setPayoutEvent(event);
        Mockito.when(eventParser.parseEvent(message)).thenReturn(sourceEvent);

        payoutListener.handle(message, ack);

        Mockito.verify(processor, Mockito.times(0)).execute();
    }

    @Test(expected = ParseException.class)
    public void listenEmptyException() {
        MachineEvent message = new MachineEvent();
        Mockito.when(eventParser.parseEvent(message)).thenThrow(new ParseException());
        payoutListener.handle(message, ack);
    }

    @Test
    public void listenChanges() {
        MachineEvent message = new MachineEvent();
        SourceEvent sourceEvent = new SourceEvent();
        Event event = new Event();
        EventPayload payload = new EventPayload();
        ArrayList<PayoutChange> payoutChanges = new ArrayList<>();
        payoutChanges.add(new PayoutChange());
        payload.setPayoutChanges(payoutChanges);
        event.setPayload(payload);
        sourceEvent.setPayoutEvent(event);
        Mockito.when(eventParser.parseEvent(message)).thenReturn(sourceEvent);
        Mockito.when(handler.handle(any(), any())).thenReturn(processor);
        Mockito.when(handlerManager.getHandler(any())).thenReturn(handler);

        payoutListener.handle(message, ack);

        Mockito.verify(processor, Mockito.times(1)).execute();
    }
}