// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.policy;

public interface ExceptionPolicy {

    /**
     * Manage an exception.
     * This method should implements a policy against the exception provided(parameter)
     * It can throw RuntimeException to upper layer (calling method) or manage the exception and fail silently if needed.
     * @param exception
     * @throws java.lang.RuntimeException only
     */
    void manage(Throwable exception) throws RuntimeException;

}

