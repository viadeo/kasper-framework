// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.policy;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.viadeo.kasper.security.exception.KasperSecurityException;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;

/**
 * This policy rethrow exceptions throws by interceptors for further processing.
 * Avoid calling fallback on validation exception.
 */
public class HystrixInterceptorExceptionPolicy implements ExceptionPolicy {

    private static final Class[] interceptorsExceptions = {
            JSR303ViolationException.class,
            KasperSecurityException.class
    };

    // ------------------------------------------------------------------------

    @Override
    public void manage(final Throwable exception) throws RuntimeException {
        for (final Class interceptorsException : interceptorsExceptions) {
            if (exception.getClass().isAssignableFrom(interceptorsException)) {
                throw new HystrixBadRequestException(exception.getMessage(), exception);
            }
        }
    }

}
