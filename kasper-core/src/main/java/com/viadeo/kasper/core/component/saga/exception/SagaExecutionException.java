// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.saga.exception;

/**
 * Exception indicating that an error has occurred while executing a Saga.
 */
public class SagaExecutionException extends RuntimeException {

    public SagaExecutionException(final String message) {
        super(message);
    }

    public SagaExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
