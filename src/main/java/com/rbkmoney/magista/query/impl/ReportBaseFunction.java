package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.magista.query.*;
import com.rbkmoney.magista.query.builder.QueryBuilder;
import com.rbkmoney.magista.query.builder.QueryBuilderException;
import com.rbkmoney.magista.query.impl.builder.AbstractQueryBuilder;
import com.rbkmoney.magista.query.impl.parser.AbstractQueryParser;
import com.rbkmoney.magista.query.parser.QueryParserException;
import com.rbkmoney.magista.query.parser.QueryPart;

import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.magista.query.impl.Parameters.*;

/**
 * Created by tolkonepiu on 21/12/2016.
 */
public abstract class ReportBaseFunction extends BaseFunction<Map<String, String>, StatResponse> {

    public ReportBaseFunction(Object descriptor, QueryParameters params, String name) {
        super(descriptor, params, name);
    }

    @Override
    public ReportBaseParameters getQueryParameters() {
        return (ReportBaseParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new ReportBaseParameters(parameters, derivedParameters);
    }

    protected FunctionQueryContext getContext(QueryContext context) {
        return this.getContext(context, FunctionQueryContext.class);
    }

    public static class ReportBaseParameters extends QueryParameters {

        public ReportBaseParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public ReportBaseParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public TemporalAccessor getFromTime() {
            return getTimeParameter(FROM_TIME_PARAM, false);
        }

        public TemporalAccessor getToTime() {
            return getTimeParameter(TO_TIME_PARAM, false);
        }

        public List<Integer> getGetWithoutShopCategoryIds() {
            return getArrayParameter(WITHOUT_SHOP_CATEGORY_IDS_PARAM, false);
        }
    }

    public static class ReportBaseValidator extends BaseQueryValidator {
        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            ReportBaseParameters reportBaseParameters = super.checkParamsType(parameters, ReportBaseParameters.class);

            TemporalAccessor from = reportBaseParameters.getFromTime();
            if (from == null) {
                checkParamsResult(true, FROM_TIME_PARAM, RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
            }
            TemporalAccessor to = reportBaseParameters.getToTime();
            if (to == null) {
                checkParamsResult(true, TO_TIME_PARAM, RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
            }
            validateTimePeriod(from, to);
        }
    }

    public static class ReportBaseParser extends AbstractQueryParser {
        private ReportBaseValidator validator = new ReportBaseValidator();
        private final String funcName;

        public ReportBaseParser(String funcName) {
            this.funcName = funcName;
        }

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            Map<String, Object> funcSource = (Map) source.get(funcName);
            ReportBaseParameters parameters = getValidatedParameters(funcSource, parent, ReportBaseParameters::new, validator);

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

    public abstract static class ReportBaseBuilder extends AbstractQueryBuilder {
        private ReportBaseValidator validator = new ReportBaseValidator();

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
