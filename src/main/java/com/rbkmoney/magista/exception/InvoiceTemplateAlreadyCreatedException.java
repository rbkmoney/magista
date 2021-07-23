package com.rbkmoney.magista.exception;

public class InvoiceTemplateAlreadyCreatedException extends RuntimeException {

    public InvoiceTemplateAlreadyCreatedException(Throwable cause) {
        super(cause);
    }

    protected InvoiceTemplateAlreadyCreatedException(String message, Throwable cause, boolean enableSuppression,
                                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvoiceTemplateAlreadyCreatedException() {
        super();
    }

    public InvoiceTemplateAlreadyCreatedException(String message) {
        super(message);
    }

    public InvoiceTemplateAlreadyCreatedException(String message, Throwable cause) {
        super(message, cause);
    }

}
