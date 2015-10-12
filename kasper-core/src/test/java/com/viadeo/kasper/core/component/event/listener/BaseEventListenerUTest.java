// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.listener;

import com.viadeo.kasper.api.component.event.Event;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class BaseEventListenerUTest {

    @SuppressWarnings("deprecation")
    @Test
    public void get_the_event_descriptor_from_a_deprecated_event_listener() {
        DeprecatedEventListener eventListener = new DeprecatedEventListener();
        Set<EventDescriptor> eventDescriptors = eventListener.getEventDescriptors();
        assertTrue(eventDescriptors.size() == 1);
        assertTrue(eventDescriptors.contains(new EventDescriptor<>(Event.class, Boolean.TRUE)));
    }

    @Test
    public void get_the_event_descriptor_from_an_event_listener() {
        EventListener eventListener = new EventListener();
        Set<EventDescriptor> eventDescriptors = eventListener.getEventDescriptors();
        assertTrue(eventDescriptors.size() == 1);
        assertTrue(eventDescriptors.contains(new EventDescriptor<>(Event.class, Boolean.FALSE)));
    }

    @Deprecated
    private static class DeprecatedEventListener extends BaseEventListener<Event> { }

    private static class EventListener extends BaseEventListener<Event> { }
}
