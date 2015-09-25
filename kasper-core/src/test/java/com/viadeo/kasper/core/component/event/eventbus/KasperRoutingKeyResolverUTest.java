package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.collect.Sets;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class KasperRoutingKeyResolverUTest {


    private ReflectionRoutingKeysResolver routingKeyResolver;

    static public class EventB implements Event {
    }

    static public class EventA extends EventB {
    }

    static public class EventListenerB extends AutowiredEventListener<EventB> {
        @Override
        public EventResponse handle(Context context, EventB event) {
            return EventResponse.success();
        }
    }

    static public class EventListenerA extends AutowiredEventListener {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }

    static public class CatchAllEventListener extends AutowiredEventListener<Event> {
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
        RoutingKeys routingKeys = routingKeyResolver.resolve(new CatchAllEventListener());

        assertEquals(Sets.newHashSet(new RoutingKeys.RoutingKey("#")), routingKeys.get());
    }

    @Test
    public void subscribe_WithParentListener_CreatesOneBindingForEachClassInInheritenceTree() throws Exception {

        // When
        RoutingKeys routingKeys = routingKeyResolver.resolve(new EventListenerB());

        assertEquals(Sets.<RoutingKeys.RoutingKey>newHashSet(
                new RoutingKeys.RoutingKey("com.viadeo.kasper.core.component.event.eventbus.KasperRoutingKeyResolverUTest$EventB"),
                new RoutingKeys.RoutingKey("com.viadeo.kasper.core.component.event.eventbus.KasperRoutingKeyResolverUTest$EventA")
        ), routingKeys.get());
    }
}
