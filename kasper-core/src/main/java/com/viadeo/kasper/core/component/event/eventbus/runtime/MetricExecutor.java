package com.viadeo.kasper.core.component.event.eventbus.runtime;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static com.codahale.metrics.MetricRegistry.name;
import static com.google.common.base.Preconditions.checkNotNull;

public class MetricExecutor implements ExecutorService {

    private final String name;
    private final MetricRegistry metricRegistry;
    private final ThreadPoolExecutor delegate;

    public MetricExecutor(final String name, final MetricRegistry metricRegistry, final ThreadPoolExecutor delegate) {
        this.name = checkNotNull(name);
        this.metricRegistry = checkNotNull(metricRegistry);
        this.delegate = checkNotNull(delegate);
        init();
    }

    private void init() {
        metricRegistry.register(name(MetricExecutor.class, name + ".threadPool.queue"),
                new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return delegate.getQueue().size();
                    }
                });
        metricRegistry.register(name(MetricExecutor.class, name + ".threadPool.active"),
                new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return delegate.getActiveCount();
                    }
                });
    }

    @Override
    public void execute(Runnable runnable) {
        checkNotNull(runnable, "runnable must be not null");
        delegate.execute(new MonitoredRunnable(runnable, metricRegistry, name + ".threadPool.run"));
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return delegate.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return delegate.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return delegate.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return delegate.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return delegate.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.invokeAny(tasks, timeout, unit);
    }

    private static class MonitoredRunnable implements Runnable {

        private final Runnable r;
        private final MetricRegistry metricRegistry;
        private final String metricName;

        public MonitoredRunnable(Runnable runnable, MetricRegistry metricRegistry, String metricName) {
            this.r = runnable;
            this.metricRegistry = metricRegistry;
            this.metricName = metricName;
        }

        @Override
        public void run() {
            Timer.Context timer = metricRegistry.timer(name(MetricExecutor.class, metricName)).time();
            r.run();
            timer.stop();
        }
    }
}
