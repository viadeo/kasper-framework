package com.viadeo.kasper.security;

import com.viadeo.kasper.CoreReasonCode;

public class KasperMissingSecurityTokenException extends KasperSecurityException {

    private static final long serialVersionUID = -2521256807381853907L;

    public KasperMissingSecurityTokenException(String message, CoreReasonCode coreReasonCode) {
        super(message, coreReasonCode);
    }

    public KasperMissingSecurityTokenException(String message, Throwable cause, CoreReasonCode coreReasonCode) {
        super(message, cause, coreReasonCode);
    }
}
