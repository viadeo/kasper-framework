// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.eventhandling.async.EventProcessor;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class KasperProcessorDownLatch {

    private static final int N_THREADS = 5;
    public static final long TIMEOUT_IN_MILLIS = 1000 * 60;

    private final Set<EventProcessor> eventProcessors;
    private final ExecutorService executor;
    private final long timeout;

    private CountDownLatch countDownLatch;
    private boolean awaiting;

    // ------------------------------------------------------------------------

    public KasperProcessorDownLatch() {
        this(TIMEOUT_IN_MILLIS);
    }

    public KasperProcessorDownLatch(final long timeout) {
        this.awaiting = false;
        this.timeout = timeout;
        this.eventProcessors = Sets.newHashSet();
        this.executor = Executors.newFixedThreadPool(
            N_THREADS,
            new ThreadFactoryBuilder().setNameFormat("event-shutdown-hook-%d").build()
        );
    }

    // ------------------------------------------------------------------------

    public void await() {
        checkState( ! awaiting, "is already awaiting");

        countDownLatch = new CountDownLatch(runAllProcesses());
        awaiting = true;

        try {
            countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            throw new KasperException(String.format(
                    "Timeout expired : event processing still alive (%s)",
                    getCount()), e
            );
        }
    }

    private synchronized int runAllProcesses() {
        for (final EventProcessor eventProcessor : eventProcessors) {
            executor.submit(eventProcessor);
        }

        return eventProcessors.size();
    }

    public synchronized void process(final EventProcessor eventProcessor){
        checkNotNull(eventProcessor);
        if (awaiting) {
            throw new KasperException(
                    "Application shutdown : unable to create new processing scheduler!"
            );
        }
        this.eventProcessors.add(eventProcessor);
    }

    public synchronized void processDown(final EventProcessor eventProcessor){
        this.eventProcessors.remove(checkNotNull(eventProcessor));
        if (null != countDownLatch) {
            this.countDownLatch.countDown();
        }
    }

    public int getCount(){
        return this.eventProcessors.size();
    }

}
