package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.Query;
import com.rbkmoney.magista.query2.QueryParameters;

import java.time.temporal.TemporalAccessor;
import java.util.Map;

import static com.rbkmoney.magista.query2.impl.Parameters.*;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public class InvoicesFunction extends PagedDataFunction {

    public static final String FUNC_NAME = "invoices";

    private final InvoicesParameters parameters;

    public InvoicesFunction(QueryParameters params, Query parentQuery) {
        super(params, parentQuery, FUNC_NAME);
        this.parameters = new InvoicesParameters(params, parentQuery.getQueryParameters());
    }

    @Override
    public InvoicesParameters getQueryParameters() {
        return parameters;
    }


    public static class InvoicesParameters extends PagedDataParameters {

        public InvoicesParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public InvoicesParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public String getInvoiceId() {
            return getStringParameter(INVOICE_ID_PARAM, false);
        }

        public String getInvoiceStatus() {
            return getStringParameter(INVOICE_STATUS_PARAM, false);
        }

        public TemporalAccessor getFromTime() {
            return getTimeParameter(FROM_TIME_PARAM, false);
        }

        public TemporalAccessor getToTime() {
            return getTimeParameter(TO_TIME_PARAM, false);
        }

        public String getMerchantId() {
            return getStringParameter(MERCHANT_ID_PARAM, false);
        }

        public String getShopId() {
            return getStringParameter(SHOP_ID_PARAM, false);
        }
    }
}
