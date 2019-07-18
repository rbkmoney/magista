package com.rbkmoney.magista.serde;

import com.rbkmoney.damsel.withdrawals.processing.SinkEvent;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;

public class MachineEventDeserializer extends AbstractThriftDeserializer<SinkEvent> {

    @Override
    public SinkEvent deserialize(String topic, byte[] data) {
        return this.deserialize(data, new SinkEvent());
    }

}
