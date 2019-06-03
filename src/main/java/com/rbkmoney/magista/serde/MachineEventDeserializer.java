package com.rbkmoney.magista.serde;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.Map;

@Slf4j
public class MachineEventDeserializer implements Deserializer<MachineEvent> {

    ThreadLocal<TDeserializer> tDeserializerThreadLocal = ThreadLocal.withInitial(() -> new TDeserializer(new TBinaryProtocol.Factory()));

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public MachineEvent deserialize(String topic, byte[] data) {
        SinkEvent machineEvent = new SinkEvent();
        try {
            tDeserializerThreadLocal.get().deserialize(machineEvent, data);
        } catch (Exception e) {
            log.error("Error when deserialize machine event data: {} ", data, e);
        }
        return machineEvent.getEvent();
    }

    @Override
    public void close() {

    }

}
