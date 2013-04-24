/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client;

public class KasperClientException extends RuntimeException {
    private static final long serialVersionUID = 5299844829088913467L;

    public KasperClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public KasperClientException(String message) {
        super(message);
    }

    public KasperClientException(Throwable cause) {
        super(cause);
    }
}
