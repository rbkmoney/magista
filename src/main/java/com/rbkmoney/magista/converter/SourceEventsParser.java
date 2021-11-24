package com.rbkmoney.magista.converter;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.magista.exception.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TUnion;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SourceEventsParser {

    private final BinaryConverter<EventPayload> converter;

    public List<EventPayload> parseEvents(MachineEvent message) {
        try {
            if (message.getData().isSetBin()) {
                var bin = message.getData().getBin();
                return List.of(converter.convert(bin, EventPayload.class));
            } else if (message.getData().isSetArr()) {
                var errorValues = message.getData().getArr().stream()
                        .filter(value -> !value.isSetBin())
                        .map(TUnion::toString)
                        .collect(Collectors.joining(", "));
                if (StringUtils.hasText(errorValues)) {
                    throw new ParseException(String.format("Cant parse type of messages=%s", errorValues));
                } else {
                    return message.getData().getArr().stream()
                            .filter(Value::isSetBin)
                            .map(Value::getBin)
                            .map(bytes -> converter.convert(bytes, EventPayload.class))
                            .collect(Collectors.toList());
                }
            } else {
                throw new ParseException(String.format("Cant parse type of message=%s", message.getData()));
            }
        } catch (Exception e) {
            log.error("Exception when parse message e: ", e);
            throw new ParseException();
        }
    }
}
