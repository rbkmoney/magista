package com.rbkmoney.magista.exception;

public class NoSuchFileException extends RuntimeException {
    public NoSuchFileException() {
        super();
    }

    public NoSuchFileException(String message) {
        super(message);
    }

    public NoSuchFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchFileException(Throwable cause) {
        super(cause);
    }

    protected NoSuchFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
