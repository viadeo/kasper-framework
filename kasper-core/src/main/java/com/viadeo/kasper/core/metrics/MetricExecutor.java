// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
package com.viadeo.kasper.core.metrics;

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
