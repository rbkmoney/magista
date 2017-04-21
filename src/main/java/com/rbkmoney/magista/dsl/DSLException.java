package com.rbkmoney.magista.dsl;

/**
 * Created by vpankrashkin on 06.04.17.
 */
public class DSLException extends RuntimeException {
    public DSLException() {
    }

    public DSLException(String message) {
        super(message);
    }

    public DSLException(String message, Throwable cause) {
        super(message, cause);
    }

    public DSLException(Throwable cause) {
        super(cause);
    }

    public DSLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
