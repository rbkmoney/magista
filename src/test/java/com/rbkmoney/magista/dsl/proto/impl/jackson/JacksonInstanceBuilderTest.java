package com.rbkmoney.magista.dsl.proto.impl.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.magista.dsl.instance.DSLInstance;
import com.rbkmoney.magista.dsl.instance.PathPoint;
import com.rbkmoney.magista.dsl.proto.query.RootFunctionDef;
import org.junit.Test;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by vpankrashkin on 15.05.17.
 */
public class JacksonInstanceBuilderTest {
    private static String readJson(String name) {
        InputStream io = Thread.currentThread().getContextClassLoader().getResourceAsStream("data/json/"+name+".json");
        try {
            return StreamUtils.copyToString(io, Charset.forName("UTF8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test() throws IOException {
        String json = readJson("payment_req_1");
        ObjectMapper mapper = new ObjectMapper();
        JacksonInstanceBuilder builder = new JacksonInstanceBuilder();
        JsonNode jnode = mapper.readTree(json);
        PathPoint<JsonNode> node = new PathPoint<>(RootFunctionDef.INSTANCE, jnode, null);
        DSLInstance instance = builder.build(jnode, new ArrayList(){{add(node);}}, null);
    }
}
