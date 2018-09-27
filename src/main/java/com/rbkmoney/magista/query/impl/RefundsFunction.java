package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.damsel.merch_stat.StatResponseData;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.dao.ConditionParameterSource;
import com.rbkmoney.magista.domain.enums.RefundStatus;
import com.rbkmoney.magista.domain.tables.pojos.Refund;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.query.*;
import com.rbkmoney.magista.query.builder.QueryBuilder;
import com.rbkmoney.magista.query.builder.QueryBuilderException;
import com.rbkmoney.magista.query.impl.builder.AbstractQueryBuilder;
import com.rbkmoney.magista.query.impl.parser.AbstractQueryParser;
import com.rbkmoney.magista.query.parser.QueryParserException;
import com.rbkmoney.magista.query.parser.QueryPart;
import com.rbkmoney.magista.util.DamselUtil;

import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.geck.common.util.TypeUtil.toEnumField;
import static com.rbkmoney.magista.domain.tables.Refund.REFUND;
import static com.rbkmoney.magista.query.impl.Parameters.*;
import static org.jooq.Comparator.EQUALS;

public class RefundsFunction extends PagedBaseFunction<Refund, StatResponse> implements CompositeQuery<Refund, StatResponse> {

    public static final String FUNC_NAME = "refunds";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    private RefundsFunction(Object descriptor, QueryParameters params, String continuationToken, CompositeQuery subquery) {
        super(descriptor, params, FUNC_NAME, continuationToken);
        this.subquery = subquery;
    }

    @Override
    public QueryResult<Refund, StatResponse> execute(QueryContext context) throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<Refund, StatResponse> execute(QueryContext context, List<QueryResult> collectedResults) throws QueryExecutionException {
        if (collectedResults.size() != 2) {
            throw new QueryExecutionException("Wrong query results count:" + collectedResults.size());
        }

        QueryResult<Refund, List<Refund>> refundsResult = (QueryResult<Refund, List<Refund>>) collectedResults.get(0);
        QueryResult<Integer, Integer> countResult = (QueryResult<Integer, Integer>) collectedResults.get(1);

        return new BaseQueryResult<>(
                () -> refundsResult.getDataStream(),
                () -> {
                    StatResponseData statResponseData = StatResponseData.refunds(refundsResult.getDataStream()
                            .map(refundEvent -> DamselUtil.toStatRefund(refundEvent))
                            .collect(Collectors.toList()));

                    StatResponse statResponse = new StatResponse(statResponseData);
                    statResponse.setTotalCount(countResult.getCollectedStream());
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
        public Query buildQuery(List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(RefundsParser.getMainDescriptor(), queryParts, queryPart -> createQuery(queryPart, continuationToken));
            validator.validateQuery(resultQuery);
            return resultQuery;
        }

        private CompositeQuery createQuery(QueryPart queryPart, String continuationToken) {
            List<Query> queries = Arrays.asList(
                    new GetDataFunction(queryPart.getDescriptor() + ":" + GetDataFunction.FUNC_NAME, queryPart.getParameters(), continuationToken),
                    new GetCountFunction(queryPart.getDescriptor() + ":" + GetCountFunction.FUNC_NAME, queryPart.getParameters())
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

    private static class GetDataFunction extends PagedBaseFunction<Refund, Collection<Refund>> {
        private static final String FUNC_NAME = RefundsFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params, String continuationToken) {
            super(descriptor, params, FUNC_NAME, continuationToken);
        }

        @Override
        public QueryResult<Refund, Collection<Refund>> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            RefundsParameters parameters = new RefundsParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Collection<Refund> result = functionContext.getDao().getRefunds(
                        Optional.ofNullable(parameters.getMerchantId()),
                        Optional.ofNullable(parameters.getShopId()),
                        buildRefundConditionParameterSource(parameters),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(parameters.getFromTime())),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(parameters.getToTime())),
                        Optional.ofNullable(parameters.getFrom()),
                        parameters.getSize()
                );
                return new BaseQueryResult<>(() -> result.stream(), () -> result);
            } catch (DaoException e) {
                throw new QueryExecutionException(e);
            }
        }
    }

    private static class GetCountFunction extends ScopedBaseFunction<Integer, Integer> {
        private static final String FUNC_NAME = RefundsFunction.FUNC_NAME + "_count";

        public GetCountFunction(Object descriptor, QueryParameters params) {
            super(descriptor, params, FUNC_NAME);
        }

        @Override
        public QueryResult<Integer, Integer> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            RefundsParameters parameters = new RefundsParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Integer result = functionContext.getDao().getRefundsCount(
                        Optional.ofNullable(parameters.getMerchantId()),
                        Optional.ofNullable(parameters.getShopId()),
                        buildRefundConditionParameterSource(parameters),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(parameters.getFromTime())),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(parameters.getToTime()))
                );
                return new BaseQueryResult<>(() -> Stream.of(result), () -> result);
            } catch (DaoException e) {
                throw new QueryExecutionException(e);
            }
        }
    }

    public static ConditionParameterSource buildRefundConditionParameterSource(RefundsParameters parameters) {
        return new ConditionParameterSource()
                .addValue(REFUND.PARTY_ID, parameters.getMerchantId(), EQUALS)
                .addValue(REFUND.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                .addValue(REFUND.INVOICE_ID, parameters.getInvoiceId(), EQUALS)
                .addValue(REFUND.PAYMENT_ID, parameters.getPaymentId(), EQUALS)
                .addValue(REFUND.REFUND_ID, parameters.getRefundId(), EQUALS)
                .addValue(REFUND.REFUND_STATUS,
                        toEnumField(parameters.getRefundStatus(), RefundStatus.class),
                        EQUALS);
    }

}
