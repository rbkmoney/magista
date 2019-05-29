package com.rbkmoney.magista.listener;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

public interface MessageListener {

    void handle(List<MachineEvent> messages, Acknowledgment ack);

}
