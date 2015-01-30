package com.viadeo.kasper.context;

import com.viadeo.kasper.exception.KasperException;

public class ContextValidationException extends KasperException {

    public ContextValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContextValidationException(String message) {
        super(message);
    }

    public ContextValidationException(Throwable cause) {
        super(cause);
    }
}
