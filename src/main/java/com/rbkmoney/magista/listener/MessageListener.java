package com.rbkmoney.magista.listener;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import org.springframework.kafka.support.Acknowledgment;

public interface MessageListener {

    void handle(MachineEvent message, Acknowledgment ack);

}
