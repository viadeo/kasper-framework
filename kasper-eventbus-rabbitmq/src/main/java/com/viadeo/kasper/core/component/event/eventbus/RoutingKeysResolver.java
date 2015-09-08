package com.viadeo.kasper.core.component.event.eventbus;

import org.axonframework.eventhandling.EventListener;

public interface RoutingKeysResolver {

    /**
     * For a given event listener, this resolver will give out
     * a list of routing keys to bind queue on.
     *
     * @param listener the event listener
     * @return a list of routing key
     * @see AMQPCluster
     * @see com.viadeo.kasper.core.component.event.listener.EventListener
     */
    RoutingKeys resolve(EventListener listener);

}
