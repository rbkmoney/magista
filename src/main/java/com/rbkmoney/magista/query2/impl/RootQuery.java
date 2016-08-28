package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.BaseQuery;
import com.rbkmoney.magista.query2.BaseQueryValidator;
import com.rbkmoney.magista.query2.QueryParameters;
import com.rbkmoney.magista.query2.parser.QueryParserException;
import com.rbkmoney.magista.query2.parser.QueryPart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.magista.query2.impl.Parameters.QUERY_PARAMETER;

/**
 * Created by vpankrashkin on 23.08.16.
 */
public class RootQuery extends BaseQuery {
    public RootQuery(QueryParameters params) {
        super(params);
    }

    public static class RootParameters extends QueryParameters {
        public RootParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public RootParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public Map getQuery() {
            Object obj = getParameter(QUERY_PARAMETER, false);
            return obj instanceof Map ? (Map) obj : null;
        }

        public QueryParameters getRestParameters() {
            return removeParameters(QueryParameters::new, QUERY_PARAMETER);
        }
    }

    public static class RootValidator extends BaseQueryValidator {
        public static final String DEFAULT_ERR_MSG_STRING = "invalid or not found";

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            RootParameters rootParameters = super.checkParamsType(parameters, RootParameters.class);
            if (rootParameters.getQuery() == null) {
                checkParamsResult(true, QUERY_PARAMETER, DEFAULT_ERR_MSG_STRING);
            }
        }
    }

    public static class RootParser extends AbstractQueryParser {
        private RootValidator validator = new RootValidator();

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            RootParameters parameters = getValidatedParameters(source, parent, RootParameters::new, validator);

            return Stream.of(
                    new QueryPart(getMainDescriptor(), new QueryParameters(parameters.getQuery(), parameters), parent),
                    new QueryPart(QueryPart.DEFAULT_DESCRIPTOR, parameters.getRestParameters(), parent))
                    .collect(Collectors.toList());
        }

        @Override
        public boolean apply(Map source, QueryPart parent) {
            return parent == null;
        }

        public static String getMainDescriptor() {
            return QUERY_PARAMETER;
        }
    }
}
