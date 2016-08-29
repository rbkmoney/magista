package com.rbkmoney.magista.query2.impl;


import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.query2.*;
import com.rbkmoney.magista.query2.builder.QueryBuilder;
import com.rbkmoney.magista.query2.builder.QueryBuilderException;
import com.rbkmoney.magista.query2.impl.builder.AbstractQueryBuilder;
import com.rbkmoney.magista.query2.impl.parser.AbstractQueryParser;
import com.rbkmoney.magista.query2.parser.QueryParserException;
import com.rbkmoney.magista.query2.parser.QueryPart;

import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.magista.query2.impl.Parameters.*;

/**
 * Created by vpankrashkin on 08.08.16.
 */
public abstract class StatBaseFunction extends ScopedBaseFunction<Map<String, String>, StatResponse> {

    public StatBaseFunction(Object descriptor, QueryParameters params, String name) {
        super(descriptor, params, name);
    }

    @Override
    public StatBaseParameters getQueryParameters() {
        return (StatBaseParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new StatBaseParameters(parameters, derivedParameters);
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

    public abstract static class StatBaseBuilder extends AbstractQueryBuilder {
        private StatBaseValidator validator = new StatBaseValidator();

        @Override
        public Query buildQuery(List<QueryPart> queryParts, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(getDescriptor(queryParts), queryParts, queryPart -> createQuery(queryPart));
            validator.validateQuery(resultQuery);
            return resultQuery;
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(getDescriptor(queryParts), queryParts).findFirst().isPresent();
        }

        protected abstract Query createQuery(QueryPart queryPart);

        protected abstract Object getDescriptor(List<QueryPart> queryParts);

    }


}
