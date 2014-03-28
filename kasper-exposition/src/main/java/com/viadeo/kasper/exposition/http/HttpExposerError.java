// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

public class HttpExposerError extends Error {

    private static final long serialVersionUID = 4344417920759385284L;

    public HttpExposerError(final String message) {
        super(message);
    }
}
