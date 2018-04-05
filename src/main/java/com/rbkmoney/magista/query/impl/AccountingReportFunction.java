package com.rbkmoney.magista.query.impl;

import com.google.common.collect.ImmutableMap;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.damsel.merch_stat.StatResponseData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.query.*;
import com.rbkmoney.magista.query.parser.QueryPart;
import com.rbkmoney.magista.util.TypeUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
        CompletableFuture<Map<String, String>> paymentCompletableFuture = CompletableFuture.supplyAsync(
                () -> getContext(context).getDao().getPaymentAccountingData(
                        getQueryParameters().getMerchantId(),
                        getQueryParameters().getContractId(),
                        getQueryParameters().getCurrencyCode(),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(getQueryParameters().getFromTime())),
                        TypeUtil.toLocalDateTime(getQueryParameters().getToTime())
                )
        );

        CompletableFuture<Map<String, String>> refundCompletableFuture = CompletableFuture.supplyAsync(
                () -> getContext(context).getDao().getRefundAccountingData(
                        getQueryParameters().getMerchantId(),
                        getQueryParameters().getContractId(),
                        getQueryParameters().getCurrencyCode(),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(getQueryParameters().getFromTime())),
                        TypeUtil.toLocalDateTime(getQueryParameters().getToTime())
                )
        );

        CompletableFuture<Map<String, String>> payoutCompletableFuture = CompletableFuture.supplyAsync(
                () -> getContext(context).getDao().getPayoutAccountingData(
                        getQueryParameters().getMerchantId(),
                        getQueryParameters().getContractId(),
                        getQueryParameters().getCurrencyCode(),
                        Optional.ofNullable(TypeUtil.toLocalDateTime(getQueryParameters().getFromTime())),
                        TypeUtil.toLocalDateTime(getQueryParameters().getToTime())
                )
        );

        CompletableFuture<Map<String, String>> combinedFuture = paymentCompletableFuture.thenCombineAsync(
                refundCompletableFuture, (payments, refunds) -> {
                    Map map = new HashMap(payments);
                    map.putAll(refunds);
                    return map;
                }
        ).thenCombineAsync(
                payoutCompletableFuture, (paymentsWithRefunds, payouts) -> {
                    Map map = new HashMap(paymentsWithRefunds);
                    map.putAll(payouts);
                    return map;
                }
        );
        try {
            List<Map<String, String>> result = Arrays.asList(combinedFuture.get());
            return new BaseQueryResult<>(() -> result.stream(), () -> new StatResponse(StatResponseData.records(result.stream().collect(Collectors.toList()))));
        } catch (ExecutionException ex) {
            throw new QueryExecutionException(ex.getCause());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ex);
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
