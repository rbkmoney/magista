package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.damsel.merch_stat.StatResponseData;
import com.rbkmoney.magista.dao.ConditionParameterSource;
import com.rbkmoney.magista.domain.enums.InvoicePaymentStatus;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.query.*;
import com.rbkmoney.magista.query.builder.QueryBuilder;
import com.rbkmoney.magista.query.builder.QueryBuilderException;
import com.rbkmoney.magista.query.impl.builder.AbstractQueryBuilder;
import com.rbkmoney.magista.query.impl.parser.AbstractQueryParser;
import com.rbkmoney.magista.query.parser.QueryParserException;
import com.rbkmoney.magista.query.parser.QueryPart;
import com.rbkmoney.magista.util.DamselUtil;
import com.rbkmoney.magista.util.TypeUtil;

import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.magista.domain.tables.InvoiceEventStat.INVOICE_EVENT_STAT;
import static com.rbkmoney.magista.query.impl.Parameters.*;
import static com.rbkmoney.magista.util.TypeUtil.toEnumField;
import static com.rbkmoney.magista.util.TypeUtil.toLocalDateTime;
import static org.jooq.Comparator.*;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public class PaymentsFunction extends PagedBaseFunction<InvoiceEventStat, StatResponse> implements CompositeQuery<InvoiceEventStat, StatResponse> {

    public static final String FUNC_NAME = "payments";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    private PaymentsFunction(Object descriptor, QueryParameters params, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
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

        QueryResult<InvoiceEventStat, List<InvoiceEventStat>> paymentsResult = (QueryResult<InvoiceEventStat, List<InvoiceEventStat>>) collectedResults.get(0);
        QueryResult<Integer, Integer> countResult = (QueryResult<Integer, Integer>) collectedResults.get(1);

        return new BaseQueryResult<>(
                () -> paymentsResult.getDataStream(),
                () -> {
                    StatResponseData statResponseData = StatResponseData.payments(paymentsResult.getDataStream()
                            .map(payment -> DamselUtil.toStatPayment(payment)).collect(Collectors.toList()));
                    StatResponse statResponse = new StatResponse(statResponseData);
                    statResponse.setTotalCount(countResult.getCollectedStream());
                    return statResponse;
                }
        );
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

        public String getPaymentEmail() {
            return getStringParameter(PAYMENT_EMAIL_PARAM, false);
        }

        public String getPaymentIp() {
            return getStringParameter(PAYMENT_IP_PARAM, false);
        }

        public String getPaymentFingerprint() {
            return getStringParameter(PAYMENT_FINGERPRINT_PARAM, false);
        }

        public String getPaymentFlow() {
            return getStringParameter(PAYMENT_FLOW_PARAM, false);
        }

        public String getPaymentMethod() {
            return getStringParameter(PAYMENT_METHOD_PARAM, false);
        }

        public String getPaymentTerminalProvider() {
            return getStringParameter(PAYMENT_TERMINAL_PROVIDER_PARAM, false);
        }

        public String getPaymentCustomerId() {
            return getStringParameter(PAYMENT_CUSTOMER_ID_PARAM, false);
        }

        public Long getPaymentAmount() {
            return getLongParameter(PAYMENT_AMOUNT_PARAM, false);
        }

        public String getPanMask() {
            return getStringParameter(PAYMENT_PAN_MASK_PARAM, false);
        }
    }

    public static class PaymentsValidator extends PagedBaseValidator {

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            PaymentsParameters paymentsParameters = super.checkParamsType(parameters, PaymentsParameters.class);

            String val = paymentsParameters.getPanMask();
            if (val != null && !val.matches("[\\d*]+")) {
                checkParamsResult(true, PAYMENT_PAN_MASK_PARAM, RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
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
            Query resultQuery = buildSingleQuery(PaymentsParser.getMainDescriptor(), queryParts, queryPart -> createQuery(queryPart));
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
            return createPaymentsFunction(queryPart.getDescriptor(), queryPart.getParameters(), compositeQuery);
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(PaymentsParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }
    }

    private static PaymentsFunction createPaymentsFunction(Object descriptor, QueryParameters queryParameters, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        PaymentsFunction paymentsFunction = new PaymentsFunction(descriptor, queryParameters, subquery);
        subquery.setParentQuery(paymentsFunction);
        return paymentsFunction;
    }

    private static class GetDataFunction extends PagedBaseFunction<InvoiceEventStat, Collection<InvoiceEventStat>> {
        private static final String FUNC_NAME = PaymentsFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params) {
            super(descriptor, params, FUNC_NAME);
        }

        @Override
        public QueryResult<InvoiceEventStat, Collection<InvoiceEventStat>> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            PaymentsParameters parameters = new PaymentsParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Collection<InvoiceEventStat> result = functionContext.getDao().getPayments(
                        Optional.ofNullable(parameters.getMerchantId()),
                        Optional.ofNullable(parameters.getShopId()),
                        buildPaymentConditionParameterSource(parameters),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(parameters.getFromTime())),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(parameters.getToTime())),
                        Optional.ofNullable(parameters.getFrom()),
                        Optional.ofNullable(parameters.getSize())
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
            PaymentsParameters parameters = new PaymentsParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Integer result = functionContext.getDao().getPaymentsCount(
                        Optional.ofNullable(parameters.getMerchantId()),
                        Optional.ofNullable(parameters.getShopId()),
                        buildPaymentConditionParameterSource(parameters),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(parameters.getFromTime())),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(parameters.getToTime()))
                );
                return new BaseQueryResult<>(() -> Stream.of(result), () -> result);
            } catch (DaoException e) {
                throw new QueryExecutionException(e);
            }
        }
    }

    public static ConditionParameterSource buildPaymentConditionParameterSource(PaymentsParameters parameters) {
        return new ConditionParameterSource()
                .addValue(INVOICE_EVENT_STAT.PARTY_ID, parameters.getMerchantId(), EQUALS)
                .addValue(INVOICE_EVENT_STAT.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                .addValue(INVOICE_EVENT_STAT.INVOICE_ID, parameters.getInvoiceId(), EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_ID, parameters.getPaymentId(), EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_STATUS,
                        toEnumField(parameters.getPaymentStatus(), InvoicePaymentStatus.class),
                        EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_FLOW, parameters.getPaymentFlow(), EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_TOOL, parameters.getPaymentMethod(), EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_TERMINAL_PROVIDER, parameters.getPaymentTerminalProvider(), EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_AMOUNT, parameters.getPaymentAmount(), EQUALS)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_EMAIL, parameters.getPaymentEmail(), LIKE)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_IP, parameters.getPaymentIp(), LIKE)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_FINGERPRINT, parameters.getPaymentFingerprint(), LIKE)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_MASKED_PAN, parameters.getPanMask(), LIKE)
                .addValue(INVOICE_EVENT_STAT.PAYMENT_CUSTOMER_ID, parameters.getPaymentCustomerId(), EQUALS)
                .addInConditionValue(INVOICE_EVENT_STAT.PARTY_SHOP_CATEGORY_ID, parameters.getShopCategoryIds());
    }
}
