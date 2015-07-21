package com.viadeo.kasper.api.exception;

import com.viadeo.kasper.api.exception.KasperException;

public class KasperEventException extends KasperException {

    public KasperEventException(String message, Throwable cause) {
        super(message, cause);
    }

    public KasperEventException(String message) {
        super(message);
    }

    public KasperEventException(Throwable cause) {
        super(cause);
    }
}
