package com.rbkmoney.magista.converter;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BinaryConverterImpl implements BinaryConverter<EventPayload> {

    private final TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());

    @Override
    public EventPayload convert(byte[] bin, Class<EventPayload> clazz) {
        EventPayload event = new EventPayload();
        try {
            deserializer.deserialize(event, bin);
        } catch (TException e) {
            log.error("BinaryConverterImpl e: ", e);
        }
        return event;
    }
}
