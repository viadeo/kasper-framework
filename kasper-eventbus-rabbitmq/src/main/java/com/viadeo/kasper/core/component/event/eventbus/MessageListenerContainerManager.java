package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.component.event.listener.EventListener;

import java.util.Collection;

public interface MessageListenerContainerManager {

    MessageListenerContainer register(EventListener eventListener, String queueName);
    MessageListenerContainer unregister(EventListener eventListener);
    Collection<MessageListenerContainer> containers();
    Optional<MessageListenerContainer> get(Class<?> eventListenerClass);
    void stopAll();
    void startAll();

}
