// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.exceptions;

import com.viadeo.kasper.exception.KasperException;

/**
 * The base Kasper query exception
 */
public class KasperQueryException extends KasperException {

    private static final long serialVersionUID = 4429295125026389937L;

    // ------------------------------------------------------------------------

    public KasperQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public KasperQueryException(String message) {
        super(message);
    }

    public KasperQueryException(Throwable cause) {
        super(cause);
    }
}
