package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.eventbus.ReflectionRoutingKeysResolver;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class KasperRoutingKeyResolverUTest {


    private ReflectionRoutingKeysResolver routingKeyResolver;

    static public class EventB implements Event {
    }

    static public class EventA extends EventB {
    }

    static public class EventListenerB extends EventListener<EventB> {
        @Override
        public EventResponse handle(Context context, EventB event) {
            return EventResponse.success();
        }
    }

    static public class EventListenerA extends EventListener {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }

    static public class CatchAllEventListener extends EventListener<Event> {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }

    @Before
    public void setUp() throws Exception {

        routingKeyResolver = new ReflectionRoutingKeysResolver();

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
                "com.viadeo.kasper.core.component.event.eventbus.KasperRoutingKeyResolverUTest$EventB",
                "com.viadeo.kasper.core.component.event.eventbus.KasperRoutingKeyResolverUTest$EventA"
        ), routingKeys);
    }
}
