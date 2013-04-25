// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

import com.viadeo.kasper.exception.KasperRuntimeException;

public class KasperClientException extends KasperRuntimeException {
    private static final long serialVersionUID = 5299844829088913467L;

    public KasperClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public KasperClientException(final String message) {
        super(message);
    }

    public KasperClientException(final Throwable cause) {
        super(cause);
    }
    
}
