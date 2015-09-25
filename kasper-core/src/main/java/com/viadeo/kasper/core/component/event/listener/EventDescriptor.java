// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.listener;

import com.google.common.base.Objects;
import com.viadeo.kasper.api.component.event.Event;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventDescriptor<EVENT extends Event> {

    private final Class<EVENT> eventClass;
    private final boolean deprecated;

    public EventDescriptor(final Class<EVENT> eventClass) {
        this(eventClass, Boolean.FALSE);
    }

    public EventDescriptor(final Class<EVENT> eventClass, final boolean deprecated) {
        this.eventClass = checkNotNull(eventClass);
        this.deprecated = checkNotNull(deprecated);
    }

    public Class<Event> getEventClass() {
        return (Class<Event>) eventClass;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(eventClass, deprecated);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final EventDescriptor other = (EventDescriptor) obj;
        return Objects.equal(this.eventClass, other.eventClass) && Objects.equal(this.deprecated, other.deprecated);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("eventClass", eventClass)
                .add("deprecated", deprecated)
                .toString();
    }
}
