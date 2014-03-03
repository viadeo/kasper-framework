// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security;

import com.viadeo.kasper.CoreReasonCode;

public class KasperMissingIpAddressException extends KasperSecurityException {

    private static final long serialVersionUID = -2421256807381853942L;

    public KasperMissingIpAddressException(final String message, final CoreReasonCode coreReasonCode) {
        super(message, coreReasonCode);
    }

    public KasperMissingIpAddressException(final String message, final Throwable cause, final CoreReasonCode coreReasonCode) {
        super(message, cause, coreReasonCode);
    }
}
