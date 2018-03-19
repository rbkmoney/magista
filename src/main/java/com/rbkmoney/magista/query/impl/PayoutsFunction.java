package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.damsel.merch_stat.StatResponseData;
import com.rbkmoney.magista.dao.ConditionParameterSource;
import com.rbkmoney.magista.domain.enums.PayoutStatus;
import com.rbkmoney.magista.domain.enums.PayoutType;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
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

import static com.rbkmoney.magista.domain.tables.PayoutEventStat.PAYOUT_EVENT_STAT;
import static com.rbkmoney.magista.query.impl.Parameters.*;
import static com.rbkmoney.magista.util.TypeUtil.*;
import static org.jooq.Comparator.*;

public class PayoutsFunction extends PagedBaseFunction<PayoutEventStat, StatResponse> implements CompositeQuery<PayoutEventStat, StatResponse> {

    public static final String FUNC_NAME = "payouts";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    private PayoutsFunction(Object descriptor, QueryParameters params, CompositeQuery subquery) {
        super(descriptor, params, FUNC_NAME);
        this.subquery = subquery;
    }

    @Override
    public QueryResult<PayoutEventStat, StatResponse> execute(QueryContext context) throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<PayoutEventStat, StatResponse> execute(QueryContext context, List<QueryResult> collectedResults) throws QueryExecutionException {
        if (collectedResults.size() != 2) {
            throw new QueryExecutionException("Wrong query results count:" + collectedResults.size());
        }

        QueryResult<PayoutEventStat, List<PayoutEventStat>> payoutsResult = (QueryResult<PayoutEventStat, List<PayoutEventStat>>) collectedResults.get(0);
        QueryResult<Integer, Integer> countResult = (QueryResult<Integer, Integer>) collectedResults.get(1);

        return new BaseQueryResult<>(
                () -> payoutsResult.getDataStream(),
                () -> {
                    StatResponseData statResponseData = StatResponseData.payouts(payoutsResult.getDataStream()
                            .map(payoutEvent -> DamselUtil.toStatPayout(payoutEvent))
                            .collect(Collectors.toList()));

                    StatResponse statResponse = new StatResponse(statResponseData);
                    statResponse.setTotalCount(countResult.getCollectedStream());
                    return statResponse;
                });
    }

    @Override
    public PayoutsParameters getQueryParameters() {
        return (PayoutsParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new PayoutsParameters(parameters, derivedParameters);
    }

    @Override
    public List<Query> getChildQueries() {
        return subquery.getChildQueries();
    }

    @Override
    public boolean isParallel() {
        return subquery.isParallel();
    }

    public static class PayoutsParameters extends PagedBaseParameters {

        public PayoutsParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public PayoutsParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public String getPayoutId() {
            return getStringParameter(PAYOUT_ID_PARAM, false);
        }

        public String getPayoutStatus() {
            return getStringParameter(PAYOUT_STATUS_PARAM, false);
        }

        public List<String> getPayoutStatuses() {
            return getArrayParameter(PAYOUT_STATUSES_PARAM, false);
        }

        public String getPayoutType() {
            return getStringParameter(PAYOUT_TYPE_PARAM, false);
        }

        public TemporalAccessor getFromTime() {
            return getTimeParameter(FROM_TIME_PARAM, false);
        }

        public TemporalAccessor getToTime() {
            return getTimeParameter(TO_TIME_PARAM, false);
        }

    }

    public static class PayoutsValidator extends PagedBaseValidator {

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            PayoutsParameters payoutsParameters = super.checkParamsType(parameters, PayoutsParameters.class);

            validateTimePeriod(payoutsParameters.getFromTime(), payoutsParameters.getToTime());
        }
    }

    public static class PayoutsParser extends AbstractQueryParser {
        private PayoutsValidator validator = new PayoutsValidator();

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            Map<String, Object> funcSource = (Map) source.get(FUNC_NAME);
            PayoutsParameters parameters = getValidatedParameters(funcSource, parent, PayoutsParameters::new, validator);

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

    public static class PayoutsBuilder extends AbstractQueryBuilder {
        private PayoutsValidator validator = new PayoutsValidator();

        @Override
        public Query buildQuery(List<QueryPart> queryParts, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(PayoutsParser.getMainDescriptor(), queryParts, queryPart -> createQuery(queryPart));
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
            return createPayoutsFunction(queryPart.getDescriptor(), queryPart.getParameters(), compositeQuery);
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(PayoutsParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }
    }

    private static PayoutsFunction createPayoutsFunction(Object descriptor, QueryParameters queryParameters, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        PayoutsFunction payoutsFunction = new PayoutsFunction(descriptor, queryParameters, subquery);
        subquery.setParentQuery(payoutsFunction);
        return payoutsFunction;
    }

    private static class GetDataFunction extends PagedBaseFunction<PayoutEventStat, Collection<PayoutEventStat>> {
        private static final String FUNC_NAME = PayoutsFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params) {
            super(descriptor, params, FUNC_NAME);
        }

        @Override
        public QueryResult<PayoutEventStat, Collection<PayoutEventStat>> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            PayoutsParameters parameters = new PayoutsParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Collection<PayoutEventStat> result = functionContext.getDao().getPayouts(
                        Optional.ofNullable(parameters.getMerchantId()),
                        Optional.ofNullable(parameters.getShopId()),
                        buildPayoutConditionParameterSource(parameters),
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
        private static final String FUNC_NAME = PayoutsFunction.FUNC_NAME + "_count";

        public GetCountFunction(Object descriptor, QueryParameters params) {
            super(descriptor, params, FUNC_NAME);
        }

        @Override
        public QueryResult<Integer, Integer> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            PayoutsParameters parameters = new PayoutsParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Integer result = functionContext.getDao().getPayoutsCount(
                        Optional.ofNullable(parameters.getMerchantId()),
                        Optional.ofNullable(parameters.getShopId()),
                        buildPayoutConditionParameterSource(parameters)
                );
                return new BaseQueryResult<>(() -> Stream.of(result), () -> result);
            } catch (DaoException e) {
                throw new QueryExecutionException(e);
            }
        }
    }

    public static ConditionParameterSource buildPayoutConditionParameterSource(PayoutsParameters parameters) {
        return new ConditionParameterSource()
                .addValue(PAYOUT_EVENT_STAT.PARTY_ID, parameters.getMerchantId(), EQUALS)
                .addValue(PAYOUT_EVENT_STAT.PARTY_SHOP_ID, parameters.getShopId(), EQUALS)
                .addValue(PAYOUT_EVENT_STAT.PAYOUT_ID, parameters.getPayoutId(), EQUALS)
                .addValue(PAYOUT_EVENT_STAT.PAYOUT_STATUS,
                        toEnumField(parameters.getPayoutStatus(), PayoutStatus.class),
                        EQUALS)
                .addInConditionValue(PAYOUT_EVENT_STAT.PAYOUT_STATUS,
                        toEnumFields(parameters.getPayoutStatuses(), PayoutStatus.class))
                .addValue(PAYOUT_EVENT_STAT.PAYOUT_TYPE,
                        toEnumField(parameters.getPayoutType(), PayoutType.class),
                        EQUALS)
                .addValue(PAYOUT_EVENT_STAT.PAYOUT_CREATED_AT, toLocalDateTime(parameters.getFromTime()), GREATER_OR_EQUAL)
                .addValue(PAYOUT_EVENT_STAT.PAYOUT_CREATED_AT, toLocalDateTime(parameters.getToTime()), LESS);
    }

}
