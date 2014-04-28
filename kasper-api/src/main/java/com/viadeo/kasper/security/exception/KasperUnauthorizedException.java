package com.viadeo.kasper.security.exception;

import com.viadeo.kasper.CoreReasonCode;

public class KasperUnauthorizedException extends KasperSecurityException {

    private static final long serialVersionUID = 8706096438163444612L;

    public KasperUnauthorizedException(String message, CoreReasonCode coreReasonCode) {
        super(message, coreReasonCode);
    }

    public KasperUnauthorizedException(String message, Throwable cause, CoreReasonCode coreReasonCode) {
        super(message, cause, coreReasonCode);
    }
}
