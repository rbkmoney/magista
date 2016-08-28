package com.rbkmoney.magista.query2.impl;


import com.rbkmoney.magista.query2.Query;
import com.rbkmoney.magista.query2.QueryParameters;
import com.rbkmoney.magista.query2.parser.QueryParserException;
import com.rbkmoney.magista.query2.parser.QueryPart;

import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.magista.query2.impl.Parameters.FROM_TIME_PARAM;
import static com.rbkmoney.magista.query2.impl.Parameters.SPLIT_INTERVAL_PARAM;
import static com.rbkmoney.magista.query2.impl.Parameters.TO_TIME_PARAM;

/**
 * Created by vpankrashkin on 08.08.16.
 */
public abstract class StatBaseFunction extends ScopedBaseFunction {

    private final StatBaseParameters parameters;

    public StatBaseFunction(QueryParameters params, Query parentQuery, String name) {
        super(params, parentQuery, name);
        this.parameters = new StatBaseParameters(params, extractParameters(parentQuery));
    }

    @Override
    public StatBaseParameters getQueryParameters() {
        return parameters;
    }

    public static class StatBaseParameters extends ScopedBaseParameters {
        public StatBaseParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public StatBaseParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public TemporalAccessor getFromTime() {
            return getTimeParameter(FROM_TIME_PARAM, false);
        }

        public TemporalAccessor getToTime() {
            return getTimeParameter(TO_TIME_PARAM, false);
        }

        public Integer getSplitInterval() {
            return getIntParameter(SPLIT_INTERVAL_PARAM, false);
        }
    }

    public static class StatBaseValidator extends ScopedBaseValidator {
        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            StatBaseParameters statBaseParameters = super.checkParamsType(parameters, StatBaseParameters.class);

            Integer splitInterval = statBaseParameters.getSplitInterval();
            if (splitInterval == null || splitInterval <= 0) {
                checkParamsResult(true, SPLIT_INTERVAL_PARAM, RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
            }

            TemporalAccessor from = statBaseParameters.getFromTime();
            if (from == null) {
                checkParamsResult(true, FROM_TIME_PARAM, RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
            }
            TemporalAccessor to = statBaseParameters.getToTime();
            if (to == null) {
                checkParamsResult(true, TO_TIME_PARAM, RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
            }
            validateTimePeriod(from, to);
        }
    }

    public static class StatBaseParser extends AbstractQueryParser {
        private StatBaseValidator validator = new StatBaseValidator();
        private final String funcName;

        public StatBaseParser(String funcName) {
            this.funcName = funcName;
        }

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            Map<String, Object> funcSource = (Map) source.get(funcName);
            StatBaseParameters parameters = getValidatedParameters(funcSource, parent, StatBaseParameters::new, validator);

            return Stream.of(
                    new QueryPart(funcName, parameters, parent)
            )
                    .collect(Collectors.toList());
        }

        @Override
        public boolean apply(Map<String, Object> source, QueryPart parent) {
            return parent != null
                    && RootQuery.RootParser.getMainDescriptor().equals(parent.getDescriptor())
                    && (source.get(funcName) instanceof Map);
        }
    }

}
