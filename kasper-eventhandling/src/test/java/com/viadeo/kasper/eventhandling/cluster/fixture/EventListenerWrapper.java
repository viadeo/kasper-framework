// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.eventhandling.cluster.fixture;

import com.google.common.base.Optional;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.EventListener;

public class EventListenerWrapper implements EventListener {

    private final Optional<EventListener> delegate;

    public EventListenerWrapper() {
        this(null);
    }

    public EventListenerWrapper(final EventListener delegate) {
        this.delegate = Optional.fromNullable(delegate);
    }

    @Override
    public void handle(EventMessage event) {
        if (delegate.isPresent()) {
            delegate.get().handle(event);
        }
    }
}
