package com.rbkmoney.magista.exception;

/**
 * Created by tolkonepiu on 30/06/2017.
 */
public class AdjustmentException extends RuntimeException {

    public AdjustmentException() {
    }

    public AdjustmentException(String message) {
        super(message);
    }

    public AdjustmentException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdjustmentException(Throwable cause) {
        super(cause);
    }

    public AdjustmentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
