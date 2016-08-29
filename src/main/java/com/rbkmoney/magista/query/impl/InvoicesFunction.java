package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.damsel.merch_stat.StatInvoice;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.damsel.merch_stat.StatResponseData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.query2.*;
import com.rbkmoney.magista.query2.builder.QueryBuilder;
import com.rbkmoney.magista.query2.builder.QueryBuilderException;
import com.rbkmoney.magista.query2.impl.builder.AbstractQueryBuilder;
import com.rbkmoney.magista.query2.impl.parser.AbstractQueryParser;
import com.rbkmoney.magista.query2.parser.QueryParserException;
import com.rbkmoney.magista.query2.parser.QueryPart;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.magista.query2.impl.Parameters.*;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public class InvoicesFunction extends PagedBaseFunction<Invoice, StatResponse> implements CompositeQuery<Invoice, StatResponse> {

    public static final String FUNC_NAME = "invoices";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    private InvoicesFunction(Object descriptor, QueryParameters params, CompositeQuery subquery) {
        super(descriptor, params, FUNC_NAME);
        this.subquery = subquery;
    }

    @Override
    public QueryResult<Invoice, StatResponse> execute(QueryContext context) throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<Invoice, StatResponse> execute(QueryContext context, List<QueryResult> collectedResults) throws QueryExecutionException {
        if (collectedResults.size() != 2) {
            throw new QueryExecutionException("Wrong query results count:"+collectedResults.size());
        }

        QueryResult<Invoice, List<Invoice>> invoicesResult = (QueryResult<Invoice, List<Invoice>>) collectedResults.get(0);
        QueryResult<Integer, Integer> countResult = (QueryResult<Integer, Integer>) collectedResults.get(1);

        return new BaseQueryResult<>(
                () ->invoicesResult.getDataStream(),
                () -> {
                    StatResponseData statResponseData = StatResponseData.invoices(invoicesResult.getDataStream().map(invoice -> new StatInvoice(invoice.getModel())).collect(Collectors.toList()));
                    StatResponse statResponse = new StatResponse(statResponseData);
                    statResponse.setTotalCount(countResult.getCollectedStream());
                    return statResponse;
                });
    }


        @Override
    public InvoicesParameters getQueryParameters() {
        return (InvoicesParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new InvoicesParameters(parameters, derivedParameters);
    }

    @Override
    public List<Query> getChildQueries() {
        return subquery.getChildQueries();
    }

    @Override
    public boolean isParallel() {
        return subquery.isParallel();
    }

    public static class InvoicesParameters extends PagedBaseParameters {

        public InvoicesParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public InvoicesParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public String getInvoiceId() {
            return getStringParameter(INVOICE_ID_PARAM, false);
        }

        public String getInvoiceStatus() {
            return getStringParameter(INVOICE_STATUS_PARAM, false);
        }

        public TemporalAccessor getFromTime() {
            return getTimeParameter(FROM_TIME_PARAM, false);
        }

        public TemporalAccessor getToTime() {
            return getTimeParameter(TO_TIME_PARAM, false);
        }

    }

    public static class InvoicesValidator extends PagedBaseValidator {

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            InvoicesParameters paymentsParameters = super.checkParamsType(parameters, InvoicesParameters.class);

            validateTimePeriod(paymentsParameters.getFromTime(), paymentsParameters.getToTime());
        }
    }

    public static class InvoicesParser extends AbstractQueryParser {
        private InvoicesValidator validator = new InvoicesValidator();

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            Map<String, Object> funcSource = (Map) source.get(FUNC_NAME);
            InvoicesParameters parameters = getValidatedParameters(funcSource, parent, InvoicesParameters::new, validator);

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


    public static class InvoicesBuilder extends AbstractQueryBuilder {
        private InvoicesValidator validator = new InvoicesValidator();

        @Override
        public Query buildQuery(List<QueryPart> queryParts, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(InvoicesParser.getMainDescriptor(), queryParts, queryPart -> createQuery(queryPart));
            validator.validateQuery(resultQuery);
            return resultQuery;
        }

        private CompositeQuery createQuery(QueryPart queryPart) {
            List<Query> queries = Arrays.asList(
                    new GetDataFunction(queryPart.getDescriptor() + ":"+GetDataFunction.FUNC_NAME, queryPart.getParameters()),
                    new GetCountFunction(queryPart.getDescriptor() + ":" +GetCountFunction.FUNC_NAME, queryPart.getParameters())
            );
            CompositeQuery<QueryResult, List<QueryResult>> compositeQuery = createCompositeQuery(
                    queryPart.getDescriptor(),
                    getParameters(queryPart.getParent()),
                    queries
                    );
            return createInvoicesFunction(queryPart.getDescriptor(), queryPart.getParameters(), compositeQuery);
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(InvoicesParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }

    }

    private static InvoicesFunction createInvoicesFunction(Object descriptor, QueryParameters queryParameters, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        InvoicesFunction invoicesFunction = new InvoicesFunction(descriptor, queryParameters, subquery);
        subquery.setParentQuery(invoicesFunction);
        return invoicesFunction;
    }

    private static class GetDataFunction extends PagedBaseFunction {
        private static final String FUNC_NAME = PaymentsFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params) {
            super(descriptor, params, FUNC_NAME);
        }

        @Override
        public QueryResult<Invoice, Collection<Invoice>> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            InvoicesParameters parameters = new InvoicesParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Pair<Integer, Collection<Invoice>> result = functionContext.getDao().getInvoices(
                        parameters.getMerchantId(),
                        parameters.getShopId(),
                        Optional.ofNullable(parameters.getInvoiceId()),
                        Optional.ofNullable(parameters.getInvoiceStatus()),
                        Optional.ofNullable(Instant.from(parameters.getFromTime())),
                        Optional.ofNullable(Instant.from(parameters.getToTime())),
                        Optional.ofNullable(parameters.getSize()),
                        Optional.ofNullable(parameters.getFrom())
                );
                return new BaseQueryResult<>(() -> result.getValue().stream(), () -> result.getValue());
            } catch (DaoException e) {
                throw new QueryExecutionException(e);
            }
        }
    }

    private static class GetCountFunction extends ScopedBaseFunction<Integer, Integer> {
        private static final String FUNC_NAME = PaymentsFunction.FUNC_NAME + "_count";

        public GetCountFunction(Object descriptor, QueryParameters params) {
            super(descriptor, params, FUNC_NAME);
        }

        @Override
        public QueryResult<Integer, Integer> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            InvoicesParameters parameters = new InvoicesParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Pair<Integer, Collection<Invoice>> result = functionContext.getDao().getInvoices(
                        parameters.getMerchantId(),
                        parameters.getShopId(),
                        Optional.ofNullable(parameters.getInvoiceId()),
                        Optional.ofNullable(parameters.getInvoiceStatus()),
                        Optional.ofNullable(Instant.from(parameters.getFromTime())),
                        Optional.ofNullable(Instant.from(parameters.getToTime())),
                        Optional.ofNullable(parameters.getSize()),
                        Optional.ofNullable(parameters.getFrom())
                );
                return new BaseQueryResult<>(() -> Stream.of(result.getKey()), () -> result.getKey());
            } catch (DaoException e) {
                throw new QueryExecutionException(e);
            }
        }
    }
}
