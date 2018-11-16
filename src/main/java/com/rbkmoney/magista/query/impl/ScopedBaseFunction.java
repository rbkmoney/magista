package com.rbkmoney.magista.query.impl;

import com.rbkmoney.magista.dsl.BaseFunction;
import com.rbkmoney.magista.dsl.BaseQueryValidator;
import com.rbkmoney.magista.dsl.QueryParameters;

import java.util.List;
import java.util.Map;

import static com.rbkmoney.magista.query.impl.Parameters.*;

/**
 * Created by vpankrashkin on 08.08.16.
 */
public abstract class ScopedBaseFunction<T, CT> extends BaseFunction<T, CT> {

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

        public List<Integer> getShopCategoryIds() {
            return getArrayParameter(SHOP_CATEGORY_IDS_PARAM, false);
        }

    }

    public static class ScopedBaseValidator extends BaseQueryValidator {
        @Override
        public void validateParameters(QueryParameters parameters) throws IllegalArgumentException {
            super.validateParameters(parameters);
            ScopedBaseParameters scopedParameters = super.checkParamsType(parameters, ScopedBaseParameters.class);

            if (!hasLength(scopedParameters.getMerchantId()) && hasLength(scopedParameters.getShopId())) {
                checkParamsResult(true, SHOP_ID_PARAM, "when searching by shop_id, merchant_id must be set");
            }
        }

    }

    public static boolean hasLength(String str) {
        return str != null && !str.isEmpty();
    }

}
