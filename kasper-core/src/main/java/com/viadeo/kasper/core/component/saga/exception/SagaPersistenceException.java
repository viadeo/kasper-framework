// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.saga.exception;

public class SagaPersistenceException extends Exception {

    public SagaPersistenceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SagaPersistenceException(final String message) {
        super(message);
    }

}
