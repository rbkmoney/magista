package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatPayment;
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
import com.rbkmoney.magista.util.TokenUtil;

import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.magista.query.impl.Parameters.*;

public class PaymentsForReportFunction extends PagedBaseFunction<Map.Entry<Long, StatPayment>, StatResponse> implements CompositeQuery<Map.Entry<Long, StatPayment>, StatResponse> {

    public static final String FUNC_NAME = "payments_for_report";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    private PaymentsForReportFunction(Object descriptor, QueryParameters params, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        super(descriptor, params, FUNC_NAME, continuationToken);
        this.subquery = subquery;
    }

    @Override
    public QueryResult<Map.Entry<Long, StatPayment>, StatResponse> execute(QueryContext context) throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<Map.Entry<Long, StatPayment>, StatResponse> execute(QueryContext context, List<QueryResult> collectedResults) throws QueryExecutionException {
        QueryResult<Map.Entry<Long, StatPayment>, List<Map.Entry<Long, StatPayment>>> paymentsForReportResult = (QueryResult<Map.Entry<Long, StatPayment>, List<Map.Entry<Long, StatPayment>>>) collectedResults.get(0);

        return new BaseQueryResult<>(
                () -> paymentsForReportResult.getDataStream(),
                () -> {
                    StatResponseData statResponseData = StatResponseData.payments(paymentsForReportResult.getDataStream()
                            .map(paymentResponse -> paymentResponse.getValue()).collect(Collectors.toList()));
                    StatResponse statResponse = new StatResponse(statResponseData);
                    List<Map.Entry<Long, StatPayment>> paymentStats = paymentsForReportResult.getCollectedStream();
                    if (!paymentsForReportResult.getCollectedStream().isEmpty() && getQueryParameters().getSize() == paymentStats.size()) {
                        String createdAt = paymentStats.get(paymentStats.size() - 1).getValue().getStatus().getCaptured().getAt();
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
    public PaymentsForReportFunction.PaymentsForReportParameters getQueryParameters() {
        return (PaymentsForReportFunction.PaymentsForReportParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new PaymentsForReportFunction.PaymentsForReportParameters(parameters, derivedParameters);
    }

    @Override
    public List<Query> getChildQueries() {
        return subquery.getChildQueries();
    }

    @Override
    public boolean isParallel() {
        return subquery.isParallel();
    }

    public static class PaymentsForReportParameters extends PagedBaseParameters {

        public PaymentsForReportParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public PaymentsForReportParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public String getInvoiceId() {
            return getStringParameter(INVOICE_ID_PARAM, false);
        }

        public String getPaymentId() {
            return getStringParameter(PAYMENT_ID_PARAM, false);
        }

        public TemporalAccessor getFromTime() {
            return getTimeParameter(FROM_TIME_PARAM, false);
        }

        public TemporalAccessor getToTime() {
            return getTimeParameter(TO_TIME_PARAM, false);
        }

    }

    public static class PaymentsForReportValidator extends PagedBaseValidator {

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            PaymentsForReportParameters paymentsParameters = super.checkParamsType(parameters, PaymentsForReportParameters.class);

            if (paymentsParameters.getMerchantId() == null) {
                checkParamsResult(true, MERCHANT_ID_PARAM, RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
            }

            validateTimePeriod(paymentsParameters.getFromTime(), paymentsParameters.getToTime());
        }
    }

    public static class PaymentsForReportParser extends AbstractQueryParser {
        private PaymentsForReportValidator validator = new PaymentsForReportValidator();

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            Map<String, Object> funcSource = (Map) source.get(FUNC_NAME);
            PaymentsForReportParameters parameters = getValidatedParameters(funcSource, parent, PaymentsForReportParameters::new, validator);

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

    public static class PaymentsForReportBuilder extends AbstractQueryBuilder {
        private PaymentsForReportValidator validator = new PaymentsForReportValidator();

        @Override
        public Query buildQuery(QueryContext queryContext, List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(PaymentsForReportParser.getMainDescriptor(), queryParts, queryPart -> createQuery(queryPart, continuationToken));
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
            return createPaymentsForReportFunction(queryPart.getDescriptor(), queryPart.getParameters(), continuationToken, compositeQuery);
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(PaymentsForReportParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }
    }

    private static PaymentsForReportFunction createPaymentsForReportFunction(Object descriptor, QueryParameters queryParameters, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        PaymentsForReportFunction paymentsForReportFunction = new PaymentsForReportFunction(descriptor, queryParameters, continuationToken, subquery);
        subquery.setParentQuery(paymentsForReportFunction);
        return paymentsForReportFunction;
    }

    private static class GetDataFunction extends PagedBaseFunction<Map.Entry<Long, StatPayment>, Collection<Map.Entry<Long, StatPayment>>> {
        private static final String FUNC_NAME = PaymentsFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params, String continuationToken) {
            super(descriptor, params, FUNC_NAME, continuationToken);
        }

        @Override
        public QueryResult<Map.Entry<Long, StatPayment>, Collection<Map.Entry<Long, StatPayment>>> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            PaymentsForReportParameters parameters = new PaymentsForReportParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Collection<Map.Entry<Long, StatPayment>> result = functionContext.getReportDao().getPaymentsForReport(
                        parameters.getMerchantId(),
                        Optional.ofNullable(parameters.getShopId()),
                        Optional.ofNullable(parameters.getInvoiceId()),
                        Optional.ofNullable(parameters.getPaymentId()),
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
