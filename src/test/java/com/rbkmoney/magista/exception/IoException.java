package com.rbkmoney.magista.exception;

public class IoException extends RuntimeException {
    public IoException() {
        super();
    }

    public IoException(String message) {
        super(message);
    }

    public IoException(String message, Throwable cause) {
        super(message, cause);
    }

    public IoException(Throwable cause) {
        super(cause);
    }

    protected IoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
