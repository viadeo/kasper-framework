// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.bundle.descriptor;

import com.google.common.base.Objects;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.listener.EventListener;

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

    @Override
    public int hashCode() {
        return Objects.hashCode(eventListenerClass, eventClass);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final EventListenerDescriptor other = (EventListenerDescriptor) obj;
        return Objects.equal(this.eventListenerClass, other.eventListenerClass) && Objects.equal(this.eventClass, other.eventClass);
    }
}
