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

public class RefundsForReportFunction extends PagedBaseFunction<Map.Entry<Long, StatRefund>, StatResponse> implements CompositeQuery<Map.Entry<Long, StatRefund>, StatResponse> {

    public static final String FUNC_NAME = "refunds_for_report";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    public RefundsForReportFunction(Object descriptor, QueryParameters params, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
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
        QueryResult<Map.Entry<Long, StatRefund>, List<Map.Entry<Long, StatRefund>>> refundsForReportResult = (QueryResult<Map.Entry<Long, StatRefund>, List<Map.Entry<Long, StatRefund>>>) collectedResults.get(0);

        return new BaseQueryResult<>(
                () -> refundsForReportResult.getDataStream(),
                () -> {
                    StatResponseData statResponseData = StatResponseData.refunds(refundsForReportResult.getDataStream()
                            .map(refundResponse -> refundResponse.getValue()).collect(Collectors.toList()));
                    StatResponse statResponse = new StatResponse(statResponseData);
                    List<Map.Entry<Long, StatRefund>> refundStats = refundsForReportResult.getCollectedStream();
                    if (!refundsForReportResult.getCollectedStream().isEmpty() && getQueryParameters().getSize() == refundStats.size()) {
                        String createdAt = refundStats.get(refundStats.size() - 1).getValue().getStatus().getSucceeded().getAt();
                        String token = getContext(context)
                                .getTokenGenService()
                                .generateToken(getQueryParameters(), TypeUtil.stringToLocalDateTime(createdAt));
                        statResponse.setContinuationToken(token);
                    }
                    return statResponse;
                }
        );
    }

    @Override
    public RefundsForReportParameters getQueryParameters() {
        return (RefundsForReportParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new RefundsForReportParameters(parameters, derivedParameters);
    }

    @Override
    public List<Query> getChildQueries() {
        return subquery.getChildQueries();
    }

    @Override
    public boolean isParallel() {
        return subquery.isParallel();
    }

    public static class RefundsForReportParameters extends PagedBaseParameters {

        public RefundsForReportParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public RefundsForReportParameters(QueryParameters parameters, QueryParameters derivedParameters) {
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

        public TemporalAccessor getFromTime() {
            return getTimeParameter(FROM_TIME_PARAM, false);
        }

        public TemporalAccessor getToTime() {
            return getTimeParameter(TO_TIME_PARAM, false);
        }

    }

    public static class RefundsForReportValidator extends PagedBaseValidator {

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            RefundsForReportParameters refundsParameters = super.checkParamsType(parameters, RefundsForReportParameters.class);

            if (refundsParameters.getMerchantId() == null) {
                checkParamsResult(true, MERCHANT_ID_PARAM, RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
            }

            if (refundsParameters.getShopId() == null) {
                checkParamsResult(true, SHOP_ID_PARAM, RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
            }

            validateTimePeriod(refundsParameters.getFromTime(), refundsParameters.getToTime());
        }
    }

    public static class RefundsForReportParser extends AbstractQueryParser {
        private RefundsForReportValidator validator = new RefundsForReportValidator();

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            Map<String, Object> funcSource = (Map) source.get(FUNC_NAME);
            RefundsForReportParameters parameters = getValidatedParameters(funcSource, parent, RefundsForReportParameters::new, validator);

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

    public static class RefundsForReportBuilder extends AbstractQueryBuilder {
        private RefundsForReportValidator validator = new RefundsForReportValidator();

        @Override
        public Query buildQuery(QueryContext queryContext, List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(RefundsForReportParser.getMainDescriptor(), queryParts, queryPart -> createQuery(queryPart, continuationToken));
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
            return createRefundsForReportFunction(queryPart.getDescriptor(), queryPart.getParameters(), continuationToken, compositeQuery);
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(RefundsForReportParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }
    }

    private static RefundsForReportFunction createRefundsForReportFunction(Object descriptor, QueryParameters queryParameters, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        RefundsForReportFunction refundsForReportFunction = new RefundsForReportFunction(descriptor, queryParameters, continuationToken, subquery);
        subquery.setParentQuery(refundsForReportFunction);
        return refundsForReportFunction;
    }

    private static class GetDataFunction extends PagedBaseFunction<Map.Entry<Long, StatRefund>, Collection<Map.Entry<Long, StatRefund>>> {
        private static final String FUNC_NAME = RefundsFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params, String continuationToken) {
            super(descriptor, params, FUNC_NAME, continuationToken);
        }

        @Override
        public QueryResult<Map.Entry<Long, StatRefund>, Collection<Map.Entry<Long, StatRefund>>> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            RefundsForReportParameters parameters = new RefundsForReportParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Collection<Map.Entry<Long, StatRefund>> result = functionContext.getReportDao().getRefundsForReport(
                        parameters.getMerchantId(),
                        parameters.getShopId(),
                        Optional.ofNullable(parameters.getInvoiceId()),
                        Optional.ofNullable(parameters.getPaymentId()),
                        Optional.ofNullable(parameters.getRefundId()),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(parameters.getFromTime())),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(parameters.getToTime())),
                        getTime(functionContext),
                        parameters.getSize()
                );
                return new BaseQueryResult<>(() -> result.stream(), () -> result);
            } catch (DaoException e) {
                throw new QueryExecutionException(e);
            }
        }
    }
}
