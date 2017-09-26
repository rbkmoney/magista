package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.BankCardPaymentSystem;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
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
import org.apache.http.entity.ContentType;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.magista.query.impl.Parameters.*;

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
                            .map(payment -> toStatPayment(payment)).collect(Collectors.toList()));
                    StatResponse statResponse = new StatResponse(statResponseData);
                    statResponse.setTotalCount(countResult.getCollectedStream());
                    return statResponse;
                }
        );
    }

    private StatPayment toStatPayment(InvoiceEventStat invoicePaymentStat) {
        StatPayment statPayment = new StatPayment();

        statPayment.setId(invoicePaymentStat.getPaymentId());
        statPayment.setInvoiceId(invoicePaymentStat.getInvoiceId());
        statPayment.setOwnerId(invoicePaymentStat.getPartyId());
        statPayment.setShopId(invoicePaymentStat.getPartyShopId());
        statPayment.setCreatedAt(TypeUtil.temporalToString(invoicePaymentStat.getPaymentCreatedAt()
                .toInstant(ZoneOffset.UTC)));
        statPayment.setStatus(toStatPaymentStatus(
                com.rbkmoney.damsel.domain.InvoicePaymentStatus._Fields.findByName(
                        invoicePaymentStat.getPaymentStatus().getLiteral()
                ),
                invoicePaymentStat.getPaymentFailureClass(),
                invoicePaymentStat.getPaymentExternalFailureCode(),
                invoicePaymentStat.getPaymentExternalFailureDescription()
        ));

        statPayment.setAmount(invoicePaymentStat.getPaymentAmount());
        statPayment.setFee(invoicePaymentStat.getPaymentFee());
        statPayment.setCurrencySymbolicCode(invoicePaymentStat.getPaymentCurrencyCode());

        if (invoicePaymentStat.getPaymentTool() != null) {
            PaymentResourcePayer paymentResourcePayer = new PaymentResourcePayer();
            PaymentTool paymentTool = toStatPaymentTool(
                    com.rbkmoney.damsel.domain.PaymentTool._Fields.findByName(
                            invoicePaymentStat.getPaymentTool()
                    ),
                    invoicePaymentStat
            );
            paymentResourcePayer.setPaymentTool(paymentTool);
            paymentResourcePayer.setIpAddress(invoicePaymentStat.getPaymentIp());
            paymentResourcePayer.setFingerprint(invoicePaymentStat.getPaymentFingerprint());
            paymentResourcePayer.setPhoneNumber(invoicePaymentStat.getPaymentPhoneNumber());
            paymentResourcePayer.setEmail(invoicePaymentStat.getPaymentEmail());
            paymentResourcePayer.setSessionId(invoicePaymentStat.getPaymentSessionId());

            statPayment.setPayer(Payer.payment_resource(paymentResourcePayer));
        } else if (invoicePaymentStat.getPaymentCustomerId() != null) {
            statPayment.setPayer(Payer.customer(new CustomerPayer(invoicePaymentStat.getPaymentCustomerId())));
        }

        if (invoicePaymentStat.getPaymentContext() != null) {
            Content content = new Content();
            //TODO we know about content type in this, its always json
            content.setType(ContentType.APPLICATION_JSON.getMimeType());
            content.setData(invoicePaymentStat.getPaymentContext());
            statPayment.setContext(content);
        }

        statPayment.setFlow(toInvoicePaymentFlow(invoicePaymentStat));

        LocationInfo locationInfo = new LocationInfo(
                invoicePaymentStat.getPaymentCityId(),
                invoicePaymentStat.getPaymentCountryId()
        );
        statPayment.setLocationInfo(locationInfo);

        return statPayment;
    }

    private InvoicePaymentFlow toInvoicePaymentFlow(InvoiceEventStat invoiceEventStat) {
        InvoicePaymentFlow._Fields paymentFlow = InvoicePaymentFlow._Fields.findByName(invoiceEventStat.getPaymentFlow());
        switch (paymentFlow) {
            case HOLD:
                return InvoicePaymentFlow.hold(new InvoicePaymentFlowHold(
                        OnHoldExpiration.valueOf(invoiceEventStat.getPaymentHoldOnExpiration()),
                        TypeUtil.temporalToString(invoiceEventStat.getPaymentHoldUntil())
                ));
            case INSTANT:
                return InvoicePaymentFlow.instant(new InvoicePaymentFlowInstant());
            default:
                throw new NotFoundException(String.format("Payment flow '%s' not found.", invoiceEventStat.getPaymentFlow()));
        }
    }

    private PaymentTool toStatPaymentTool(com.rbkmoney.damsel.domain.PaymentTool._Fields paymentTool, InvoiceEventStat invoicePaymentStat) {
        switch (paymentTool) {
            case BANK_CARD:
                return PaymentTool.bank_card(new BankCard(
                        invoicePaymentStat.getPaymentToken(),
                        BankCardPaymentSystem.valueOf(invoicePaymentStat.getPaymentSystem()),
                        invoicePaymentStat.getPaymentBin(),
                        invoicePaymentStat.getPaymentMaskedPan()
                ));
            case PAYMENT_TERMINAL:
                return PaymentTool.payment_terminal(new PaymentTerminal(
                        TerminalPaymentProvider.valueOf(invoicePaymentStat.getPaymentTerminalProvider())
                ));
            default:
                throw new NotFoundException(String.format("Payment tool '%s' not found", paymentTool.getFieldName()));
        }
    }

    private InvoicePaymentStatus toStatPaymentStatus(
            com.rbkmoney.damsel.domain.InvoicePaymentStatus._Fields status,
            String failureClass,
            String externalFailureCode,
            String externalFailureDescription
    ) {
        switch (status) {
            case PENDING:
                return InvoicePaymentStatus.pending(new InvoicePaymentPending());
            case PROCESSED:
                return InvoicePaymentStatus.processed(new InvoicePaymentProcessed());
            case CAPTURED:
                return InvoicePaymentStatus.captured(new InvoicePaymentCaptured());
            case CANCELLED:
                return InvoicePaymentStatus.cancelled(new InvoicePaymentCancelled());
            case REFUNDED:
                return InvoicePaymentStatus.refunded(new InvoicePaymentRefunded());
            case FAILED:
                return InvoicePaymentStatus.failed(new InvoicePaymentFailed(
                        toOperationFailure(failureClass, externalFailureCode, externalFailureDescription)
                ));
            default:
                throw new NotFoundException(String.format("Payment status '%s' not found", status.getFieldName()));
        }
    }

    private OperationFailure toOperationFailure(String failureClass, String externalFailureCode, String externalFailureDescription) {
        OperationFailure._Fields failureType = OperationFailure._Fields.findByName(failureClass);
        switch (failureType) {
            case OPERATION_TIMEOUT:
                return OperationFailure.operation_timeout(new OperationTimeout());
            case EXTERNAL_FAILURE:
                ExternalFailure externalFailure = new ExternalFailure();
                externalFailure.setCode(externalFailureCode);
                externalFailure.setDescription(externalFailureDescription);
                return OperationFailure.external_failure(externalFailure);
            default:
                throw new NotFoundException(String.format("Failure type '%s' not found", failureClass));
        }
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
                        parameters.getMerchantId(),
                        parameters.getShopId(),
                        Optional.ofNullable(parameters.getInvoiceId()),
                        Optional.ofNullable(parameters.getPaymentId()),
                        Optional.ofNullable(parameters.getPaymentStatus()),
                        Optional.ofNullable(parameters.getPaymentFlow()),
                        Optional.ofNullable(parameters.getPaymentMethod()),
                        Optional.ofNullable(parameters.getPaymentTerminalProvider()),
                        Optional.ofNullable(parameters.getPaymentEmail()),
                        Optional.ofNullable(parameters.getPaymentIp()),
                        Optional.ofNullable(parameters.getPaymentFingerprint()),
                        Optional.ofNullable(parameters.getPanMask()),
                        Optional.ofNullable(parameters.getPaymentAmount()),
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
            PaymentsParameters parameters = new PaymentsParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Integer result = functionContext.getDao().getPaymentsCount(
                        parameters.getMerchantId(),
                        parameters.getShopId(),
                        Optional.ofNullable(parameters.getInvoiceId()),
                        Optional.ofNullable(parameters.getPaymentId()),
                        Optional.ofNullable(parameters.getPaymentStatus()),
                        Optional.ofNullable(parameters.getPaymentFlow()),
                        Optional.ofNullable(parameters.getPaymentMethod()),
                        Optional.ofNullable(parameters.getPaymentTerminalProvider()),
                        Optional.ofNullable(parameters.getPaymentEmail()),
                        Optional.ofNullable(parameters.getPaymentIp()),
                        Optional.ofNullable(parameters.getPaymentFingerprint()),
                        Optional.ofNullable(parameters.getPanMask()),
                        Optional.ofNullable(parameters.getPaymentAmount()),
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
