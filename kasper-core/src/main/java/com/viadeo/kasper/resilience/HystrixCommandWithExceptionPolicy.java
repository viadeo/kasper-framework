package com.viadeo.kasper.resilience;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.viadeo.kasper.core.policy.ExceptionPolicy;
import com.viadeo.kasper.core.policy.HystrixInterceptorExceptionPolicy;

/**
 * Run an hystrix command with an Exception policy
 * @param <R>
 */
public abstract class HystrixCommandWithExceptionPolicy<R> extends HystrixCommand<R> {

    private static final ExceptionPolicy DEFAULT_POLICY = new HystrixInterceptorExceptionPolicy();

    private final ExceptionPolicy policy;

    protected HystrixCommandWithExceptionPolicy(HystrixCommandGroupKey group) {
        super(group);
        this.policy = DEFAULT_POLICY;
    }

    protected HystrixCommandWithExceptionPolicy(HystrixCommand.Setter setter) {
        super(setter);
        this.policy = new HystrixInterceptorExceptionPolicy();
    }

    protected HystrixCommandWithExceptionPolicy(HystrixCommandGroupKey group, ExceptionPolicy policy) {
        super(group);
        this.policy = policy;
    }

    protected HystrixCommandWithExceptionPolicy(HystrixCommand.Setter setter, ExceptionPolicy policy) {
        super(setter);
        this.policy = policy;
    }


    @Override
    public final R run() throws Exception {
        try {
            return runWithException();
        } catch (Exception e) {
            policy.manage(e); // send exception to calling method
            throw e; // go to fallback
        }
    }

    protected abstract R runWithException() throws Exception;
}
