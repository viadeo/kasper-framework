// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exception;

/**
 * The base Kasper runtime exception, KasperQueryException and KasperCommandException
 * should be preferred to this one as they are more specific.
 */
public class KasperCheckedException extends Exception {

    private static final long serialVersionUID = 4439295425026389937L;

    public KasperCheckedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public KasperCheckedException(final String message) {
        super(message);
    }

    public KasperCheckedException(final Throwable cause) {
        super(cause);
    }

}
