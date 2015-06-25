// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.exception;

public class SagaExecutionException extends RuntimeException {

    public SagaExecutionException(final String message) {
        super(message);
    }

    public SagaExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
