// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.exception;

import com.viadeo.kasper.CoreReasonCode;

public class KasperInvalidApplicationIdException extends KasperSecurityException {

    private static final long serialVersionUID = -6948184221968186520L;

    public KasperInvalidApplicationIdException(final String message, final CoreReasonCode coreReasonCode) {
        super(message, coreReasonCode);
    }

    public KasperInvalidApplicationIdException(final String message, final Throwable cause, final CoreReasonCode coreReasonCode) {
        super(message, cause, coreReasonCode);
    }

}
