package com.rbkmoney.magista.exception;

public class InvoiceTemplateAlreadyDeletedException extends RuntimeException {

    public InvoiceTemplateAlreadyDeletedException(Throwable cause) {
        super(cause);
    }

    protected InvoiceTemplateAlreadyDeletedException(String message, Throwable cause, boolean enableSuppression,
                                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvoiceTemplateAlreadyDeletedException() {
        super();
    }

    public InvoiceTemplateAlreadyDeletedException(String message) {
        super(message);
    }

    public InvoiceTemplateAlreadyDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

}
