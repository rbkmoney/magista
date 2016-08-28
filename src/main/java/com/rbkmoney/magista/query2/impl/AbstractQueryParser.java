package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.QueryParameters;
import com.rbkmoney.magista.query2.QueryValidator;
import com.rbkmoney.magista.query2.parser.QueryParser;
import com.rbkmoney.magista.query2.parser.QueryParserException;
import com.rbkmoney.magista.query2.parser.QueryPart;

import java.util.Map;

/**
 * Created by vpankrashkin on 25.08.16.
 */
public abstract class AbstractQueryParser implements QueryParser<Map<String, Object>> {

    protected QueryParameters getParameters(QueryPart queryPart) {
        return queryPart == null ? null : queryPart.getParameters();
    }

    protected <T extends QueryParameters> T getValidatedParameters(Map<String, Object> source, QueryPart parent, QueryParameters.QueryParametersRef<T> parametersRef, QueryValidator validator) throws QueryParserException {
        try {
            T parameters = parametersRef.newInstance(source, getParameters(parent));
            validator.validateParameters(parameters);
            return parameters;
        } catch (IllegalArgumentException e) {
            throw new QueryParserException(e.getMessage(), e);
        }
    }
}
