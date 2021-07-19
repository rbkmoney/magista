package com.rbkmoney.magista.exception;

public class InvoiceTemplateCreatedException extends RuntimeException {

    public InvoiceTemplateCreatedException(Throwable cause) {
        super(cause);
    }

    protected InvoiceTemplateCreatedException(String message, Throwable cause, boolean enableSuppression,
                                              boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvoiceTemplateCreatedException() {
        super();
    }

    public InvoiceTemplateCreatedException(String message) {
        super(message);
    }

    public InvoiceTemplateCreatedException(String message, Throwable cause) {
        super(message, cause);
    }

}
