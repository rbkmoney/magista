package com.rbkmoney.magista.query.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.magista.query.QueryParser;
import com.rbkmoney.magista.query.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class JsonQueryParser implements QueryParser<String> {
    private final Map<String, Function<Map, BaseFunction>> functions;

    public JsonQueryParser() {
        this(new HashMap<String, Function<Map, BaseFunction>>() {
            {
                this.put(InvoicesFunction.FUNC_NAME, map -> new InvoicesFunction(map));
                this.put(PaymentsFunction.FUNC_NAME, map -> new PaymentsFunction(map));
            }
        });
    }

    public JsonQueryParser(Map<String, Function<Map, BaseFunction>> functions) {
        this.functions = new HashMap<>(functions);
    }

    @Override
    public Query parse(String source) throws Exception {
        Map jsonMap;

        jsonMap = getMapper().readValue(source, HashMap.class);
         Object queryDef = jsonMap.get("query");

        if (!(queryDef instanceof Map)) {
            throw new IllegalArgumentException("Query Definition not found");
        }

        Map queryDefMap = (Map) queryDef;
        Set keys = queryDefMap.keySet();
        int expectedKeysCount = 1;
        if (keys.size() != expectedKeysCount) {
            throw new IllegalArgumentException("Query Definition has wrong properties count. Expected: "+expectedKeysCount + ", actual: "+ keys.size());
        }

        String funcName = String.valueOf(keys.iterator().next());
        Object funcParams = queryDefMap.get(funcName);
        if (!(funcParams instanceof Map)) {
            throw new IllegalArgumentException("Query Parameters not found");
        }

        BaseFunction query = getFunction(funcName, (Map) funcParams);
        if (query == null) {
            throw new IllegalArgumentException("Function not found: "+ funcName);
        }
        query.validateParameters();
        return query;

    }

    protected ObjectMapper getMapper() {
        return new ObjectMapper();
    }

    private BaseFunction getFunction(String name, Map params) {
        Function<Map, BaseFunction> function = functions.get(name);
        return function != null ? function.apply(params) : null;
    }


}
