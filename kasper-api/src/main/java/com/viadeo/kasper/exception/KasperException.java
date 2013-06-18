// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exception;

/**
 * The base Kasper runtime exception, KasperQueryException and KasperCommandException should be preferred to this one as
 * they are more specific.
 */
public class KasperException extends RuntimeException {

    private static final long serialVersionUID = 4439295125026389937L;

    public KasperException(String message, Throwable cause) {
        super(message, cause);
    }

    public KasperException(String message) {
        super(message);
    }

    public KasperException(Throwable cause) {
        super(cause);
    }
}
