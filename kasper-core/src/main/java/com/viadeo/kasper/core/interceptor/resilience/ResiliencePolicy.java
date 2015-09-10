// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.resilience;

import com.google.common.collect.Lists;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.viadeo.kasper.api.exception.KasperSecurityException;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;

import java.util.List;

/**
 * This policy rethrow exceptions throws by interceptors for further processing.
 * Avoid calling fallback on validation exception.
 */
public class ResiliencePolicy {

    /**
     * Manage an exception in order to distinguish an error or failure.
     *
     * @param exception a caught exception
     * @throws HystrixBadRequestException only it the specified exception represents an error
     */
    public void manage(final Throwable exception) throws RuntimeException {
        for (final Class<?> interceptorsException : getExceptionRepresentingAnError()) {
            if (interceptorsException.isAssignableFrom(exception.getClass())) {
                throw new HystrixBadRequestException(exception.getMessage(), exception);
            }
        }
    }

    /**
     * @return a list of exception representing an error
     */
    @SuppressWarnings("unchecked")
    protected List<Class<? extends Exception>> getExceptionRepresentingAnError() {
        return Lists.<Class<? extends Exception>>newArrayList(
                JSR303ViolationException.class,
                KasperSecurityException.class
        );
    }

}
