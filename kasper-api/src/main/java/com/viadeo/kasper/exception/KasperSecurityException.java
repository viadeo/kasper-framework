package com.viadeo.kasper.exception;

public class KasperSecurityException extends KasperException {
    public KasperSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public KasperSecurityException(String message) {
        super(message);
    }

    public KasperSecurityException(Throwable cause) {
        super(cause);
    }
}
