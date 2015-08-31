package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.base.Optional;
import org.axonframework.eventhandling.EventListener;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConversionException;

public class EventBusMessageConversionException extends MessageConversionException implements WithSource<Class<EventListener>> {

    private Optional<Class<EventListener>> eventListenerClass;

    public EventBusMessageConversionException(Message message, Throwable cause) {
        super("unable to convert message : " + (message == null ? null : message.getMessageProperties()), cause);

        if (message instanceof EventBusMessage) {
            eventListenerClass = Optional.fromNullable((Class<EventListener>) ((EventBusMessage) message).getEventListenerClass());
        } else {
            eventListenerClass = Optional.absent();
        }
    }

    public Optional<Class<EventListener>> getSource() {
        return eventListenerClass;
    }
}
