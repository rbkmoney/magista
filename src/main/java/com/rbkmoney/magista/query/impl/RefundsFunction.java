package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatRefund;
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

public class RefundsFunction extends PagedBaseFunction<Map.Entry<Long, StatRefund>, StatResponse> implements CompositeQuery<Map.Entry<Long, StatRefund>, StatResponse> {

    public static final String FUNC_NAME = "refunds";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    private RefundsFunction(Object descriptor, QueryParameters params, String continuationToken, CompositeQuery subquery) {
        super(descriptor, params, FUNC_NAME, continuationToken);
        this.subquery = subquery;
    }

    @Override
    public QueryResult<Map.Entry<Long, StatRefund>, StatResponse> execute(QueryContext context) throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<Map.Entry<Long, StatRefund>, StatResponse> execute(QueryContext context, List<QueryResult> collectedResults) throws QueryExecutionException {
        QueryResult<Map.Entry<Long, StatRefund>, List<Map.Entry<Long, StatRefund>>> refundsResult = (QueryResult<Map.Entry<Long, StatRefund>, List<Map.Entry<Long, StatRefund>>>) collectedResults.get(0);

        return new BaseQueryResult<>(
                () -> refundsResult.getDataStream(),
                () -> {
                    StatResponseData statResponseData = StatResponseData.refunds(refundsResult.getDataStream()
                            .map(refundResponse -> refundResponse.getValue())
                            .collect(Collectors.toList()));

                    StatResponse statResponse = new StatResponse(statResponseData);
                    List<Map.Entry<Long, StatRefund>> refunds = refundsResult.getCollectedStream();
                    if (!refunds.isEmpty() && getQueryParameters().getSize() == refunds.size()) {
                        final String createdAt = refunds.get(refunds.size() - 1).getValue().getCreatedAt();
                        final String token = getContext(context)
                                .getTokenGenService()
                                .generateToken(getQueryParameters(), TypeUtil.stringToLocalDateTime(createdAt));
                        statResponse.setContinuationToken(token);
                    }
                    return statResponse;
                });
    }

    @Override
    public RefundsFunction.RefundsParameters getQueryParameters() {
        return (RefundsFunction.RefundsParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new RefundsFunction.RefundsParameters(parameters, derivedParameters);
    }

    @Override
    public List<Query> getChildQueries() {
        return subquery.getChildQueries();
    }

    @Override
    public boolean isParallel() {
        return subquery.isParallel();
    }

    public static class RefundsParameters extends PagedBaseParameters {

        public RefundsParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public RefundsParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public String getInvoiceId() {
            return getStringParameter(INVOICE_ID_PARAM, false);
        }

        public String getPaymentId() {
            return getStringParameter(PAYMENT_ID_PARAM, false);
        }

        public String getRefundId() {
            return getStringParameter(REFUND_ID_PARAM, false);
        }

        public String getRefundStatus() {
            return getStringParameter(REFUND_STATUS_PARAM, false);
        }

        public TemporalAccessor getFromTime() {
            return getTimeParameter(FROM_TIME_PARAM, false);
        }

        public TemporalAccessor getToTime() {
            return getTimeParameter(TO_TIME_PARAM, false);
        }

    }

    public static class RefundsValidator extends PagedBaseValidator {

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            RefundsFunction.RefundsParameters refundsParameters = super.checkParamsType(parameters, RefundsFunction.RefundsParameters.class);

            validateTimePeriod(refundsParameters.getFromTime(), refundsParameters.getToTime());
        }
    }

    public static class RefundsParser extends AbstractQueryParser {
        private RefundsValidator validator = new RefundsValidator();

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            Map<String, Object> funcSource = (Map) source.get(FUNC_NAME);
            RefundsParameters parameters = getValidatedParameters(funcSource, parent, RefundsParameters::new, validator);

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

    public static class RefundsBuilder extends AbstractQueryBuilder {
        private RefundsValidator validator = new RefundsValidator();

        @Override
        public Query buildQuery(QueryContext queryContext, List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(RefundsParser.getMainDescriptor(), queryParts, queryPart -> createQuery(queryPart, continuationToken));
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
            return createRefundsFunction(queryPart.getDescriptor(), queryPart.getParameters(), continuationToken, compositeQuery);
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(RefundsParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }
    }

    private static RefundsFunction createRefundsFunction(Object descriptor, QueryParameters queryParameters, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        RefundsFunction refundsFunction = new RefundsFunction(descriptor, queryParameters, continuationToken, subquery);
        subquery.setParentQuery(refundsFunction);
        return refundsFunction;
    }

    private static class GetDataFunction extends PagedBaseFunction<Map.Entry<Long, StatRefund>, Collection<Map.Entry<Long, StatRefund>>> {
        private static final String FUNC_NAME = RefundsFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params, String continuationToken) {
            super(descriptor, params, FUNC_NAME, continuationToken);
        }

        @Override
        public QueryResult<Map.Entry<Long, StatRefund>, Collection<Map.Entry<Long, StatRefund>>> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            RefundsParameters parameters = new RefundsParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Collection<Map.Entry<Long, StatRefund>> result = functionContext.getSearchDao().getRefunds(
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
