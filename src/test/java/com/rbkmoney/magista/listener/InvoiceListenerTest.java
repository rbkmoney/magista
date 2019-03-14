package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.event_stock.SourceEvent;
import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
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

public class InvoiceListenerTest {

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
    private SafeMessageConsumer safeMessageConsumer;

    private InvoiceListener invoiceListener;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        safeMessageConsumer = new SafeMessageConsumer(1000L, 1.5, sleepService);
        invoiceListener = new InvoiceListener(handlerManager, safeMessageConsumer, eventParser);
    }

    @Test
    public void listenEmptyChanges() {
        MachineEvent message = new MachineEvent();
        SourceEvent sourceEvent = new SourceEvent();
        Event event = new Event();
        EventPayload payload = new EventPayload();
        payload.setInvoiceChanges(new ArrayList<>());
        event.setPayload(payload);
        sourceEvent.setProcessingEvent(event);
        Mockito.when(eventParser.parseEvent(message)).thenReturn(sourceEvent);

        invoiceListener.listen(message, ack);

        Mockito.verify(ack, Mockito.times(1)).acknowledge();
    }

    @Test
    public void listenWithTwoRetryAckAndOneMoreRetry() {
        MachineEvent message = new MachineEvent();
        SourceEvent sourceEvent = new SourceEvent();
        Event event = new Event();
        EventPayload payload = new EventPayload();
        payload.setInvoiceChanges(new ArrayList<>());
        event.setPayload(payload);
        sourceEvent.setProcessingEvent(event);
        Mockito.when(eventParser.parseEvent(message)).thenThrow(new ParseException());

        invoiceListener.listen(message, ack);
        Mockito.verify(ack, Mockito.times(0)).acknowledge();
        Mockito.verify(sleepService, Mockito.times(1)).safeSleep(1000L);

        invoiceListener.listen(message, ack);
        Mockito.verify(sleepService, Mockito.times(1)).safeSleep(1500L);

        Mockito.reset(eventParser);
        Mockito.when(eventParser.parseEvent(message)).thenReturn(sourceEvent);
        invoiceListener.listen(message, ack);
        Mockito.verify(ack, Mockito.times(1)).acknowledge();

        Mockito.reset(eventParser);
        Mockito.reset(sleepService);
        Mockito.when(eventParser.parseEvent(message)).thenThrow(new ParseException());
        invoiceListener.listen(message, ack);
        Mockito.verify(sleepService, Mockito.times(1)).safeSleep(1000L);
    }

    @Test
    public void listenChanges() {
        MachineEvent message = new MachineEvent();
        SourceEvent sourceEvent = new SourceEvent();
        Event event = new Event();
        EventPayload payload = new EventPayload();
        ArrayList<InvoiceChange> invoiceChanges = new ArrayList<>();
        invoiceChanges.add(new InvoiceChange());
        payload.setInvoiceChanges(invoiceChanges);
        event.setPayload(payload);
        sourceEvent.setProcessingEvent(event);
        Mockito.when(eventParser.parseEvent(message)).thenReturn(sourceEvent);
        Mockito.when(handler.handle(any(), any())).thenReturn(processor);
        Mockito.when(handlerManager.getHandler(any())).thenReturn(handler);

        invoiceListener.listen(message, ack);

        Mockito.verify(processor, Mockito.times(1)).execute();
        Mockito.verify(ack, Mockito.times(1)).acknowledge();
    }

}