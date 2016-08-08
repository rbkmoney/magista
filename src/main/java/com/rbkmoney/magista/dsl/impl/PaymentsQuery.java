package com.rbkmoney.magista.dsl.impl;

import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.magista.dsl.QueryContext;
import com.rbkmoney.magista.dsl.QueryResult;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public class PaymentsQuery extends ScopedFunctionBaseQuery {
    public static final String PAYMENT_ID_PARAM = "payment_id";
    public static final String INVOICE_ID_PARAM = "invoice_id";
    public static final String PAYMENT_STATUS_PARAM = "payment_status";
    public static final String PAN_MASK_PARAM = "pan_mask";

    public PaymentsQuery(Map<String, Object> params) {
        super(params, InvoicePayment.class);
    }

    @Override
    public QueryResult compute(QueryContext context) {
        return null;
    }

    @Override
    protected boolean checkParams(Map<String, Object> params, boolean throwOnError) {
        if (!StringUtils.hasLength(getStringParameter(PAYMENT_ID_PARAM))) {
            checkParamsResult(throwOnError, true, PAYMENT_ID_PARAM +" not found");
        }
        if (!StringUtils.hasLength(getStringParameter(INVOICE_ID_PARAM))) {
            checkParamsResult(throwOnError, true, INVOICE_ID_PARAM +" not found");
        }

        if (!StringUtils.hasLength(getStringParameter(PAYMENT_STATUS_PARAM))) {
            checkParamsResult(throwOnError, true, PAYMENT_STATUS_PARAM +" not found");
        }
        String val = getStringParameter(PAN_MASK_PARAM);
        if (!StringUtils.hasLength(val)) {
            checkParamsResult(throwOnError, true, PAN_MASK_PARAM +" not found");
        } else {
            if (!val.matches("[/d*]+")) {
                checkParamsResult(throwOnError, true, PAN_MASK_PARAM+ " is not valid");
            }
        }
        return true;
    }

}
