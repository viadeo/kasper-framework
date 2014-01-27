// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.AbstractContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

<<<<<<< HEAD
import static com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus.Policy;
import static junit.framework.Assert.*;
=======
import static org.junit.Assert.assertEquals;
>>>>>>> Add distributed event bus
import static org.mockito.Mockito.spy;

public class KasperEventBusTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(KasperEventBus.class);

    @Captor
    ArgumentCaptor<GenericEventMessage<Event>> captor;

    @XKasperUnregistered
    private static class TestEvent extends Event {
        private static final long serialVersionUID = 7266657610382378609L;
    }


    public KasperEventBusTest() {
        KasperMetrics.setMetricRegistry(new MetricRegistry());
    }

    // ------------------------------------------------------------------------

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    // ------------------------------------------------------------------------

    @Test
    public void nominal() throws Exception {
        // Given
        final KasperEventBus eventBus = spy(new KasperEventBusBuilder().build());
        final TestEvent dummyEvent = new TestEvent();
        CurrentContext.set(DefaultContextBuilder.get());

        // When
        eventBus.publish(dummyEvent);

        // Then
        Mockito.verify(eventBus).publishToSuper(captor.capture());
        final GenericEventMessage<Event> value = captor.getValue();
        assertEquals(dummyEvent, value.getPayload());
        assertTrue(value.getMetaData().containsKey(Context.METANAME));
        assertNotNull(((AbstractContext) value.getMetaData().get(Context.METANAME)).getKasperCorrelationId());
    }

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    private static class TestEventListener extends EventListener<TestEvent> {

        private final List<Integer> returns;

        TestEventListener(final List<Integer> returns) {
            this.returns = returns;
        }

        @Override
        public void handle(final EventMessage<TestEvent> eventMessage) {
            try {
                LOGGER.info("Begin long running process");
                Thread.sleep(LONG_RUNNING_TIME);
                returns.add(THREAD_RETURNS);
                LOGGER.info("Ended long running process");

                /* Error should not be catched by client */
                throw new RuntimeException("ERROR");

            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // ------------------------------------------------------------------------

    private static final Integer LONG_RUNNING_TIME = 1000;

    private static final Integer THREAD_RETURNS = 1;
    private static final Integer EVENT_PUBLISHED = 2;

    @Test
    public void asynchronous() throws InterruptedException {
        // Given
        final KasperEventBus eventBus = new KasperEventBusBuilder().build();
        final List<Integer> returns = Lists.newLinkedList();
        final Event event = new TestEvent();

        // When
        eventBus.subscribe(new TestEventListener(returns));
        LOGGER.info("Publish event");
        eventBus.publish(event);
        LOGGER.info("Event published");
        returns.add(EVENT_PUBLISHED);
        Thread.sleep(3 * LONG_RUNNING_TIME);

        // Then
        assertEquals(2, returns.size());
        assertEquals(EVENT_PUBLISHED, returns.get(0));
        assertEquals(THREAD_RETURNS, returns.get(1));
    }

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    private static class TestEventErrorListener extends EventListener<TestEvent> {
        @Override
        public void handle(final EventMessage<TestEvent> eventMessage) {
            throw new RuntimeException("ERROR");
        }
    }


    @Test
    public void listeningSyncError() {
        // Given
        final KasperEventBus syncEventBus = new KasperEventBusBuilder().build();
        final Event event = new TestEvent();

        // When
        syncEventBus.subscribe(new TestEventErrorListener());
        try {
            syncEventBus.publish(event);
        } catch (final RuntimeException e) {
            // Then ignore
        }
    }

}
