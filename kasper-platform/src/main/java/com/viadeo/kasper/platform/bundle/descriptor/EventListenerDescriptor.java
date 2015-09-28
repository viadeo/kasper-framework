// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.bundle.descriptor;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.listener.EventListener;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventListenerDescriptor implements KasperComponentDescriptor {

    private final Class<? extends EventListener> eventListenerClass;
    private final Set<Class<? extends Event>> eventClasses;

    // ------------------------------------------------------------------------

    public EventListenerDescriptor(
            final Class<? extends EventListener> eventListenerClass,
            final Class<? extends Event> eventClass
    ) {
        this(eventListenerClass, Sets.<Class<? extends Event>>newHashSet(eventClass));
    }

    public EventListenerDescriptor(
            final Class<? extends EventListener> eventListenerClass,
            final Set<Class<? extends Event>> eventClasses
    ) {
        this.eventListenerClass = checkNotNull(eventListenerClass);
        this.eventClasses = checkNotNull(eventClasses);
    }

    // ------------------------------------------------------------------------

    @Override
    public Class<? extends EventListener> getReferenceClass() {
        return eventListenerClass;
    }

    public Set<Class<? extends Event>> getEventClasses() {
        return eventClasses;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(eventListenerClass, eventClasses);
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
        return Objects.equal(this.eventListenerClass, other.eventListenerClass) && Objects.equal(this.eventClasses, other.eventClasses);
    }
}
