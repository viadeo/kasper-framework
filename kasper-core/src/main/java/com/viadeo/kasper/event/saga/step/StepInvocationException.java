// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

public class StepInvocationException extends RuntimeException {

    public StepInvocationException(String message) {
        super(message);
    }

    public StepInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
