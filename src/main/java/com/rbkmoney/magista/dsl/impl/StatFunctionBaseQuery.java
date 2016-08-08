package com.rbkmoney.magista.dsl.impl;

import java.util.Map;

/**
 * Created by vpankrashkin on 08.08.16.
 */
public abstract class StatFunctionBaseQuery extends ScopedFunctionBaseQuery {

    public static final String SPLIT_INTERVAL_PARAM = "split_interval";

    public StatFunctionBaseQuery(Map<String, Object> params, Class resultElementType) {
        super(params, resultElementType);
    }

    public int getSplitInterval() {
        return getIntParameter(SPLIT_INTERVAL_PARAM);
    }

    @Override
    protected boolean checkParams(Map<String, Object> params, boolean throwOnError) {
        return super.checkParams(params, throwOnError) && checkSplitInterval(throwOnError);
    }

    protected boolean checkSplitInterval(boolean throwOnError) {
        try {
            Integer val = getSplitInterval();
            if (val == null) {
                return checkParamsResult(throwOnError, true, SPLIT_INTERVAL_PARAM + " not found");
            }
            if (val <= 0) {
                return checkParamsResult(throwOnError, true, SPLIT_INTERVAL_PARAM + " is not valid");
            }
        } catch (Exception e) {
            return checkParamsResult(throwOnError, true, SPLIT_INTERVAL_PARAM + ": " + e.getMessage());
        }
        return true;
    }


}
