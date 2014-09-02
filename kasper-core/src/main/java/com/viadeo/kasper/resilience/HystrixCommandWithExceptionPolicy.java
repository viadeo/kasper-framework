// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.resilience;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.viadeo.kasper.core.policy.ExceptionPolicy;
import com.viadeo.kasper.core.policy.HystrixInterceptorExceptionPolicy;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Run an hystrix command with an Exception policy
 * @param <R>
 */
public abstract class HystrixCommandWithExceptionPolicy<R> extends HystrixCommand<R> {

    private static final ExceptionPolicy DEFAULT_POLICY = new HystrixInterceptorExceptionPolicy();

    private final ExceptionPolicy policy;

    // ------------------------------------------------------------------------

    protected HystrixCommandWithExceptionPolicy(final HystrixCommandGroupKey group) {
        super(group);
        this.policy = DEFAULT_POLICY;
    }

    protected HystrixCommandWithExceptionPolicy(final HystrixCommand.Setter setter) {
        super(setter);
        this.policy = new HystrixInterceptorExceptionPolicy();
    }

    protected HystrixCommandWithExceptionPolicy(final HystrixCommandGroupKey group, final ExceptionPolicy policy) {
        super(group);
        this.policy = checkNotNull(policy);
    }

    protected HystrixCommandWithExceptionPolicy(final HystrixCommand.Setter setter, final ExceptionPolicy policy) {
        super(setter);
        this.policy = checkNotNull(policy);
    }

    // ------------------------------------------------------------------------

    @Override
    public final R run() throws Exception {
        try {
            return runWithException();
        } catch (final Exception e) {
            policy.manage(e); // send exception to calling method
            throw e; // go to fallback
        }
    }

    protected abstract R runWithException() throws Exception;

}
