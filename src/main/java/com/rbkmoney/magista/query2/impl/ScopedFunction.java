package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.BaseFunction;
import com.rbkmoney.magista.query2.BaseQueryValidator;
import com.rbkmoney.magista.query2.Query;
import com.rbkmoney.magista.query2.QueryParameters;
import org.springframework.util.StringUtils;

import java.util.Map;

import static com.rbkmoney.magista.query2.impl.Parameters.MERCHANT_ID_PARAM;
import static com.rbkmoney.magista.query2.impl.Parameters.SHOP_ID_PARAM;

/**
 * Created by vpankrashkin on 08.08.16.
 */
public abstract class ScopedFunction extends BaseFunction {

    private final ScopedParameters parameters;

    public ScopedFunction(QueryParameters params, Query parentQuery, String name) {
        super(params, parentQuery, name);
        this.parameters = new ScopedParameters(params, extractParameters(parentQuery));

    }

    @Override
    public ScopedParameters getQueryParameters() {
        return parameters;
    }

    public static class ScopedParameters extends QueryParameters {

        public ScopedParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public ScopedParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public String getMerchantId() {
            return getStringParameter(MERCHANT_ID_PARAM, false);
        }

        public String getShopId() {
            return getStringParameter(SHOP_ID_PARAM, false);
        }

    }

    public static class ScopedValidator extends BaseQueryValidator {
        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            ScopedParameters scopedParameters = super.checkParamsType(parameters, ScopedParameters.class);

            if (!StringUtils.hasLength(scopedParameters.getMerchantId())) {
                checkParamsResult(true, MERCHANT_ID_PARAM, RootQuery.RootValidator.DEFAULT_MSG_STRING);
            }

            if (!StringUtils.hasLength(scopedParameters.getShopId())) {
                checkParamsResult(true, SHOP_ID_PARAM,  RootQuery.RootValidator.DEFAULT_MSG_STRING);
            }

        }

    }

}
