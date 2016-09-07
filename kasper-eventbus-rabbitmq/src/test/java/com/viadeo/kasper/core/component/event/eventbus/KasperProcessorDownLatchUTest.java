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

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.exception.KasperException;
import org.axonframework.eventhandling.EventListener;
import org.axonframework.eventhandling.MultiplexingEventProcessingMonitor;
import org.axonframework.eventhandling.async.ErrorHandler;
import org.axonframework.eventhandling.async.EventProcessor;
import org.axonframework.unitofwork.UnitOfWorkFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class KasperProcessorDownLatchUTest {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private KasperProcessorDownLatch processorDownLatch;

    // ------------------------------------------------------------------------

    @Before
    public void setUp(){
        processorDownLatch = new KasperProcessorDownLatch(new MetricRegistry());
    }

    // ------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void process_withNullAsEventProcessor_throwException() {
        // Given nothing

        // When
        processorDownLatch.process(null);

        // Then throws an exception
    }

    @Test
    public void process_withEventProcessor_isOk() {
        // Given
        final EventProcessor eventProcessor = mock(EventProcessor.class);

        // When
        processorDownLatch.process(eventProcessor);

        // Then
        assertEquals(1, processorDownLatch.getCount());
    }

    @Test(expected = KasperException.class)
    public void process_withEventProcessor_inAwaiting_throwException() throws InterruptedException {
        // Given
        final EventProcessor eventProcessor = mock(EventProcessor.class);
        processorDownLatch.process(eventProcessor);

        executor.submit(new Runnable() {
            @Override
            public void run() {
                processorDownLatch.await();
            }
        });

        sleep(100);

        // When
        processorDownLatch.process(eventProcessor);

        // Then throws an exception
    }

    @Test(expected = NullPointerException.class)
    public void processDown_withNullAsEventProcessor_throwException() {
        // Given nothing

        // When
        processorDownLatch.processDown(null);

        // Then throws an exception
    }

    @Test
    public void processDown_withEventProcessor_isOk() {
        // Given
        final EventProcessor eventProcessor = mock(EventProcessor.class);
        processorDownLatch.process(eventProcessor);

        executor.submit(new Runnable() {
            @Override
            public void run() {
                processorDownLatch.await();
            }
        });

        // When
        processorDownLatch.processDown(eventProcessor);

        // Then
        assertEquals(0, processorDownLatch.getCount());
    }

    @Test
    public void process_withEventProcessor_inAwaiting_isOk() {
        // Given
        final EventProcessor eventProcessor = mock(EventProcessor.class);
        processorDownLatch.process(eventProcessor);

        // When
        processorDownLatch.processDown(eventProcessor);

        // Then
        assertEquals(0, processorDownLatch.getCount());
    }

    @Test
    public void await_withNoEventProcessor_isOk() {
        // Given nothing

        // When
        processorDownLatch.await();

        // Then
        assertEquals(0, processorDownLatch.getCount());
    }

    @Test
    public void await_withEventProcessors_isOk() {
        // Given
        final EventProcessor eventProcessor = spy(new MockEventProcessor(processorDownLatch, 100));
        processorDownLatch.process(eventProcessor);

        // When
        processorDownLatch.await();

        // Then
        assertEquals(0, processorDownLatch.getCount());
        verify(eventProcessor).run();
    }

    @Test(expected = IllegalStateException.class)
    public void await_inAwaiting_isOk() {
        // Given
        processorDownLatch.await();

        // When
        processorDownLatch.await();

        // Then throw an exception
    }

    @Test
    public void await_withSeveralEventProcessors_isOk() {
        // Given
        final EventProcessor eventProcessorA = spy(new MockEventProcessor(processorDownLatch, 100));
        processorDownLatch.process(eventProcessorA);

        final EventProcessor eventProcessorB = spy(new MockEventProcessor(processorDownLatch, 100));
        processorDownLatch.process(eventProcessorB);

        // When
        processorDownLatch.await();

        // Then
        assertEquals(0, processorDownLatch.getCount());
        verify(eventProcessorA).run();
        verify(eventProcessorB).run();
    }

    @Test(expected = KasperException.class)
    public void await_withEventProcessor_exceedingTimeout_throwException() {
        // Given
        processorDownLatch= new KasperProcessorDownLatch(100, new MetricRegistry());
        processorDownLatch.process(new MockEventProcessor(processorDownLatch, 500));

        // When
        processorDownLatch.await();

        // Then throw an exception
    }

    // ------------------------------------------------------------------------

    private static class MockEventProcessor extends EventProcessor {

        private static final Logger LOGGER = LoggerFactory.getLogger(MockEventProcessor.class);

        private final long timeout;

        public MockEventProcessor(final KasperProcessorDownLatch processorDownLatch, final long timeout) {
            super(
                mock(Executor.class),
                new KasperShutdownCallback(processorDownLatch, mock(EventProcessor.ShutdownCallback.class)),
                mock(ErrorHandler.class),
                mock(UnitOfWorkFactory.class),
                Sets.<EventListener>newHashSet(),
                mock(MultiplexingEventProcessingMonitor.class)
            );
            this.timeout = timeout;
        }

        @Override
        public void run() {
            LOGGER.info("Waiting until...");
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LOGGER.info("...now!! ({}ms)", timeout);
            super.run();
        }
    }

}
