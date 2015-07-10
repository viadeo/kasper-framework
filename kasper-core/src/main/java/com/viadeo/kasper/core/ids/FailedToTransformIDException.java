// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.ids;

public class FailedToTransformIDException extends RuntimeException {

    public FailedToTransformIDException(final String message) {
        super(message);
    }

    public FailedToTransformIDException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
