package com.rbkmoney.magista.query.impl;

import org.springframework.util.StringUtils;

import java.time.temporal.TemporalAccessor;
import java.util.Map;

/**
 * Created by vpankrashkin on 08.08.16.
 */
public abstract class ScopedBaseFunction extends BaseFunction {
    public static final String MERCHANT_ID_PARAM = "merchant_id";
    public static final String SHOP_ID_PARAM = "shop_id";
    public static final String FROM_TIME_PARAM = "from_time";
    public static final String TO_TIME_PARAM = "to_time";

    public ScopedBaseFunction(Map<String, Object> params, Class resultElementType, String name) {
        super(params, resultElementType, name);
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

    @Override
    protected boolean checkParams(Map<String, Object> params, boolean throwOnError) {
        return super.checkParams(params, throwOnError) && checkMerchantId(throwOnError) && checkShopId(throwOnError) && checkTimeParameters(throwOnError);
    }

    protected boolean checkTimeParameters(boolean throwOnError) {
        return checkFromTime(throwOnError) && checkToTime(throwOnError);
    }

    private boolean checkMerchantId(boolean throwOnError) {
        if (!StringUtils.hasLength(getStringParameter(MERCHANT_ID_PARAM, false))) {
            return checkParamsResult(throwOnError, true, MERCHANT_ID_PARAM + " not found");
        }
        return true;
    }

    private boolean checkShopId(boolean throwOnError) {
        if (!StringUtils.hasLength(getStringParameter(SHOP_ID_PARAM, false))) {
            return checkParamsResult(throwOnError, true, SHOP_ID_PARAM + " not found");
        }
        return true;
    }

    private boolean checkFromTime(boolean throwOnError) {
        return checkTime(FROM_TIME_PARAM, isTimeRequired(), throwOnError);
    }

    private boolean checkToTime(boolean throwOnError) {
        return checkTime(TO_TIME_PARAM, isTimeRequired(), throwOnError);
    }

    protected boolean isTimeRequired() {
        return true;
    }

    protected boolean checkTime(String key, boolean required, boolean throwOnError) {
        TemporalAccessor val;
        try {
            val = getTimeParameter(key, false);
        } catch (Exception e) {
            return checkParamsResult(throwOnError, true, key + ": " + e.getMessage());
        }
        if (val == null && required) {
            return checkParamsResult(throwOnError, true, key + " not found");
        }
        return true;

    }

}
