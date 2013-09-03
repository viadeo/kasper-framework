package com.viadeo.kasper.platform.components.eventbus;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.AbstractContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.impl.AbstractEvent;
import junit.framework.Assert;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBusTerminal;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

public class KasperHybridEventBusTest {

    @Captor
    ArgumentCaptor<GenericEventMessage<Event>> captor;

    @Mock
    private EventBusTerminal terminal;

    @InjectMocks
    @Spy
    private KasperHybridEventBus eventBus = new KasperHybridEventBus();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    class DummyEvent extends AbstractEvent {
        public String foo;

        public DummyEvent(String bar) {
            this.foo = bar;
        }
    }

    @Test
    public void nominal() throws Exception {
        DummyEvent dummyEvent = new DummyEvent("bar");
        Context context = new DefaultContextBuilder().build();
        dummyEvent.setContext(context);
        eventBus.publish(dummyEvent);
        Mockito.verify(eventBus).publish(captor.capture());

        GenericEventMessage<Event> value = captor.getValue();
        Assert.assertEquals(dummyEvent, value.getPayload());
        Assert.assertTrue(value.getMetaData().containsKey(Context.METANAME));
        Assert.assertEquals(dummyEvent.getContext().get(), value.getMetaData().get(Context.METANAME));
        Assert.assertNotNull(((AbstractContext) value.getMetaData().get(Context.METANAME)).getKasperCorrelationId());
    }

    @Test(expected = IllegalStateException.class)
    public void contextAbsent() {
        DummyEvent dummyEvent = new DummyEvent("bar");
        eventBus.publish(dummyEvent);
    }
}
