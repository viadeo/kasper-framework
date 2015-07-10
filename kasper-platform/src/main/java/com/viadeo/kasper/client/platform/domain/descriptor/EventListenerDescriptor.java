// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.domain.descriptor;

import com.viadeo.kasper.api.domain.event.Event;
import com.viadeo.kasper.event.EventListener;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventListenerDescriptor implements KasperComponentDescriptor {

    private final Class<? extends EventListener> eventListenerClass;
    private final Class<? extends Event> eventClass;

    // ------------------------------------------------------------------------

    public EventListenerDescriptor(final Class<? extends EventListener> eventListenerClass,
                                   final Class<? extends Event> eventClass) {
        this.eventListenerClass = checkNotNull(eventListenerClass);
        this.eventClass = checkNotNull(eventClass);
    }

    // ------------------------------------------------------------------------

    @Override
    public Class<? extends EventListener> getReferenceClass() {
        return eventListenerClass;
    }

    public Class<? extends Event> getEventClass() {
        return eventClass;
    }

}
