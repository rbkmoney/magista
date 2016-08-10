package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.magista.query.QueryContext;
import com.rbkmoney.magista.query.QueryResult;

import java.util.Map;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public class PaymentsFunction extends CursorScopedBaseFunction {
    public static final String PAYMENT_ID_PARAM = "payment_id";
    public static final String INVOICE_ID_PARAM = "invoice_id";
    public static final String PAYMENT_STATUS_PARAM = "payment_status";
    public static final String PAN_MASK_PARAM = "pan_mask";

    public static final String FUNC_NAME = "payments";

    public PaymentsFunction(Map<String, Object> params) {
        super(params, InvoicePayment.class, FUNC_NAME);
    }

    public String getInvoiceId() {
        return getStringParameter(INVOICE_ID_PARAM, false);
    }

    public String getPaymentId() {
        return getStringParameter(PAYMENT_ID_PARAM, false);
    }

    public String getPaymentStats() {
        return getStringParameter(PAYMENT_STATUS_PARAM, false);
    }

    public String getPanMask() {
        return getStringParameter(PAN_MASK_PARAM, false);
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
        /*if (!StringUtils.hasLength(getStringParameter(PAYMENT_ID_PARAM, false))) {
            checkParamsResult(throwOnError, true, PAYMENT_ID_PARAM + " not found");
        }
        if (!StringUtils.hasLength(getStringParameter(INVOICE_ID_PARAM, false))) {
            checkParamsResult(throwOnError, true, INVOICE_ID_PARAM + " not found");
        }*/

        /*if (!StringUtils.hasLength(getStringParameter(PAYMENT_STATUS_PARAM, false))) {
            checkParamsResult(throwOnError, true, PAYMENT_STATUS_PARAM + " not found");
        }*/
        String val = getStringParameter(PAN_MASK_PARAM, false);
        if (val != null && !val.matches("[\\d*]+")) {
            checkParamsResult(throwOnError, true, PAN_MASK_PARAM + " is not valid");
        }
        return true;
    }

    @Override
    protected boolean isTimeRequired() {
        return false;
    }
}
