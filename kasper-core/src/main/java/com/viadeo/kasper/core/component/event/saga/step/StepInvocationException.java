// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.step;

public class StepInvocationException extends RuntimeException {

    public StepInvocationException(final String message) {
        super(message);
    }

    public StepInvocationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
