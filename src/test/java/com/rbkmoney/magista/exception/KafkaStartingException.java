package com.rbkmoney.magista.exception;

public class KafkaStartingException extends RuntimeException {

    public KafkaStartingException() {
        super();
    }

    public KafkaStartingException(String message) {
        super(message);
    }

    public KafkaStartingException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaStartingException(Throwable cause) {
        super(cause);
    }

    protected KafkaStartingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}