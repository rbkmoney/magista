package com.rbkmoney.magista.serde;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TCompactProtocol;

import java.util.Map;

@Slf4j
public class MachineEventSerializer implements Serializer<MachineEvent> {

    private final TSerializer serializer = new TSerializer(new TCompactProtocol.Factory());

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, MachineEvent data) {
        byte[] retVal = null;
        try {
            retVal = serializer.serialize(data);
        } catch (Exception e) {
            log.error("Error when serialize RuleTemplate data: {} ", data, e);
        }
        return retVal;
    }

    @Override
    public void close() {

    }

}
