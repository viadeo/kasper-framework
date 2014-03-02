// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.viadeo.kasper.exception.KasperException;
import org.axonframework.eventhandling.async.EventProcessor;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class KasperProcessorDownLatchUTest {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private KasperProcessorDownLatch processorDownLatch;

    // ------------------------------------------------------------------------

    @Before
    public void setUp(){
        processorDownLatch = new KasperProcessorDownLatch();
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
    public synchronized void process_withEventProcessor_inAwaiting_throwException() throws InterruptedException {
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
        final EventProcessor eventProcessor = mock(EventProcessor.class);
        processorDownLatch.process(eventProcessor);

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                processorDownLatch.processDown(eventProcessor);
            }
        });

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
        final EventProcessor eventProcessorA = mock(EventProcessor.class);
        processorDownLatch.process(eventProcessorA);

        final EventProcessor eventProcessorB = mock(EventProcessor.class);
        processorDownLatch.process(eventProcessorB);

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                processorDownLatch.processDown(eventProcessorA);
            }
        });

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                processorDownLatch.processDown(eventProcessorB);
            }
        });

        // When
        processorDownLatch.await();

        // Then
        assertEquals(0, processorDownLatch.getCount());
        verify(eventProcessorA).run();
        verify(eventProcessorB).run();
    }

}
