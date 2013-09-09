package com.viadeo.kasper.platform.components.eventbus;

import com.google.common.collect.Lists;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.AbstractContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventMessage;
import com.viadeo.kasper.event.impl.AbstractEvent;
import com.viadeo.kasper.event.impl.AbstractEventListener;
import junit.framework.Assert;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBusTerminal;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.mongodb.util.MyAsserts.assertEquals;

public class KasperEventBusTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(KasperEventBus.class);

    @Captor
    ArgumentCaptor<GenericEventMessage<Event>> captor;

    @Mock
    private EventBusTerminal terminal;

    @InjectMocks
    @Spy
    private KasperEventBus eventBus = new KasperEventBus();

    class DummyEvent extends AbstractEvent {
        public String foo;

        public DummyEvent(String bar) {
            this.foo = bar;
        }
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
        final DummyEvent dummyEvent = new DummyEvent("bar");
        final Context context = new DefaultContextBuilder().build();
        dummyEvent.setContext(context);

        // When
        eventBus.publish(dummyEvent);

        // Then
        Mockito.verify(eventBus).publish(captor.capture());
        final GenericEventMessage<Event> value = captor.getValue();
        Assert.assertEquals(dummyEvent, value.getPayload());
        Assert.assertTrue(value.getMetaData().containsKey(Context.METANAME));
        Assert.assertEquals(dummyEvent.getContext().get(), value.getMetaData().get(Context.METANAME));
        Assert.assertNotNull(((AbstractContext) value.getMetaData().get(Context.METANAME)).getKasperCorrelationId());
    }

    @Test(expected = IllegalStateException.class)
    public void contextAbsent() {
        final DummyEvent dummyEvent = new DummyEvent("bar");
        eventBus.publish(dummyEvent);
    }

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    private static class TestEvent extends AbstractEvent { }


    @XKasperUnregistered
    private static class TestEventListener extends AbstractEventListener<TestEvent> {

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
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final Integer LONG_RUNNING_TIME = 1000;

    private static final Integer THREAD_RETURNS = 1;
    private static final Integer EVENT_PUBLISHED = 2;

    @Test
    public void asynchronous() throws InterruptedException {
        // Given
        final List<Integer> returns = Lists.newLinkedList();
        final Event event = new TestEvent();
        final Context context = new DefaultContextBuilder().build();

        eventBus.subscribe(new TestEventListener(returns));

        // When
        LOGGER.info("Publish event");
        eventBus.publish(event, context);
        LOGGER.info("Event published");
        returns.add(EVENT_PUBLISHED);
        Thread.sleep(3 * LONG_RUNNING_TIME);

        // Then
        assertEquals(2, returns.size());
        assertEquals(EVENT_PUBLISHED, returns.get(0));
        assertEquals(THREAD_RETURNS, returns.get(1));
    }

    @Test
    public void listeningError() {
    }

}
