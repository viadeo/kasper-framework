// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security;

import com.viadeo.kasper.CoreReasonCode;

public class KasperMissingSecurityTokenException extends KasperSecurityException {

    private static final long serialVersionUID = -2521256807381853907L;

    public KasperMissingSecurityTokenException(final String message, final CoreReasonCode coreReasonCode) {
        super(message, coreReasonCode);
    }

    public KasperMissingSecurityTokenException(final String message, final Throwable cause, final CoreReasonCode coreReasonCode) {
        super(message, cause, coreReasonCode);
    }
}
