package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.damsel.merch_stat.*;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.query.QueryExecutionException;
import com.rbkmoney.magista.query.QueryResult;
import com.rbkmoney.magista.repository.DaoException;
import javafx.util.Pair;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public class InvoicesFunction extends CursorScopedBaseFunction {
    public static final String INVOICE_ID_PARAM = "invoice_id";
    public static final String INVOICE_STATUS_PARAM = "invoice_status";

    public static final String FUNC_NAME = "invoices";

    public InvoicesFunction(Map<String, Object> params) {
        super(params, InvoicePayment.class, FUNC_NAME);
    }

    public String getInvoiceId() {
        return getStringParameter(INVOICE_ID_PARAM, false);
    }

    public String getInvocieStatus() {
        return getStringParameter(INVOICE_STATUS_PARAM, false);
    }

    private BiFunction<Stream<Invoice>, QueryResult, Supplier<StatResponse>> dataCollectorFunction = (st, qr) -> {
        StatResponseData statResponseData = StatResponseData.invoices(st.map(invoice -> new StatInvoice(invoice.getModel())).collect(Collectors.toList()));
        StatResponse statResponse = new StatResponse(statResponseData);
        statResponse.setTotalCount((qr).expectedTotalCount());
        return () -> statResponse;
    };

    @Override
    public QueryResult execute(FunctionQueryContext context) throws QueryExecutionException {
        try {
            Pair<Integer, Collection<Invoice>> result = context.getDao().getInvoices(
                    getMerchantId(),
                    getShopId(),
                    Optional.ofNullable(getInvoiceId()),
                    Optional.ofNullable(getInvocieStatus()),
                    Optional.ofNullable(Instant.from(getFromTime())),
                    Optional.ofNullable(Instant.from(getToTime())),
                    Optional.ofNullable(getSize() == null ? null : getSize()),
                    Optional.ofNullable(getFrom() == null ? null : getFrom())
            );
            return new BaseQueryResult<>(result.getKey(), () -> result.getValue().stream(), dataCollectorFunction);
        } catch (DaoException e) {
            throw new QueryExecutionException(e);
        }
    }

    @Override
    public QueryResult execute(QueryContext context) {
        return null;
    }

    @Override
    protected boolean checkParams(Map<String, Object> params, boolean throwOnError) {
        if (!super.checkParams(params, throwOnError)) {
            return false;
        }
        if (!StringUtils.hasLength(getStringParameter(INVOICE_ID_PARAM, false))) {
            checkParamsResult(throwOnError, true, INVOICE_ID_PARAM +" not found");
        }

        if (!StringUtils.hasLength(getStringParameter(INVOICE_STATUS_PARAM, false))) {
            checkParamsResult(throwOnError, true, INVOICE_STATUS_PARAM +" not found");
        }
        return true;
    }

    @Override
    protected boolean isTimeRequired() {
        return false;
    }
}
