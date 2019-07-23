package com.rbkmoney.magista.serde;

import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;

public class MachineEventDeserializer extends AbstractThriftDeserializer<SinkEvent> {

    @Override
    public SinkEvent deserialize(String topic, byte[] data) {
        return this.deserialize(data, new SinkEvent());
    }

}
