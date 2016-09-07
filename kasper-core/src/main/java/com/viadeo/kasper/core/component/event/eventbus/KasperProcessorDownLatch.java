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
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.InstrumentedExecutorService;
import com.codahale.metrics.MetricRegistry;
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

    public KasperProcessorDownLatch(final MetricRegistry metricRegistry) {
        this(TIMEOUT_IN_MILLIS, metricRegistry);
    }

    public KasperProcessorDownLatch(final long timeout, final MetricRegistry metricRegistry) {
        this.awaiting = false;
        this.timeout = timeout;
        this.eventProcessors = Sets.newHashSet();
        this.executor = new InstrumentedExecutorService(Executors.newFixedThreadPool(
            N_THREADS,
            new ThreadFactoryBuilder().setNameFormat("event-shutdown-hook-%d").build()
        ), metricRegistry, KasperProcessorDownLatch.class.getName());
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
