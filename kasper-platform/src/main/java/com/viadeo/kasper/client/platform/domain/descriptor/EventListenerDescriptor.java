package com.viadeo.kasper.client.platform.domain.descriptor;


import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;

public class EventListenerDescriptor implements Descriptor {
    private final Class<? extends EventListener> eventListenerClass;
    private final Class<? extends Event> eventClass;

    public EventListenerDescriptor(Class<? extends EventListener> eventListenerClass, Class<? extends Event> eventClass) {
        this.eventListenerClass = eventListenerClass;
        this.eventClass = eventClass;
    }

    @Override
    public Class<? extends EventListener> getReferenceClass() {
        return eventListenerClass;
    }

    public Class<? extends Event> getEventClass() {
        return eventClass;
    }
}