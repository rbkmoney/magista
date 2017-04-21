package com.rbkmoney.magista.dsl;

/**
 * Created by vpankrashkin on 06.04.17.
 */
public class DSLBuildException extends DSLException {
    public DSLBuildException() {
        super();
    }

    public DSLBuildException(String message) {
        super(message);
    }

    public DSLBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public DSLBuildException(Throwable cause) {
        super(cause);
    }

    public DSLBuildException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
