package com.viadeo.kasper.eventhandling.terminal.amqp;

import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.EventListener;
import org.springframework.amqp.support.converter.MessageConversionException;


public class MessageListener {

    private EventListener listener;

    public MessageListener(EventListener listener) {
        this.listener = listener;
    }

    @SuppressWarnings("unused")
    public void handleMessage(EventMessage eventMessage) {
        try {
            listener.handle(eventMessage);
        } catch (Exception e) {
            throw new MessageConversionException("Unable to deserialize an incoming message", e);
        }
    }
}
