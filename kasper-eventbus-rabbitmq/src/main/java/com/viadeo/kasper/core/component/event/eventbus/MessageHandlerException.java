package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.base.Optional;
import org.axonframework.eventhandling.EventListener;

public class MessageHandlerException extends RuntimeException implements WithSource<Class<EventListener>> {

    private final Optional<Class<EventListener>> optionalSource;

    @SuppressWarnings("unchecked")
    public MessageHandlerException(Class<? extends EventListener> source, Throwable cause) {
        super(cause);
        this.optionalSource = Optional.fromNullable((Class<EventListener>)source);
    }

    public Optional<Class<EventListener>> getSource() {
        return optionalSource;
    }
}
