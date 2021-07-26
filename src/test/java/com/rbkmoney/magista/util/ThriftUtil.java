package com.rbkmoney.magista.util;

import com.rbkmoney.geck.serializer.kit.mock.FieldHandler;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.machinegun.msgpack.Value;
import lombok.SneakyThrows;
import org.apache.thrift.TBase;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.time.Instant;
import java.util.Map;

public class ThriftUtil {

    private static final Map.Entry<FieldHandler, String[]> timeFields = Map.entry(
            structHandler -> structHandler.value(Instant.now().toString()),
            new String[]{"created_at", "at", "due"}
    );

    @SneakyThrows
    public static <T extends TBase<?, ?>> T fillThriftObject(T data, Class<T> type) {
        MockTBaseProcessor mockTBaseProcessor = new MockTBaseProcessor(MockMode.REQUIRED_ONLY, 25, 1);
        mockTBaseProcessor.addFieldHandler(timeFields.getKey(), timeFields.getValue());
        return mockTBaseProcessor.process(data, new TBaseHandler<>(type));
    }

    @SneakyThrows
    public static Value toByteArray(TBase<?, ?> data) {
        return Value.bin(
                new TSerializer(new TBinaryProtocol.Factory())
                        .serialize(data));
    }
}
