package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.InvoiceCart;
import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.query.*;
import com.rbkmoney.magista.query.builder.QueryBuilder;
import com.rbkmoney.magista.query.builder.QueryBuilderException;
import com.rbkmoney.magista.query.impl.builder.AbstractQueryBuilder;
import com.rbkmoney.magista.query.impl.parser.AbstractQueryParser;
import com.rbkmoney.magista.query.parser.QueryParserException;
import com.rbkmoney.magista.query.parser.QueryPart;
import com.rbkmoney.magista.util.DamselUtil;
import org.apache.http.entity.ContentType;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.magista.query.impl.Parameters.*;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public class InvoicesFunction extends PagedBaseFunction<InvoiceEventStat, StatResponse> implements CompositeQuery<InvoiceEventStat, StatResponse> {

    public static final String FUNC_NAME = "invoices";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    private InvoicesFunction(Object descriptor, QueryParameters params, CompositeQuery subquery) {
        super(descriptor, params, FUNC_NAME);
        this.subquery = subquery;
    }

    @Override
    public QueryResult<InvoiceEventStat, StatResponse> execute(QueryContext context) throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<InvoiceEventStat, StatResponse> execute(QueryContext context, List<QueryResult> collectedResults) throws QueryExecutionException {
        if (collectedResults.size() != 2) {
            throw new QueryExecutionException("Wrong query results count:" + collectedResults.size());
        }

        QueryResult<InvoiceEventStat, List<InvoiceEventStat>> invoicesResult = (QueryResult<InvoiceEventStat, List<InvoiceEventStat>>) collectedResults.get(0);
        QueryResult<Integer, Integer> countResult = (QueryResult<Integer, Integer>) collectedResults.get(1);

        return new BaseQueryResult<>(
                () -> invoicesResult.getDataStream(),
                () -> {
                    StatResponseData statResponseData = StatResponseData.invoices(invoicesResult.getDataStream().map(invoice -> toStatInvoice(invoice)).collect(Collectors.toList()));
                    StatResponse statResponse = new StatResponse(statResponseData);
                    statResponse.setTotalCount(countResult.getCollectedStream());
                    return statResponse;
                });
    }

    private StatInvoice toStatInvoice(InvoiceEventStat invoiceEventStat) {
        StatInvoice statInvoice = new StatInvoice();
        statInvoice.setId(invoiceEventStat.getInvoiceId());
        statInvoice.setOwnerId(invoiceEventStat.getPartyId());
        statInvoice.setShopId(invoiceEventStat.getPartyShopId());
        statInvoice.setCreatedAt(TypeUtil.temporalToString(
                invoiceEventStat.getInvoiceCreatedAt().toInstant(ZoneOffset.UTC)
        ));

        statInvoice.setStatus(toStatInvoiceStatus(
                com.rbkmoney.damsel.domain.InvoiceStatus._Fields.findByName(
                        invoiceEventStat.getInvoiceStatus().getLiteral()
                ), invoiceEventStat.getInvoiceStatusDetails()));

        statInvoice.setProduct(invoiceEventStat.getInvoiceProduct());
        statInvoice.setDescription(invoiceEventStat.getInvoiceDescription());

        statInvoice.setDue(
                TypeUtil.temporalToString(invoiceEventStat.getInvoiceDue())
        );
        statInvoice.setAmount(invoiceEventStat.getInvoiceAmount());
        statInvoice.setCurrencySymbolicCode(invoiceEventStat.getInvoiceCurrencyCode());

        if (Objects.nonNull(invoiceEventStat.getInvoiceCart())) {
            statInvoice.setCart(DamselUtil.fromJson(invoiceEventStat.getInvoiceCart(), InvoiceCart.class));
        }

        if (invoiceEventStat.getInvoiceContext() != null) {
            Content content = new Content();
            //TODO we know about content type in this, its always json
            content.setType(ContentType.APPLICATION_JSON.getMimeType());
            content.setData(invoiceEventStat.getInvoiceContext());
            statInvoice.setContext(content);
        }

        return statInvoice;
    }

    private InvoiceStatus toStatInvoiceStatus(com.rbkmoney.damsel.domain.InvoiceStatus._Fields status, String statusDetails) throws NotFoundException {
        InvoiceStatus._Fields invoiceStatusField = InvoiceStatus._Fields.
                findByName(status.getFieldName());
        switch (invoiceStatusField) {
            case UNPAID:
                return InvoiceStatus.unpaid(new InvoiceUnpaid());
            case PAID:
                return InvoiceStatus.paid(new InvoicePaid());
            case CANCELLED:
                return InvoiceStatus.cancelled(new InvoiceCancelled(statusDetails));
            case FULFILLED:
                return InvoiceStatus.fulfilled(new InvoiceFulfilled(statusDetails));
            default:
                throw new NotFoundException(String.format("Status '%s' not found", status.getFieldName()));
        }
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

    public static class InvoicesParameters extends PaymentsFunction.PaymentsParameters {

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

        public Long getInvoiceAmount() {
            return getLongParameter(INVOICE_AMOUNT_PARAM, false);
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
                    new GetDataFunction(queryPart.getDescriptor() + ":" + GetDataFunction.FUNC_NAME, queryPart.getParameters()),
                    new GetCountFunction(queryPart.getDescriptor() + ":" + GetCountFunction.FUNC_NAME, queryPart.getParameters())
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
        public QueryResult<InvoiceEventStat, Collection<InvoiceEventStat>> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            InvoicesParameters parameters = new InvoicesParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Collection<InvoiceEventStat> result = functionContext.getDao().getInvoices(
                        parameters.getMerchantId(),
                        parameters.getShopId(),
                        Optional.ofNullable(parameters.getInvoiceId()),
                        Optional.ofNullable(parameters.getPaymentId()),
                        Optional.ofNullable(parameters.getInvoiceStatus()),
                        Optional.ofNullable(parameters.getPaymentStatus()),
                        Optional.ofNullable(parameters.getInvoiceAmount()),
                        Optional.ofNullable(parameters.getPaymentAmount()),
                        Optional.ofNullable(parameters.getPaymentEmail()),
                        Optional.ofNullable(parameters.getPaymentIp()),
                        Optional.ofNullable(parameters.getPaymentFingerprint()),
                        Optional.ofNullable(parameters.getPanMask()),
                        Optional.ofNullable(Instant.from(parameters.getFromTime())),
                        Optional.ofNullable(Instant.from(parameters.getToTime())),
                        Optional.ofNullable(parameters.getSize()),
                        Optional.ofNullable(parameters.getFrom())
                );
                return new BaseQueryResult<>(() -> result.stream(), () -> result);
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
                Integer result = functionContext.getDao().getInvoicesCount(
                        parameters.getMerchantId(),
                        parameters.getShopId(),
                        Optional.ofNullable(parameters.getInvoiceId()),
                        Optional.ofNullable(parameters.getPaymentId()),
                        Optional.ofNullable(parameters.getInvoiceStatus()),
                        Optional.ofNullable(parameters.getPaymentStatus()),
                        Optional.ofNullable(parameters.getInvoiceAmount()),
                        Optional.ofNullable(parameters.getPaymentAmount()),
                        Optional.ofNullable(parameters.getPaymentEmail()),
                        Optional.ofNullable(parameters.getPaymentIp()),
                        Optional.ofNullable(parameters.getPaymentFingerprint()),
                        Optional.ofNullable(parameters.getPanMask()),
                        Optional.ofNullable(Instant.from(parameters.getFromTime())),
                        Optional.ofNullable(Instant.from(parameters.getToTime())),
                        Optional.ofNullable(parameters.getSize()),
                        Optional.ofNullable(parameters.getFrom())
                );
                return new BaseQueryResult<>(() -> Stream.of(result), () -> result);
            } catch (DaoException e) {
                throw new QueryExecutionException(e);
            }
        }
    }
}
