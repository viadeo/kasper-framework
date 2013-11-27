package com.viadeo.kasper.client.platform.domain.descriptor;


public class EventListenerDescriptor {
    private final Class eventListenerClass;
    private final Class eventClass;

    public EventListenerDescriptor(Class eventListenerClass, Class eventClass) {
        this.eventListenerClass = eventListenerClass;
        this.eventClass = eventClass;
    }

    public Class getReferenceClass() {
        return eventListenerClass;
    }

    public Class getEventClass() {
        return eventClass;
    }
}