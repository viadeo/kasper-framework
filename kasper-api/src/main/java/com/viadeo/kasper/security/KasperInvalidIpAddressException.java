// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security;

import com.viadeo.kasper.CoreReasonCode;

public class KasperInvalidIpAddressException extends KasperSecurityException {

    private static final long serialVersionUID = -6948187421968186520L;

    public KasperInvalidIpAddressException(String message, CoreReasonCode coreReasonCode) {
        super(message, coreReasonCode);
    }

    public KasperInvalidIpAddressException(String message, Throwable cause, CoreReasonCode coreReasonCode) {
        super(message, cause, coreReasonCode);
    }

}
