package com.viadeo.kasper.client.platform.components.eventbus;

import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.EventListener;


public class MessageListener {

    private EventListener listener;

    public MessageListener(EventListener listener) {
        this.listener = listener;
    }

    @SuppressWarnings("unused")
    public void handleMessage(EventMessage eventMessage) {
        listener.handle(eventMessage);
    }
}
