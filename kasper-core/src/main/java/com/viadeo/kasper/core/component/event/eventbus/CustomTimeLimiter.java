package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.google.common.util.concurrent.Uninterruptibles;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * based on com.google.common.util.concurrent.SimpleTimeLimiter
 */
public class CustomTimeLimiter implements TimeLimiter {

    private final ExecutorService executor;

    public CustomTimeLimiter() {
        this(Executors.newCachedThreadPool());
    }

    public CustomTimeLimiter(final ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public <T> T newProxy(
            final T target,
            final Class<T> interfaceType,
            final long timeoutDuration,
            final TimeUnit timeoutUnit
    ) {
        checkNotNull(target);
        checkNotNull(interfaceType);
        checkNotNull(timeoutUnit);
        checkArgument(timeoutDuration > 0, "bad timeout: " + timeoutDuration);
        checkArgument(interfaceType.isInterface(), "interfaceType must be an interface type");

        InvocationHandler handler = new CustomInvocationHandler() {

            @Override
            public Class<?> getTargetClass() {
                return target.getClass();
            }

            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

                final Callable<Object> callable = new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        try {
                            return method.invoke(target, args);
                        } catch (InvocationTargetException e) {
                            throw throwCause(e, false);
                        }
                    }
                };

                final Set<Method> interruptibleMethods = findInterruptibleMethods(interfaceType);

                return callWithTimeout(
                        callable,
                        timeoutDuration,
                        timeoutUnit,
                        interruptibleMethods.contains(method));
            }
        };

        Object object = Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class<?>[]{interfaceType}, handler);
        return interfaceType.cast(object);
    }

    @Override
    public <T> T callWithTimeout(
            final Callable<T> callable,
            final long timeoutDuration,
            final TimeUnit timeoutUnit,
            final boolean interruptible
    ) throws Exception {

        checkNotNull(callable);
        checkNotNull(timeoutUnit);
        checkArgument(timeoutDuration > 0, "timeout must be positive: %s",
                timeoutDuration);
        Future<T> future = executor.submit(callable);
        try {
            if (interruptible) {
                try {
                    return future.get(timeoutDuration, timeoutUnit);
                } catch (InterruptedException e) {
                    future.cancel(true);
                    throw e;
                }
            } else {
                return Uninterruptibles.getUninterruptibly(future,
                        timeoutDuration, timeoutUnit);
            }
        } catch (ExecutionException e) {
            throw throwCause(e, true);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new UncheckedTimeoutException(e);
        }
    }

    private static Exception throwCause(Exception e, boolean combineStackTraces)
            throws Exception {
        Throwable cause = e.getCause();
        if (cause == null) {
            throw e;
        }
        if (combineStackTraces) {
            StackTraceElement[] combined = ObjectArrays.concat(cause.getStackTrace(),
                    e.getStackTrace(), StackTraceElement.class);
            cause.setStackTrace(combined);
        }
        if (cause instanceof Exception) {
            throw (Exception) cause;
        }
        if (cause instanceof Error) {
            throw (Error) cause;
        }
        // The cause is a weird kind of Throwable, so throw the outer exception.
        throw e;
    }

    private static Set<Method> findInterruptibleMethods(Class<?> interfaceType) {
        Set<Method> set = Sets.newHashSet();
        for (Method m : interfaceType.getMethods()) {
            if (declaresInterruptedEx(m)) {
                set.add(m);
            }
        }
        return set;
    }

    private static boolean declaresInterruptedEx(Method method) {
        for (Class<?> exType : method.getExceptionTypes()) {
            // debate: == or isAssignableFrom?
            if (exType == InterruptedException.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * Implements this class in order to retrieve the target behind the proxy
     */
    public interface CustomInvocationHandler extends InvocationHandler {

        /**
         * Return the target class behind the implementing object
         * (typically a proxy configuration or an actual proxy).
         * @return the target Class, or {@code null} if not known
         */
        Class<?> getTargetClass();
    }
}
