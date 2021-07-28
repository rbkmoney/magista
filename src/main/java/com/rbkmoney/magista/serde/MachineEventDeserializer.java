package com.rbkmoney.magista.serde;

import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.protocol.TBinaryProtocol;

@Slf4j
public class MachineEventDeserializer extends AbstractThriftDeserializer<MachineEvent> {

    private static final ThreadLocal<TDeserializer> deserializerThreadLocal =
            ThreadLocal.withInitial(() -> new TDeserializer(new TBinaryProtocol.Factory()));

    @Override
    public MachineEvent deserialize(String topic, byte[] data) {
        SinkEvent machineEvent = new SinkEvent();
        try {
            deserializerThreadLocal.get().deserialize(machineEvent, data);
        } catch (Exception e) {
            log.error("Error when deserialize machine event data: {} ", data, e);
        }
        return machineEvent.getEvent();
    }

    @Override
    public void close() {
        deserializerThreadLocal.remove();
    }
}
