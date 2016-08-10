package com.rbkmoney.magista.provider;

/**
 * Created by tolkonepiu on 08.08.16.
 */
public class ProviderException extends RuntimeException {

    public ProviderException() {
        super();
    }

    public ProviderException(String message) {
        super(message);
    }

    public ProviderException(String message, Throwable cause) {
        super(message, cause);
    }

}
