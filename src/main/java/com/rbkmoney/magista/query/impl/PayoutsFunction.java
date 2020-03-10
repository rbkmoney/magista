package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatPayout;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.damsel.merch_stat.StatResponseData;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.query.*;
import com.rbkmoney.magista.query.builder.QueryBuilder;
import com.rbkmoney.magista.query.builder.QueryBuilderException;
import com.rbkmoney.magista.query.impl.builder.AbstractQueryBuilder;
import com.rbkmoney.magista.query.impl.parser.AbstractQueryParser;
import com.rbkmoney.magista.query.parser.QueryParserException;
import com.rbkmoney.magista.query.parser.QueryPart;

import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.magista.query.impl.Parameters.*;

public class PayoutsFunction extends PagedBaseFunction<Map.Entry<Long, StatPayout>, StatResponse> implements CompositeQuery<Map.Entry<Long, StatPayout>, StatResponse> {

    public static final String FUNC_NAME = "payouts";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    private PayoutsFunction(Object descriptor, QueryParameters params, String continuationToken, CompositeQuery subquery) {
        super(descriptor, params, FUNC_NAME, continuationToken);
        this.subquery = subquery;
    }

    @Override
    public QueryResult<Map.Entry<Long, StatPayout>, StatResponse> execute(QueryContext context) throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<Map.Entry<Long, StatPayout>, StatResponse> execute(QueryContext context, List<QueryResult> collectedResults) throws QueryExecutionException {
        QueryResult<Map.Entry<Long, StatPayout>, List<Map.Entry<Long, StatPayout>>> payoutsResult = (QueryResult<Map.Entry<Long, StatPayout>, List<Map.Entry<Long, StatPayout>>>) collectedResults.get(0);

        return new BaseQueryResult<>(
                () -> payoutsResult.getDataStream(),
                () -> {
                    StatResponseData statResponseData = StatResponseData.payouts(payoutsResult.getDataStream()
                            .map(payoutResponse -> payoutResponse.getValue())
                            .collect(Collectors.toList()));

                    StatResponse statResponse = new StatResponse(statResponseData);
                    List<Map.Entry<Long, StatPayout>> payouts = payoutsResult.getCollectedStream();
                    if (!payouts.isEmpty() && getQueryParameters().getSize() == payouts.size()) {
                        final String createdAt = payouts.get(payouts.size() - 1).getValue().getCreatedAt();
                        final String token = getContext(context)
                                .getTokenGenService()
                                .generateToken(getQueryParameters(), TypeUtil.stringToLocalDateTime(createdAt));
                        statResponse.setContinuationToken(token);
                    }
                    return statResponse;
                });
    }

    @Override
    public PayoutsParameters getQueryParameters() {
        return (PayoutsParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new PayoutsParameters(parameters, derivedParameters);
    }

    @Override
    public List<Query> getChildQueries() {
        return subquery.getChildQueries();
    }

    @Override
    public boolean isParallel() {
        return subquery.isParallel();
    }

    public static class PayoutsParameters extends PagedBaseParameters {

        public PayoutsParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public PayoutsParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public String getPayoutId() {
            return getStringParameter(PAYOUT_ID_PARAM, false);
        }

        public String getPayoutStatus() {
            return getStringParameter(PAYOUT_STATUS_PARAM, false);
        }

        public List<String> getPayoutStatuses() {
            return getArrayParameter(PAYOUT_STATUSES_PARAM, false);
        }

        public String getPayoutType() {
            return getStringParameter(PAYOUT_TYPE_PARAM, false);
        }

        public TemporalAccessor getFromTime() {
            return getTimeParameter(FROM_TIME_PARAM, false);
        }

        public TemporalAccessor getToTime() {
            return getTimeParameter(TO_TIME_PARAM, false);
        }

    }

    public static class PayoutsValidator extends PagedBaseValidator {

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            PayoutsParameters payoutsParameters = super.checkParamsType(parameters, PayoutsParameters.class);

            validateTimePeriod(payoutsParameters.getFromTime(), payoutsParameters.getToTime());
        }
    }

    public static class PayoutsParser extends AbstractQueryParser {
        private PayoutsValidator validator = new PayoutsValidator();

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            Map<String, Object> funcSource = (Map) source.get(FUNC_NAME);
            PayoutsParameters parameters = getValidatedParameters(funcSource, parent, PayoutsParameters::new, validator);

            return Stream.of(
                    new QueryPart(FUNC_NAME, parameters, parent)
            )
                    .collect(Collectors.toList());
        }

        @Override
        public boolean apply(Map source, QueryPart parent) {
            return parent != null
                    && RootQuery.RootParser.getMainDescriptor().equals(parent.getDescriptor())
                    && (source.get(FUNC_NAME) instanceof Map);
        }

        public static String getMainDescriptor() {
            return FUNC_NAME;
        }
    }

    public static class PayoutsBuilder extends AbstractQueryBuilder {
        private PayoutsValidator validator = new PayoutsValidator();

        @Override
        public Query buildQuery(QueryContext queryContext, List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(PayoutsParser.getMainDescriptor(), queryParts, queryPart -> createQuery(queryPart, continuationToken));
            validator.validateQuery(resultQuery, queryContext);
            return resultQuery;
        }

        private CompositeQuery createQuery(QueryPart queryPart, String continuationToken) {
            List<Query> queries = Arrays.asList(
                    new GetDataFunction(queryPart.getDescriptor() + ":" + GetDataFunction.FUNC_NAME, queryPart.getParameters(), continuationToken)
            );
            CompositeQuery<QueryResult, List<QueryResult>> compositeQuery = createCompositeQuery(
                    queryPart.getDescriptor(),
                    getParameters(queryPart.getParent()),
                    queries
            );
            return createPayoutsFunction(queryPart.getDescriptor(), queryPart.getParameters(), continuationToken, compositeQuery);
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(PayoutsParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }
    }

    private static PayoutsFunction createPayoutsFunction(Object descriptor, QueryParameters queryParameters, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        PayoutsFunction payoutsFunction = new PayoutsFunction(descriptor, queryParameters, continuationToken, subquery);
        subquery.setParentQuery(payoutsFunction);
        return payoutsFunction;
    }

    private static class GetDataFunction extends PagedBaseFunction<Map.Entry<Long, StatPayout>, Collection<Map.Entry<Long, StatPayout>>> {
        private static final String FUNC_NAME = PayoutsFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params, String continuationToken) {
            super(descriptor, params, FUNC_NAME, continuationToken);
        }

        @Override
        public QueryResult<Map.Entry<Long, StatPayout>, Collection<Map.Entry<Long, StatPayout>>> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            PayoutsParameters parameters = new PayoutsParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Collection<Map.Entry<Long, StatPayout>> result = functionContext.getSearchDao().getPayouts(
                        parameters,
                        TypeUtil.toLocalDateTime(parameters.getFromTime()),
                        TypeUtil.toLocalDateTime(parameters.getToTime()),
                        getTime(functionContext),
                        parameters.getSize()
                );
                return new BaseQueryResult<>(result::stream, () -> result);
            } catch (DaoException e) {
                throw new QueryExecutionException(e);
            }
        }
    }

}
