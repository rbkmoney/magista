package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.damsel.merch_stat.StatResponseData;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.domain.tables.pojos.Adjustment;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.query.*;
import com.rbkmoney.magista.query.builder.QueryBuilder;
import com.rbkmoney.magista.query.builder.QueryBuilderException;
import com.rbkmoney.magista.query.impl.builder.AbstractQueryBuilder;
import com.rbkmoney.magista.query.impl.parser.AbstractQueryParser;
import com.rbkmoney.magista.query.parser.QueryParserException;
import com.rbkmoney.magista.query.parser.QueryPart;
import com.rbkmoney.magista.util.BeanUtil;

import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rbkmoney.magista.query.impl.Parameters.*;

public class AdjustmentsForReportFunction extends PagedBaseFunction<Adjustment, StatResponse> implements CompositeQuery<Adjustment, StatResponse> {

    public static final String FUNC_NAME = "adjustments_for_report";

    private final CompositeQuery<QueryResult, List<QueryResult>> subquery;

    public AdjustmentsForReportFunction(Object descriptor, QueryParameters params, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        super(descriptor, params, FUNC_NAME, continuationToken);
        this.subquery = subquery;
    }

    @Override
    public QueryResult<Adjustment, StatResponse> execute(QueryContext context) throws QueryExecutionException {
        QueryResult<QueryResult, List<QueryResult>> collectedResults = subquery.execute(context);

        return execute(context, collectedResults.getCollectedStream());
    }

    @Override
    public QueryResult<Adjustment, StatResponse> execute(QueryContext context, List<QueryResult> collectedResults) throws QueryExecutionException {
        QueryResult<Adjustment, List<Adjustment>> adjustmentsForReportResult = (QueryResult<Adjustment, List<Adjustment>>) collectedResults.get(0);

        return new BaseQueryResult<>(
                adjustmentsForReportResult::getDataStream,
                () -> {
                    StatResponseData statResponseData = StatResponseData.records(adjustmentsForReportResult.getDataStream()
                            .map(adjustmentResponse -> BeanUtil.toStringMap(adjustmentResponse)).collect(Collectors.toList()));
                    StatResponse statResponse = new StatResponse(statResponseData);
                    List<Adjustment> adjustmentStats = adjustmentsForReportResult.getCollectedStream();
                    if (!adjustmentsForReportResult.getCollectedStream().isEmpty() && getQueryParameters().getSize() == adjustmentStats.size()) {
                        String createdAt = adjustmentStats.get(adjustmentStats.size() - 1).getEventCreatedAt().atOffset(ZoneOffset.UTC).toString();
                        String token = getContext(context)
                                .getTokenGenService()
                                .generateToken(getQueryParameters(), TypeUtil.stringToLocalDateTime(createdAt));
                        statResponse.setContinuationToken(token);
                    }
                    return statResponse;
                }
        );
    }

    @Override
    public AdjustmentsForReportParameters getQueryParameters() {
        return (AdjustmentsForReportParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new AdjustmentsForReportParameters(parameters, derivedParameters);
    }

    @Override
    public List<Query> getChildQueries() {
        return subquery.getChildQueries();
    }

    @Override
    public boolean isParallel() {
        return subquery.isParallel();
    }

    public static class AdjustmentsForReportParameters extends PagedBaseParameters {

        public AdjustmentsForReportParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public AdjustmentsForReportParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public TemporalAccessor getFromTime() {
            return getTimeParameter(FROM_TIME_PARAM, false);
        }

        public TemporalAccessor getToTime() {
            return getTimeParameter(TO_TIME_PARAM, false);
        }

    }

    public static class AdjustmentsForReportValidator extends PagedBaseValidator {

        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            AdjustmentsForReportParameters adjustmentsParameters = super.checkParamsType(parameters, AdjustmentsForReportParameters.class);

            if (adjustmentsParameters.getMerchantId() == null) {
                checkParamsResult(true, MERCHANT_ID_PARAM, RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
            }

            validateTimePeriod(adjustmentsParameters.getFromTime(), adjustmentsParameters.getToTime());
        }
    }

    public static class AdjustmentsForReportParser extends AbstractQueryParser {
        private AdjustmentsForReportValidator validator = new AdjustmentsForReportValidator();

        @Override
        public List<QueryPart> parseQuery(Map<String, Object> source, QueryPart parent) throws QueryParserException {
            Map<String, Object> funcSource = (Map) source.get(FUNC_NAME);
            AdjustmentsForReportParameters parameters = getValidatedParameters(funcSource, parent, AdjustmentsForReportParameters::new, validator);

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

    public static class AdjustmentsForReportBuilder extends AbstractQueryBuilder {
        private AdjustmentsForReportValidator validator = new AdjustmentsForReportValidator();

        @Override
        public Query buildQuery(QueryContext queryContext, List<QueryPart> queryParts, String continuationToken, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
            Query resultQuery = buildSingleQuery(AdjustmentsForReportParser.getMainDescriptor(), queryParts, queryPart -> createQuery(queryPart, continuationToken));
            validator.validateQuery(resultQuery, queryContext);
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
            return createAdjustmentsForReportFunction(queryPart.getDescriptor(), queryPart.getParameters(), continuationToken, compositeQuery);
        }

        @Override
        public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
            return getMatchedPartsStream(AdjustmentsForReportParser.getMainDescriptor(), queryParts).findFirst().isPresent();
        }
    }

    private static AdjustmentsForReportFunction createAdjustmentsForReportFunction(Object descriptor, QueryParameters queryParameters, String continuationToken, CompositeQuery<QueryResult, List<QueryResult>> subquery) {
        AdjustmentsForReportFunction adjustmentsForReportFunction = new AdjustmentsForReportFunction(descriptor, queryParameters, continuationToken, subquery);
        subquery.setParentQuery(adjustmentsForReportFunction);
        return adjustmentsForReportFunction;
    }

    private static class GetDataFunction extends PagedBaseFunction<Adjustment, Collection<Adjustment>> {
        private static final String FUNC_NAME = AdjustmentsForReportFunction.FUNC_NAME + "_data";

        public GetDataFunction(Object descriptor, QueryParameters params, String continuationToken) {
            super(descriptor, params, FUNC_NAME, continuationToken);
        }

        @Override
        public QueryResult<Adjustment, Collection<Adjustment>> execute(QueryContext context) throws QueryExecutionException {
            FunctionQueryContext functionContext = getContext(context);
            AdjustmentsForReportParameters parameters = new AdjustmentsForReportParameters(getQueryParameters(), getQueryParameters().getDerivedParameters());
            try {
                Collection<Adjustment> result = functionContext.getReportDao().getAdjustmentsForReport(
                        parameters.getMerchantId(),
                        Optional.ofNullable(parameters.getShopId()),
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
