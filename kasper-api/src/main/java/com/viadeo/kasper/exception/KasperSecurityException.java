// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exception;

public class KasperSecurityException extends KasperException {

    public KasperSecurityException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public KasperSecurityException(final String message) {
        super(message);
    }

    public KasperSecurityException(final Throwable cause) {
        super(cause);
    }

}
