package com.rbkmoney.magista.exception;

public class WrongParametersException extends RuntimeException {

    public WrongParametersException(Throwable cause) {
        super(cause);
    }

    protected WrongParametersException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public WrongParametersException() {
        super();
    }

    public WrongParametersException(String message) {
        super(message);
    }

    public WrongParametersException(String message, Throwable cause) {
        super(message, cause);
    }

}
