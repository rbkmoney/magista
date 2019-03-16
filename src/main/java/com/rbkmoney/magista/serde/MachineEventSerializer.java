package com.rbkmoney.magista.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Slf4j
public class MachineEventSerializer implements Serializer<MachineEvent> {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, MachineEvent data) {
        byte[] retVal = null;
        try {
            retVal = om.writeValueAsString(data).getBytes();
        } catch (Exception e) {
            log.error("Error when serialize RuleTemplate data: {} ", data, e);
        }
        return retVal;
    }

    @Override
    public void close() {

    }

}
