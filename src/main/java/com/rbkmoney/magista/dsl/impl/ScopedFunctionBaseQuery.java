package com.rbkmoney.magista.dsl.impl;

import org.springframework.util.StringUtils;

import java.time.temporal.TemporalAccessor;
import java.util.Map;

/**
 * Created by vpankrashkin on 08.08.16.
 */
public abstract class ScopedFunctionBaseQuery extends FunctionBaseQuery {
    public static final String MERCHANT_ID_PARAM = "merchant_id";
    public static final String SHOP_ID_PARAM = "shop_id";
    public static final String FROM_TIME_PARAM = "from_time";
    public static final String TO_TIME_PARAM = "to_time";

    public ScopedFunctionBaseQuery(Map<String, Object> params, Class resultElementType) {
        super(params, resultElementType);
    }

    public TemporalAccessor getFromTime() {
        return getTimeParameter(FROM_TIME_PARAM);
    }

    public TemporalAccessor getToTIme() {
        return getTimeParameter(TO_TIME_PARAM);
    }

    public String getMerchantId() {
        return getStringParameter(MERCHANT_ID_PARAM);
    }

    public String getShopId() {
        return getStringParameter(SHOP_ID_PARAM);
    }

    @Override
    protected boolean checkParams(Map<String, Object> params, boolean throwOnError) {
        return  super.checkParams(params, throwOnError) && checkMerchantId(throwOnError) && checkShopId(throwOnError) && checkFromTime(throwOnError) && checkToTime(throwOnError);
    }

    private boolean checkMerchantId(boolean throwOnError) {
        if (!StringUtils.hasLength(getStringParameter(MERCHANT_ID_PARAM))) {
            return checkParamsResult(throwOnError, true, MERCHANT_ID_PARAM + " not found");
        }
        return true;
    }

    private boolean checkShopId(boolean throwOnError) {
        if (!StringUtils.hasLength(getStringParameter(SHOP_ID_PARAM))) {
            return checkParamsResult(throwOnError, true, SHOP_ID_PARAM + " not found");
        }
        return true;
    }

    private boolean checkFromTime(boolean throwOnError) {
        return checkTime(FROM_TIME_PARAM, throwOnError);
    }

    private boolean checkToTime(boolean throwOnError) {
        return checkTime(TO_TIME_PARAM, throwOnError);
    }

    private boolean checkTime(String key, boolean throwOnError) {
        try {
            TemporalAccessor val = getTimeParameter(key);
            if (val == null) {
                return checkParamsResult(throwOnError, true, key + " not found");
            }
            return true;
        } catch (Exception e) {
            return checkParamsResult(throwOnError, true, key + ": " + e.getMessage());
        }
    }

}
