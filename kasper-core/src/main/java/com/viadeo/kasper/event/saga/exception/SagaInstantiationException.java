// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.exception;

public class SagaInstantiationException extends RuntimeException {

    public SagaInstantiationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
