package com.rbkmoney.magista.dsl;

/**
 * Created by vpankrashkin on 06.04.17.
 */
public class DSLInvalidException extends DSLException {
    public DSLInvalidException() {
    }

    public DSLInvalidException(String message) {
        super(message);
    }

    public DSLInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public DSLInvalidException(Throwable cause) {
        super(cause);
    }

    public DSLInvalidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
