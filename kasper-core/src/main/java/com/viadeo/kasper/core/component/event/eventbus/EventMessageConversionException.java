package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.base.Optional;
import org.axonframework.eventhandling.EventListener;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConversionException;

public class EventMessageConversionException extends MessageConversionException implements WithSource<Class<EventListener>> {

    private Optional<Class<EventListener>> eventListenerClass;

    public EventMessageConversionException(Message message, Throwable cause) {
        super("unable to convert message : " + (message == null ? null : message.getMessageProperties()), cause);

        if (message instanceof EventMessage) {
            eventListenerClass = Optional.fromNullable((Class<EventListener>) ((EventMessage) message).getEventListenerClass());
        } else {
            eventListenerClass = Optional.absent();
        }
    }

    public Optional<Class<EventListener>> getSource() {
        return eventListenerClass;
    }
}
