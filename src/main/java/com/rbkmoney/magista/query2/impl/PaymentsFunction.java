package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.CompositeQuery;
import com.rbkmoney.magista.query2.Query;
import com.rbkmoney.magista.query2.QueryParameters;
import com.rbkmoney.magista.query2.builder.QueryBuilder;
import com.rbkmoney.magista.query2.builder.QueryBuilderException;
import com.rbkmoney.magista.query2.impl.builder.AbstractQueryBuilder;
import com.rbkmoney.magista.query2.impl.parser.AbstractQueryParser;
import com.rbkmoney.magista.query2.parser.QueryParserException;
import com.rbkmoney.magista.query2.parser.QueryPart;

import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.magista.query2.impl.Parameters.*;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public class PaymentsFunction extends PagedBaseFunction implements CompositeQuery {

    public static final String FUNC_NAME = "payments";

    private final CompositeQuery subquery;

    private PaymentsFunction(Object descriptor, QueryParameters params, CompositeQuery subquery) {
        super(descriptor, params, FUNC_NAME);
        this.subquery = subquery;
    }

    @Override
    public PaymentsParameters getQueryParameters() {
        return (PaymentsParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new PaymentsParameters(parameters, derivedParameters);
    }

    @Override
    public List<Query> getChildQueries() {
        return subquery.getChildQueries();
    }

    @Override
    public void setChildQueries(List<Query> queries, boolean parallel) {
        subquery.setChildQueries(queries, parallel);
    }

    @Override
    public boolean isParallel() {
        return subquery.isParallel();
    }

    public static class PaymentsParameters extends PagedBaseParameters {

        public PaymentsParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public PaymentsParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public String getInvoiceId() {
            return getStringParameter(INVOICE_ID_PARAM, false);
        }

        public String getPaymentId() {
            return getStringParameter(PAYMENT_ID_PARAM, false);
        }

        public String getPaymentStatus() {
            return getStringParameter(PAYMENT_STATUS_PARAM, false);
        }

        public TemporalAccessor getFromTime() {
            return getTimeParameter(FROM_TIME_PARAM, false);
        }

        public TemporalAccessor getToTime() {
            return getTimeParameter(TO_TIME_PARAM, false);
        }

        public String getPanMask() {
            return getStringParameter(PAN_MASK_PARAM, false);
        }
    }

    public static class PaymentsValidator extends PagedBaseValidator {

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            PaymentsParameters paymentsParameters = super.checkParamsType(parameters, PaymentsParameters.class);

            String val = paymentsParameters.getPanMask();
            if (val != null && !val.matches("[\\d*]+")) {
                checkParamsResult(true, PAN_MASK_PARAM, RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
            }
            validateTimePeriod(paymentsParameters.getFromTime(), paymentsParameters.getToTime());
        }
    }

    public static class PaymentsParser extends AbstractQueryParser {
        private PaymentsValidator validator = new PaymentsValidator();

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            Map<String, Object> funcSource = (Map) source.get(FUNC_NAME);
            PaymentsParameters parameters = getValidatedParameters(funcSource, parent, PaymentsParameters::new, validator);

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

    public static class PaymentsBuilder extends AbstractQueryBuilder {
        private PaymentsValidator validator = new PaymentsValidator();

        @Override
        public Query buildQuery(List<QueryPart> queryParts, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildAndWrapQueries(PaymentsParser.getMainDescriptor(), queryParts, queryPart -> createQuery(queryPart), getParameters(parentQueryPart));
            validator.validateQuery(resultQuery);
            return resultQuery;
        }

        private CompositeQuery createQuery(QueryPart queryPart) {
            List<Query> queries = Arrays.asList(
                    new GetDataFunction(queryPart.getDescriptor() + ":"+GetDataFunction.FUNC_NAME, queryPart.getParameters()),
                    new GetCountFunction(queryPart.getDescriptor() + ":" +GetCountFunction.FUNC_NAME, queryPart.getParameters())
            );
            CompositeQuery compositeQuery = createCompositeQuery(queryPart.getDescriptor(), getParameters(queryPart.getParent()), queries, false);
            return createPaymentsFunction(queryPart.getDescriptor(), queryPart.getParameters(), compositeQuery);
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(PaymentsParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }

    }

    private static PaymentsFunction createPaymentsFunction(Object descriptor, QueryParameters queryParameters, CompositeQuery subquery) {
        PaymentsFunction paymentsFunction = new PaymentsFunction(descriptor, queryParameters, subquery);
        subquery.setParentQuery(paymentsFunction);
        return paymentsFunction;
    }

    private static class GetDataFunction extends PagedBaseFunction {
        private static final String FUNC_NAME = PaymentsFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params) {
            super(descriptor, params, FUNC_NAME);
        }
    }

    private static class GetCountFunction extends ScopedBaseFunction {
        private static final String FUNC_NAME = PaymentsFunction.FUNC_NAME + "_count";

        public GetCountFunction(Object descriptor, QueryParameters params) {
            super(descriptor, params, FUNC_NAME);
        }
    }
}
