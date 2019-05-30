package com.rbkmoney.magista.listener;

import com.rbkmoney.damsel.payment_processing.Event;
import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.converter.SourceEventParser;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.event.handler.impl.InvoiceBatchHandler;
import com.rbkmoney.magista.exception.ParseException;
import com.rbkmoney.magista.service.HandlerManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.Acknowledgment;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class InvoiceListenerTest {

    @Mock
    private HandlerManager handlerManager;
    @Mock
    private InvoiceBatchHandler invoiceHandler;
    @Mock
    private Processor processor;
    @Mock
    private SourceEventParser eventParser;
    @Mock
    private Acknowledgment ack;

    private InvoiceListener invoiceListener;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        invoiceListener = new InvoiceListener(handlerManager, eventParser);
    }

    @Test
    public void listenEmptyChanges() {
        MachineEvent message = new MachineEvent();
        List<MachineEvent> messages = List.of(message);
        Event event = new Event();
        EventPayload payload = new EventPayload();
        payload.setInvoiceChanges(new ArrayList<>());
        event.setPayload(payload);
        Mockito.when(eventParser.parseEvent(message)).thenReturn(payload);

        invoiceListener.handle(messages, ack);

        Mockito.verify(processor, Mockito.times(0)).execute();
    }

    @Test(expected = ParseException.class)
    public void listenEmptyException() {
        MachineEvent message = new MachineEvent();
        List<MachineEvent> messages = List.of(message);
        Mockito.when(eventParser.parseEvent(message)).thenThrow(new ParseException());
        invoiceListener.handle(messages, ack);
    }

    @Test
    public void listenChanges() {
        MachineEvent message = new MachineEvent();
        List<MachineEvent> messages = List.of(message);
        Event event = new Event();
        EventPayload payload = new EventPayload();
        ArrayList<InvoiceChange> invoiceChanges = new ArrayList<>();
        invoiceChanges.add(new InvoiceChange());
        payload.setInvoiceChanges(invoiceChanges);
        event.setPayload(payload);
        Mockito.when(eventParser.parseEvent(message)).thenReturn(payload);
        Mockito.when(invoiceHandler.handle(any())).thenReturn(processor);
        Mockito.when(handlerManager.getHandler(any())).thenReturn(invoiceHandler);

        invoiceListener.handle(messages, ack);

        Mockito.verify(invoiceHandler, Mockito.times(1)).handle(any());
        Mockito.verify(processor, Mockito.times(1)).execute();
    }

}