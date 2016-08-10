package com.rbkmoney.magista.query.impl;

import java.util.Map;

/**
 * Created by vpankrashkin on 08.08.16.
 */
public abstract class StatBaseFunction extends ScopedBaseFunction {

    public static final String SPLIT_INTERVAL_PARAM = "split_interval";

    public StatBaseFunction(Map<String, Object> params, Class resultElementType, String name) {
        super(params, resultElementType, name);
    }

    public int getSplitInterval() {
        return getIntParameter(SPLIT_INTERVAL_PARAM, false);
    }

    @Override
    protected boolean checkParams(Map<String, Object> params, boolean throwOnError) {
        return super.checkParams(params, throwOnError) && checkSplitInterval(throwOnError);
    }

    protected boolean checkSplitInterval(boolean throwOnError) {
        Integer val;
        try {
            val = getSplitInterval();
        } catch (Exception e) {
            return checkParamsResult(throwOnError, true, SPLIT_INTERVAL_PARAM + ": " + e.getMessage());
        }
        if (val == null) {
            return checkParamsResult(throwOnError, true, SPLIT_INTERVAL_PARAM + " not found");
        }
        if (val <= 0) {
            return checkParamsResult(throwOnError, true, SPLIT_INTERVAL_PARAM + " is not valid");
        }

        return true;
    }


}
