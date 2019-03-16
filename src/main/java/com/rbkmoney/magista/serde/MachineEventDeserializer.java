package com.rbkmoney.magista.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

@Slf4j
public class MachineEventDeserializer implements Deserializer<MachineEvent> {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public MachineEvent deserialize(String topic, byte[] data) {
        MachineEvent ruleTemplate = null;
        try {
            ruleTemplate = om.readValue(data, MachineEvent.class);
        } catch (Exception e) {
            log.error("Error when deserialize ruleTemplate data: {} ", data, e);
        }
        return ruleTemplate;
    }

    @Override
    public void close() {

    }

}
