package com.viadeo.kasper.core.component.event.eventbus;

import org.axonframework.eventhandling.EventListener;
import org.springframework.amqp.core.Message;

public class EventMessage extends Message {

    private final Class<? extends EventListener> eventListenerClass;

    public EventMessage(final Message message, final Class<? extends EventListener> eventListenerClass) {
        super(message.getBody(), message.getMessageProperties());
        this.eventListenerClass = eventListenerClass;
    }

    public Class<? extends EventListener> getEventListenerClass() {
        return eventListenerClass;
    }
}
