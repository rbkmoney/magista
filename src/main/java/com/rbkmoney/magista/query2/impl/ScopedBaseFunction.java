package com.rbkmoney.magista.query2.impl;

import com.rbkmoney.magista.query2.BaseFunction;
import com.rbkmoney.magista.query2.BaseQueryValidator;
import com.rbkmoney.magista.query2.QueryParameters;
import org.springframework.util.StringUtils;

import java.util.Map;

import static com.rbkmoney.magista.query2.impl.Parameters.MERCHANT_ID_PARAM;
import static com.rbkmoney.magista.query2.impl.Parameters.SHOP_ID_PARAM;

/**
 * Created by vpankrashkin on 08.08.16.
 */
public abstract class ScopedBaseFunction extends BaseFunction {

    public ScopedBaseFunction(Object descriptor, QueryParameters params, String name) {
        super(descriptor, params, name);

    }

    @Override
    public ScopedBaseParameters getQueryParameters() {
        return (ScopedBaseParameters) super.getQueryParameters();
    }

    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return new ScopedBaseParameters(parameters, derivedParameters);
    }

    public static class ScopedBaseParameters extends QueryParameters {

        public ScopedBaseParameters(Map<String, Object> parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public ScopedBaseParameters(QueryParameters parameters, QueryParameters derivedParameters) {
            super(parameters, derivedParameters);
        }

        public String getMerchantId() {
            return getStringParameter(MERCHANT_ID_PARAM, false);
        }

        public String getShopId() {
            return getStringParameter(SHOP_ID_PARAM, false);
        }

    }

    public static class ScopedBaseValidator extends BaseQueryValidator {
        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            ScopedBaseParameters scopedParameters = super.checkParamsType(parameters, ScopedBaseParameters.class);

            if (!StringUtils.hasLength(scopedParameters.getMerchantId())) {
                checkParamsResult(true, MERCHANT_ID_PARAM, RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
            }

            if (!StringUtils.hasLength(scopedParameters.getShopId())) {
                checkParamsResult(true, SHOP_ID_PARAM,  RootQuery.RootValidator.DEFAULT_ERR_MSG_STRING);
            }

        }

    }

}
