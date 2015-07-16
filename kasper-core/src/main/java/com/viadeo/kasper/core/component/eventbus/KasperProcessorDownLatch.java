// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.eventbus;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viadeo.kasper.api.exception.KasperException;
import org.axonframework.eventhandling.async.EventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class KasperProcessorDownLatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperProcessorDownLatch.class);

    private static final int N_THREADS = 5;
    public static final long TIMEOUT_IN_MILLIS = 1000 * 60;

    private final Set<EventProcessor> eventProcessors;
    private final ExecutorService executor;
    private final long timeout;

    private CountDownLatch countDownLatch;
    private boolean awaiting;
    private int nbProcessors;

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

        nbProcessors = runAllProcessors();

        LOGGER.info("Starting all scheduled event processor : {}", nbProcessors);
        
        countDownLatch = new CountDownLatch(nbProcessors);
        awaiting = true;

        final boolean succeed;

        try {
            succeed = countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            throw new KasperException(e);
        }

        if(succeed) {
            LOGGER.info("All event processors were finished : {}", nbProcessors);
        } else {
            final String message = String.format("Timeout expired : event processors still alive (%s)", getCount());
            LOGGER.error(message);
            throw new KasperException(message);
        }
    }

    private synchronized int runAllProcessors() {
        for (final EventProcessor eventProcessor : eventProcessors) {
            executor.submit(eventProcessor);
        }

        return eventProcessors.size();
    }

    public synchronized void process(final EventProcessor eventProcessor){
        checkNotNull(eventProcessor);
        if (awaiting) {
            final String message = "Reject event processing : the application is awaiting to shutdown";
            LOGGER.warn(message);
            throw new KasperException(message);
        }
        this.eventProcessors.add(eventProcessor);
    }

    public synchronized void processDown(final EventProcessor eventProcessor){
        this.eventProcessors.remove(checkNotNull(eventProcessor));
        if (null != countDownLatch) {
            this.countDownLatch.countDown();
            LOGGER.info("Event process complete ({}/{})", countDownLatch.getCount(), nbProcessors);
        }
    }

    public int getCount(){
        return this.eventProcessors.size();
    }

}
