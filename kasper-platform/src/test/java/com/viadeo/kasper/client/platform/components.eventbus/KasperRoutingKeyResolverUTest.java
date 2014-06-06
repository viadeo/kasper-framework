package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.components.eventbus.KasperRoutingKeysResolver;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.IEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class KasperRoutingKeyResolverUTest {


    private KasperRoutingKeysResolver routingKeyResolver;

    static public class EventB implements IEvent {
    }

    static public class EventA extends EventB {
    }

    static public class EventListenerB extends EventListener<EventB> {
    }

    static public class EventListenerA extends EventListener {
    }

    static public class CatchAllEventListener extends EventListener<IEvent> {
        @Override
        public void handle(IEvent event) {
        }
    }

    @Before
    public void setUp() throws Exception {

        routingKeyResolver = new KasperRoutingKeysResolver();

    }

    @Test
    public void subscribe_WithCatchAllHandler_CreatesOneBinding() throws Exception {

        // When
        List<String> routingKeys = routingKeyResolver.resolve(new CatchAllEventListener());

        assertEquals(Lists.newArrayList("#"), routingKeys);
    }

    @Test
    public void subscribe_WithParentListener_CreatesOneBindingForEachClassInInheritenceTree() throws Exception {

        // When
        List<String> routingKeys = routingKeyResolver.resolve(new EventListenerB());

        assertEquals(Lists.newArrayList(
                "com.viadeo.kasper.client.platform.components.eventbus.KasperRoutingKeyResolverUTest$EventB",
                "com.viadeo.kasper.client.platform.components.eventbus.KasperRoutingKeyResolverUTest$EventA"
        ), routingKeys);
    }
}
