package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.collect.Sets;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.eventbus.ReflectionRoutingKeysResolver;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReflectionRoutingKeysResolverUTest {

    private ReflectionRoutingKeysResolver resolver;

    @Before
    public void setUp() throws Exception {
        resolver = new ReflectionRoutingKeysResolver();
    }

    @Test
    public void resolve_withEventListener_usingAbstractEvent_isOk() {
        // When
        List<String> keys = resolver.resolve(new AbstractEventListener());

        // Then
        assertNotNull(keys);
        assertEquals(3, keys.size());
        assertEquals(
                Sets.newHashSet(
                        MyAbstractEvent.class.getName(),
                        MySpeOneAbstractEvent.class.getName(),
                        MySpeTwoAbstractEvent.class.getName()
                ),
                Sets.newHashSet(keys)
        );
    }

    @Test
    public void resolve_withEventListener_usingConcreteEvent_isOk() {
        // When
        List<String> keys = resolver.resolve(new ConcreteEventListener());

        // Then
        assertNotNull(keys);
        assertEquals(1, keys.size());
        assertEquals(MyConcreteEvent.class.getName(), keys.iterator().next());
    }

    @Test
    public void resolve_withEventListener_usingInterface_isOk() {
        // When
        List<String> keys = resolver.resolve(new InterfaceEventListener());

        // Then
        assertNotNull(keys);
        assertEquals(2, keys.size());
        assertEquals(
                Sets.newHashSet(
                        MyInterfaceEvent.class.getName(),
                        MySpeAbstractEvent.class.getName()
                ),
                Sets.newHashSet(keys)
        );
    }

    @Test
    public void resolve_withEventListener_usingIEvent_isOk() {
        // When
        List<String> keys = resolver.resolve(new IEventListener());

        // Then
        assertNotNull(keys);
        assertEquals(1, keys.size());
        assertEquals("#", keys.iterator().next());
    }

    private static class MyConcreteEvent implements Event {}
    private static interface MyInterfaceEvent extends Event {}
    private static interface MySpeAbstractEvent extends MyInterfaceEvent {}
    private static class MyAbstractEvent implements Event {}
    private static class MySpeOneAbstractEvent extends MyAbstractEvent {}
    private static class MySpeTwoAbstractEvent extends MyAbstractEvent {}

    private static class IEventListener extends EventListener<Event> {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }
    private static class ConcreteEventListener extends EventListener<MyConcreteEvent> {
        @Override
        public EventResponse handle(Context context, MyConcreteEvent event) {
            return EventResponse.success();
        }
    }
    private static class AbstractEventListener extends EventListener<MyAbstractEvent> {
        @Override
        public EventResponse handle(Context context, MyAbstractEvent event) {
            return EventResponse.success();
        }
    }
    private static class InterfaceEventListener extends EventListener<MyInterfaceEvent> {
        @Override
        public EventResponse handle(Context context, MyInterfaceEvent event) {
            return EventResponse.success();
        }
    }
}
