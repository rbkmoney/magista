package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.query.QueryResult;
import org.springframework.util.StringUtils;

import java.util.Map;

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
