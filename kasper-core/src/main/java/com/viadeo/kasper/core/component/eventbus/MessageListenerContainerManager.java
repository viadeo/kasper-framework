package com.viadeo.kasper.core.component.eventbus;

import com.google.common.base.Optional;
import org.axonframework.eventhandling.EventListener;

import java.util.Collection;

public interface MessageListenerContainerManager {

    MessageListenerContainer register(EventListener eventListener, String queueName);
    MessageListenerContainer unregister(EventListener eventListener);
    Collection<MessageListenerContainer> containers();
    Optional<MessageListenerContainer> get(Class<?> eventListenerClass);
    void stopAll();
    void startAll();

}
