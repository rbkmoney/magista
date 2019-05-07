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

/**
 * Created by vpankrashkin on 03.08.16.
 */
public class PaymentsFunction extends PagedBaseFunction<Map.Entry<Long, StatPayment>, StatResponse> implements CompositeQuery<Map.Entry<Long, StatPayment>, StatResponse> {

    public static final String FUNC_NAME = "payments";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    private PaymentsFunction(Object descriptor, QueryParameters params, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
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
        QueryResult<Map.Entry<Long, StatPayment>, List<Map.Entry<Long, StatPayment>>> paymentsResult = (QueryResult<Map.Entry<Long, StatPayment>, List<Map.Entry<Long, StatPayment>>>) collectedResults.get(0);

        return new BaseQueryResult<>(
                () -> paymentsResult.getDataStream(),
                () -> {
                    StatResponseData statResponseData = StatResponseData.payments(paymentsResult.getDataStream()
                            .map(paymentResponse -> paymentResponse.getValue()).collect(Collectors.toList()));
                    StatResponse statResponse = new StatResponse(statResponseData);
                    List<Map.Entry<Long, StatPayment>> payments = paymentsResult.getCollectedStream();
                    if (!payments.isEmpty() && getQueryParameters().getSize() == payments.size()) {
                        statResponse.setContinuationToken(
                                TokenUtil.buildToken(
                                        getQueryParameters(),
                                        payments.get(payments.size() - 1).getKey()
                                )
                        );
                    }
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

        public Long getPaymentDomainRevision() {
            return getLongParameter(PAYMENT_DOMAIN_REVISION_PARAM, false);
        }

        public Long getFromPaymentDomainRevision() {
            return getLongParameter(FROM_PAYMENT_DOMAIN_REVISION_PARAM, false);
        }

        public Long getToPaymentDomainRevision() {
            return getLongParameter(TO_PAYMENT_DOMAIN_REVISION_PARAM, false);
        }

        public String getPaymentCustomerId() {
            return getStringParameter(PAYMENT_CUSTOMER_ID_PARAM, false);
        }

        public Long getPaymentAmount() {
            return getLongParameter(PAYMENT_AMOUNT_PARAM, false);
        }

        public String getPaymentBankCardBin() {
            return getStringParameter(PAYMENT_BANK_CARD_BIN_PARAM, false);
        }

        public String getPaymentBankCardLastDigits() {
            return getStringParameter(PAYMENT_BANK_CARD_LAST_DIGITS_PARAM, false);
        }


        public String getPaymentBankCardSystem() {
            return getStringParameter(PAYMENT_BANK_CARD_PAYMENT_SYSTEM_PARAM, false);
        }

        public String getPaymentBankCardTokenProvider() {
            return getStringParameter(PAYMENT_BANK_CARD_TOKEN_PROVIDER_PARAM, false);
        }
    }

    public static class PaymentsValidator extends PagedBaseValidator {

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            PaymentsParameters paymentsParameters = super.checkParamsType(parameters, PaymentsParameters.class);

            String bin = paymentsParameters.getPaymentBankCardBin();
            if (bin != null && !bin.matches("^\\d{6,8}$")) {
                checkParamsResult(true, PAYMENT_BANK_CARD_BIN_PARAM, RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
            }

            String lastDigits = paymentsParameters.getPaymentBankCardLastDigits();
            if (lastDigits != null && !lastDigits.matches("^\\d{2,4}$")) {
                checkParamsResult(true, PAYMENT_BANK_CARD_LAST_DIGITS_PARAM, RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
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
        public Query buildQuery(List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(PaymentsParser.getMainDescriptor(), queryParts, queryPart -> createQuery(queryPart, continuationToken));
            validator.validateQuery(resultQuery);
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
            return createPaymentsFunction(queryPart.getDescriptor(), queryPart.getParameters(), continuationToken, compositeQuery);
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(PaymentsParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }
    }

    private static PaymentsFunction createPaymentsFunction(Object descriptor, QueryParameters queryParameters, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        PaymentsFunction paymentsFunction = new PaymentsFunction(descriptor, queryParameters, continuationToken, subquery);
        subquery.setParentQuery(paymentsFunction);
        return paymentsFunction;
    }

    private static class GetDataFunction extends PagedBaseFunction<Map.Entry<Long, StatPayment>, Collection<Map.Entry<Long, StatPayment>>> {
        private static final String FUNC_NAME = PaymentsFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params, String continuationToken) {
            super(descriptor, params, FUNC_NAME, continuationToken);
        }

        @Override
        public QueryResult<Map.Entry<Long, StatPayment>, Collection<Map.Entry<Long, StatPayment>>> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            PaymentsParameters parameters = new PaymentsParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Collection<Map.Entry<Long, StatPayment>> result = functionContext.getSearchDao().getPayments(
                        parameters,
                        Optional.ofNullable(TypeUtil.toLocalDateTime(parameters.getFromTime())),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(parameters.getToTime())),
                        getFromId(),
                        parameters.getSize()
                );
                return new BaseQueryResult<>(() -> result.stream(), () -> result);
            } catch (DaoException e) {
                throw new QueryExecutionException(e);
            }
        }
    }

}
