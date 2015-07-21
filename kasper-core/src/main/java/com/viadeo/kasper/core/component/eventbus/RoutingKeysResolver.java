package com.viadeo.kasper.core.component.eventbus;

import org.axonframework.eventhandling.EventListener;

import java.util.List;

public interface RoutingKeysResolver {

    /**
     * For a given event listener, this resolver will give out
     * a list of routing keys to bind queue on.
     *
     * @param listener the event listener
     * @return a list of routing keys
     * @see com.viadeo.kasper.core.component.eventbus.AMQPCluster
     * @see com.viadeo.kasper.core.component.event.EventListener
     */
    List<String> resolve(EventListener listener);
}
