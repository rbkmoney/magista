package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.query.*;
import com.rbkmoney.magista.query.builder.QueryBuilder;
import com.rbkmoney.magista.query.builder.QueryBuilderException;
import com.rbkmoney.magista.query.impl.builder.AbstractQueryBuilder;
import com.rbkmoney.magista.query.impl.parser.AbstractQueryParser;
import com.rbkmoney.magista.query.parser.QueryParserException;
import com.rbkmoney.magista.query.parser.QueryPart;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.http.util.TextUtils.isBlank;

/**
 * merchant OKKO-specific, in general shouldn't be touched.
 *
 * @author n.pospolita
 */
public class EnrichedRefundsFunction extends PagedBaseFunction<Map.Entry<Long, EnrichedStatInvoice>, StatResponse>
        implements CompositeQuery<Map.Entry<Long, EnrichedStatInvoice>, StatResponse> {

    public static final String FUNC_NAME = "enriched_refunds";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    private EnrichedRefundsFunction(Object descriptor, QueryParameters params, String continuationToken,
                                    CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        super(descriptor, params, FUNC_NAME, continuationToken);
        this.subquery = subquery;
    }

    private static EnrichedRefundsFunction createEnrichedRefundsFunction(
            Object descriptor,
            QueryParameters queryParameters,
            String continuationToken,
            CompositeQuery<QueryResult, List<QueryResult>> subquery
    ) {
        EnrichedRefundsFunction refundsFunction =
                new EnrichedRefundsFunction(descriptor, queryParameters, continuationToken, subquery);
        subquery.setParentQuery(refundsFunction);
        return refundsFunction;
    }

    @Override
    public QueryResult<Map.Entry<Long, EnrichedStatInvoice>, StatResponse> execute(QueryContext context)
            throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<Map.Entry<Long, EnrichedStatInvoice>, StatResponse> execute(QueryContext context,
                                                                                   List<QueryResult> collectedResults)
            throws QueryExecutionException {
        QueryResult<Map.Entry<Long, EnrichedStatInvoice>, List<Map.Entry<Long, EnrichedStatInvoice>>>
                enrichedInvoicesResult =
                (QueryResult<Map.Entry<Long, EnrichedStatInvoice>, List<Map.Entry<Long, EnrichedStatInvoice>>>)
                        collectedResults.get(0);

        return new BaseQueryResult<>(
                enrichedInvoicesResult::getDataStream,
                () -> {
                    StatResponseData statResponseData = StatResponseData.enriched_invoices(
                            enrichedInvoicesResult.getDataStream()
                                    .map(Map.Entry::getValue)
                                    .collect(Collectors.toList())
                    );
                    StatResponse statResponse = new StatResponse(statResponseData);

                    List<Map.Entry<Long, EnrichedStatInvoice>> enrichedInvoicesStats =
                            enrichedInvoicesResult.getCollectedStream();
                    if (!enrichedInvoicesResult.getCollectedStream().isEmpty()
                            && getQueryParameters().getSize() == enrichedInvoicesStats.size()) {
                        final String eventOccuredAt = enrichedInvoicesStats
                                .stream()
                                .map(Map.Entry::getValue)
                                .flatMap(enrichedStatInvoice -> enrichedStatInvoice.getRefunds().stream())
                                .map(o -> Map.entry(this.extractEventOccuredAtTime(o), o))
                                .min(Map.Entry.comparingByKey())
                                .get()
                                .getKey();
                        final String token = getContext(context)
                                .getTokenGenService()
                                .generateToken(getQueryParameters(), TypeUtil.stringToLocalDateTime(eventOccuredAt));
                        statResponse.setContinuationToken(token);
                    }
                    return statResponse;
                });
    }

    private String extractEventOccuredAtTime(StatRefund o) {
        InvoicePaymentRefundStatus status = o.getStatus();
        String eventOccuredAt = null;
        if (status.isSetFailed()) {
            eventOccuredAt = status.getFailed().getAt();
        } else if (status.isSetPending()) {
            // no eventOccuredAt field
        } else if (status.isSetSucceeded()) {
            eventOccuredAt = status.getSucceeded().getAt();
        }
        if (!isBlank(eventOccuredAt)) {
            return eventOccuredAt;
        } else {
            return o.getCreatedAt(); //we can't return null, return CreatedAt instead
        }
    }

    @Override
    public RefundsFunction.RefundsParameters getQueryParameters() {
        return (RefundsFunction.RefundsParameters) super.getQueryParameters();
    }

    @Override
    protected RefundsFunction.RefundsParameters createQueryParameters(QueryParameters parameters,
                                                                      QueryParameters derivedParameters) {
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

    public static class EnrichedRefundsParser extends AbstractQueryParser {
        private final RefundsFunction.RefundsValidator validator = new RefundsFunction.RefundsValidator();

        public static String getMainDescriptor() {
            return FUNC_NAME;
        }

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            Map<String, Object> funcSource = (Map) source.get(FUNC_NAME);
            RefundsFunction.RefundsParameters parameters =
                    getValidatedParameters(funcSource, parent, RefundsFunction.RefundsParameters::new, validator);

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
    }

    public static class EnrichedRefundsBuilder extends AbstractQueryBuilder {
        private final RefundsFunction.RefundsValidator validator = new RefundsFunction.RefundsValidator();

        @Override
        public Query buildQuery(QueryContext queryContext, List<QueryPart> queryParts, String continuationToken,
                                QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(EnrichedRefundsParser.getMainDescriptor(), queryParts,
                    queryPart -> createQuery(queryPart, continuationToken));
            validator.validateQuery(resultQuery, queryContext);
            return resultQuery;
        }

        private CompositeQuery createQuery(QueryPart queryPart, String continuationToken) {
            List<Query> queries = List.of(
                    new GetDataFunction(queryPart.getDescriptor() + ":" + GetDataFunction.FUNC_NAME,
                            queryPart.getParameters(), continuationToken)
            );
            CompositeQuery<QueryResult, List<QueryResult>> compositeQuery = createCompositeQuery(
                    queryPart.getDescriptor(),
                    getParameters(queryPart.getParent()),
                    queries
            );
            return createEnrichedRefundsFunction(queryPart.getDescriptor(), queryPart.getParameters(),
                    continuationToken, compositeQuery);
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(EnrichedRefundsParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }
    }

    private static class GetDataFunction extends
            PagedBaseFunction<Map.Entry<Long, EnrichedStatInvoice>, Collection<Map.Entry<Long, EnrichedStatInvoice>>> {
        private static final String FUNC_NAME = EnrichedRefundsFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params, String continuationToken) {
            super(descriptor, params, FUNC_NAME, continuationToken);
        }

        @Override
        public QueryResult<Map.Entry<Long, EnrichedStatInvoice>, Collection<Map.Entry<Long, EnrichedStatInvoice>>>
                execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            RefundsFunction.RefundsParameters parameters = new RefundsFunction.RefundsParameters(getQueryParameters(),
                    getQueryParameters().getDerivedParameters());
            try {
                Collection<Map.Entry<Long, EnrichedStatInvoice>> result =
                        functionContext.getSearchDao().getEnrichedInvoices(
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
