package com.rbkmoney.magista.dsl.impl;

import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.magista.dsl.QueryContext;
import com.rbkmoney.magista.dsl.QueryResult;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public class InvoicesQuery extends ScopedFunctionBaseQuery {
    public static final String INVOICE_ID_PARAM = "invoice_id";
    public static final String INVOICE_STATUS_PARAM = "invoice_status";

    public InvoicesQuery(Map<String, Object> params) {
        super(params, InvoicePayment.class);
    }

    @Override
    public QueryResult compute(QueryContext context) {
        return null;
    }

    @Override
    protected boolean checkParams(Map<String, Object> params, boolean throwOnError) {
        if (!StringUtils.hasLength(getStringParameter(INVOICE_ID_PARAM))) {
            checkParamsResult(throwOnError, true, INVOICE_ID_PARAM +" not found");
        }

        if (!StringUtils.hasLength(getStringParameter(INVOICE_STATUS_PARAM))) {
            checkParamsResult(throwOnError, true, INVOICE_STATUS_PARAM +" not found");
        }
        return true;
    }

}
