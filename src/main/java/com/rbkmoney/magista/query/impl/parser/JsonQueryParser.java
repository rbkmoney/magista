package com.rbkmoney.magista.query2.impl.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.magista.query2.parser.QueryParser;
import com.rbkmoney.magista.query2.parser.QueryParserException;
import com.rbkmoney.magista.query2.parser.QueryPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by vpankrashkin on 09.08.16.
 */
public class JsonQueryParser implements QueryParser<String> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final QueryParser<Map<String, Object>> queryPartParser;

    public JsonQueryParser() {
        this(new QueryParserImpl());
    }

    public JsonQueryParser(QueryParser<Map<String, Object>> queryPartParser) {
        this.queryPartParser = queryPartParser;
    }


    public List<QueryPart> parseQuery(String source) throws QueryParserException {
        return parseQuery(source, null);
    }

    @Override
    public List<QueryPart> parseQuery(String source, QueryPart parent) throws QueryParserException {
        try {
            log.info("Received json string request: {}", source);
            Map<String, Object> jsonMap = parseJsonMap(source);
            return queryPartParser.parseQuery(jsonMap, parent);

        } catch (IOException e) {
            throw new QueryParserException("Failed to parse received json request: "+e.getMessage(), e);
        }
    }


    @Override
    public boolean apply(String source, QueryPart parent) {
        return true;
    }

    protected ObjectMapper getMapper() {
        return new ObjectMapper();
    }

    protected Map<String, Object> parseJsonMap(String data) throws IOException {
        return getMapper().readerFor(new TypeReference<Map<String, Object>>() {}).readValue(data);
    }
}
