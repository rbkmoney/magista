package com.rbkmoney.magista.dsl.impl;

import java.util.Map;

/**
 * Created by vpankrashkin on 08.08.16.
 */
public abstract class CursorScopedBaseFunction extends ScopedBaseFunction {
    public static final String FROM_PARAMETER = "from";
    public static final String SIZE_PARAMETER = "size";

    public CursorScopedBaseFunction(Map<String, Object> params, Class resultElementType, String name) {
        super(params, resultElementType, name);
    }

    public Integer getFrom() {
        return getIntParameter(FROM_PARAMETER, true);
    }

    public Integer getSize() {
        return getIntParameter(SIZE_PARAMETER, true);
    }

    private boolean checkFrom(boolean throwOnError) {
        Integer val;
        try {
            val = getFrom();
        } catch (Exception e) {
            return checkParamsResult(throwOnError, true, FROM_PARAMETER + " :" + e.getMessage());
        }

        if (val != null && val < 0) {
            return checkParamsResult(throwOnError, true, FROM_PARAMETER + " is not valid");
        }
        return true;
    }

    private boolean checkSize(boolean throwOnError) {
        Integer val;
        try {
            val = getSize();
        } catch (Exception e) {
            return checkParamsResult(throwOnError, true, SIZE_PARAMETER + " :" + e.getMessage());
        }

        if (val != null && val <= 0) {
            return checkParamsResult(throwOnError, true, SIZE_PARAMETER + " is not valid");
        }
        return true;
    }

    @Override
    protected boolean checkParams(Map<String, Object> params, boolean throwOnError) {
        return super.checkParams(params, throwOnError) && checkFrom(throwOnError) && checkSize(throwOnError);
    }
}
