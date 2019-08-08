package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.EnrichedStatInvoice;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnrichedPaymentsFunction extends PagedBaseFunction<Map.Entry<Long, EnrichedStatInvoice>, StatResponse> implements CompositeQuery<Map.Entry<Long, EnrichedStatInvoice>, StatResponse> {

    public static final String FUNC_NAME = "enriched_payments";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    private EnrichedPaymentsFunction(Object descriptor, QueryParameters params, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        super(descriptor, params, FUNC_NAME, continuationToken);
        this.subquery = subquery;
    }


    @Override
    public QueryResult<Map.Entry<Long, EnrichedStatInvoice>, StatResponse> execute(QueryContext context) throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<Map.Entry<Long, EnrichedStatInvoice>, StatResponse> execute(QueryContext context, List<QueryResult> collectedResults) throws QueryExecutionException {
        QueryResult<Map.Entry<Long, EnrichedStatInvoice>, List<Map.Entry<Long, EnrichedStatInvoice>>> enrichedInvoicesResult = (QueryResult<Map.Entry<Long, EnrichedStatInvoice>, List<Map.Entry<Long, EnrichedStatInvoice>>>) collectedResults.get(0);

        return new BaseQueryResult<>(
                enrichedInvoicesResult::getDataStream,
                () -> {
                    StatResponseData statResponseData = StatResponseData.enriched_invoices(
                            enrichedInvoicesResult.getDataStream()
                                    .map(Map.Entry::getValue)
                                    .collect(Collectors.toList())
                    );
                    StatResponse statResponse = new StatResponse(statResponseData);

                    List<Map.Entry<Long, EnrichedStatInvoice>> enrichedInvoicesStats = enrichedInvoicesResult.getCollectedStream();
                    if (!enrichedInvoicesResult.getCollectedStream().isEmpty() && getQueryParameters().getSize() == enrichedInvoicesStats.size()) {
                        String createdAt = enrichedInvoicesStats
                                .stream()
                                .map(Map.Entry::getValue)
                                .flatMap(enrichedStatInvoice -> enrichedStatInvoice.getPayments().stream())
                                .min(Comparator.comparing(o -> TypeUtil.stringToLocalDateTime(o.getCreatedAt())))
                                .get()
                                .getCreatedAt();
                        String token = getContext(context)
                                .getTokenGenService()
                                .generateToken(getQueryParameters(), TypeUtil.stringToLocalDateTime(createdAt));
                        statResponse.setContinuationToken(token);
                    }
                    return statResponse;
                });
    }

    @Override
    public PaymentsFunction.PaymentsParameters getQueryParameters() {
        return (PaymentsFunction.PaymentsParameters) super.getQueryParameters();
    }


    @Override
    protected PaymentsFunction.PaymentsParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new PaymentsFunction.PaymentsParameters(parameters, derivedParameters);
    }

    @Override
    public List<Query> getChildQueries() {
        return subquery.getChildQueries();
    }

    @Override
    public boolean isParallel() {
        return subquery.isParallel();
    }

    public static class EnrichedPaymentsBuilder extends AbstractQueryBuilder {
        private PaymentsFunction.PaymentsValidator validator = new PaymentsFunction.PaymentsValidator();

        @Override
        public Query buildQuery(QueryContext queryContext, List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(EnrichedPaymentsParser.getMainDescriptor(), queryParts, queryPart -> createQuery(queryPart, continuationToken));
            validator.validateQuery(resultQuery, queryContext);
            return resultQuery;
        }

        private CompositeQuery createQuery(QueryPart queryPart, String continuationToken) {
            List<Query> queries = Collections.singletonList(
                    new GetDataFunction(queryPart.getDescriptor() + ":" + GetDataFunction.FUNC_NAME, queryPart.getParameters(), continuationToken)
            );
            CompositeQuery<QueryResult, List<QueryResult>> compositeQuery = createCompositeQuery(
                    queryPart.getDescriptor(),
                    getParameters(queryPart.getParent()),
                    queries
            );
            return createEnrichedPaymentsFunction(queryPart.getDescriptor(), queryPart.getParameters(), continuationToken, compositeQuery);
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(EnrichedPaymentsParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }
    }

    public static class EnrichedPaymentsParser extends AbstractQueryParser {
        private PaymentsFunction.PaymentsValidator validator = new PaymentsFunction.PaymentsValidator();

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            Map<String, Object> funcSource = (Map) source.get(FUNC_NAME);
            PaymentsFunction.PaymentsParameters parameters = getValidatedParameters(funcSource, parent, PaymentsFunction.PaymentsParameters::new, validator);

            return Stream
                    .of(new QueryPart(FUNC_NAME, parameters, parent))
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

    private static EnrichedPaymentsFunction createEnrichedPaymentsFunction(Object descriptor, QueryParameters queryParameters, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        EnrichedPaymentsFunction paymentsFunction = new EnrichedPaymentsFunction(descriptor, queryParameters, continuationToken, subquery);
        subquery.setParentQuery(paymentsFunction);
        return paymentsFunction;
    }

    private static class GetDataFunction extends PagedBaseFunction<Map.Entry<Long, EnrichedStatInvoice>, Collection<Map.Entry<Long, EnrichedStatInvoice>>> {
        private static final String FUNC_NAME = EnrichedPaymentsFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params, String continuationToken) {
            super(descriptor, params, FUNC_NAME, continuationToken);
        }

        @Override
        public QueryResult<Map.Entry<Long, EnrichedStatInvoice>, Collection<Map.Entry<Long, EnrichedStatInvoice>>> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            PaymentsFunction.PaymentsParameters parameters = new PaymentsFunction.PaymentsParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Collection<Map.Entry<Long, EnrichedStatInvoice>> result = functionContext.getSearchDao().getEnrichedInvoices(
                        parameters,
                        Optional.ofNullable(TypeUtil.toLocalDateTime(parameters.getFromTime())),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(parameters.getToTime())),
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

