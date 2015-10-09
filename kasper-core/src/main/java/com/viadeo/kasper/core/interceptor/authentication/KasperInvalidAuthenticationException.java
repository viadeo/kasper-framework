// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.authentication;

import com.viadeo.kasper.api.exception.KasperSecurityException;
import com.viadeo.kasper.api.response.CoreReasonCode;

public class KasperInvalidAuthenticationException extends KasperSecurityException {

    public KasperInvalidAuthenticationException(String message, CoreReasonCode coreReasonCode) {
        super(message, coreReasonCode);
    }

    public KasperInvalidAuthenticationException(String message, Throwable cause, CoreReasonCode coreReasonCode) {
        super(message, cause, coreReasonCode);
    }
}
