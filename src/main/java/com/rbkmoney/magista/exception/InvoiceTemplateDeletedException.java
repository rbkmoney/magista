package com.rbkmoney.magista.exception;

public class InvoiceTemplateDeletedException extends RuntimeException {

    public InvoiceTemplateDeletedException(Throwable cause) {
        super(cause);
    }

    protected InvoiceTemplateDeletedException(String message, Throwable cause, boolean enableSuppression,
                                              boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvoiceTemplateDeletedException() {
        super();
    }

    public InvoiceTemplateDeletedException(String message) {
        super(message);
    }

    public InvoiceTemplateDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

}
