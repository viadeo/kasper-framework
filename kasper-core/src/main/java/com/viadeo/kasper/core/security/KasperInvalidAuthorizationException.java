// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.security;

import com.viadeo.kasper.api.exception.KasperSecurityException;
import com.viadeo.kasper.api.response.CoreReasonCode;

public class KasperInvalidAuthorizationException extends KasperSecurityException {

    public KasperInvalidAuthorizationException(String message, CoreReasonCode coreReasonCode) {
        super(message, coreReasonCode);
    }

    public KasperInvalidAuthorizationException(String message, Throwable cause, CoreReasonCode coreReasonCode) {
        super(message, cause, coreReasonCode);
    }
}
