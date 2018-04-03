package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.damsel.merch_stat.StatResponseData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.query.*;
import com.rbkmoney.magista.query.parser.QueryPart;
import com.rbkmoney.magista.util.TypeUtil;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by tolkonepiu on 21/12/2016.
 */
public class AccountingReportFunction extends ReportBaseFunction {

    public static final String FUNC_NAME = "shop_accounting_report";

    public AccountingReportFunction(Object descriptor, QueryParameters params) {
        super(descriptor, params, FUNC_NAME);
    }

    @Override
    public QueryResult<Map<String, String>, StatResponse> execute(QueryContext context) throws QueryExecutionException {
        try {
            Collection<Map<String, String>> result = getContext(context).getDao().getAccountingDataByPeriod(
                    getQueryParameters().getMerchantId(),
                    getQueryParameters().getContractId(),
                    Optional.ofNullable(TypeUtil.toLocalDateTime(getQueryParameters().getFromTime())),
                    TypeUtil.toLocalDateTime(getQueryParameters().getToTime())
            );

            return new BaseQueryResult<>(() -> result.stream(), () -> new StatResponse(StatResponseData.records(result.stream().collect(Collectors.toList()))));
        } catch (DaoException e) {
            throw new QueryExecutionException(e);
        }
    }

    public static class AccountingReportParser extends ReportBaseFunction.ReportBaseParser {

        public AccountingReportParser() {
            super(FUNC_NAME);
        }

        public static String getMainDescriptor() {
            return FUNC_NAME;
        }
    }

    public static class AccountingReportBuilder extends AccountingReportFunction.ReportBaseBuilder {

        @Override
        protected Query createQuery(QueryPart queryPart) {
            return new AccountingReportFunction(queryPart.getDescriptor(), queryPart.getParameters());
        }

        @Override
        protected Object getDescriptor(List<QueryPart> queryParts) {
            return AccountingReportFunction.AccountingReportParser.getMainDescriptor();
        }
    }

}
