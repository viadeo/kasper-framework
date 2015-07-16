// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class UnitOfWorkEvent implements Event {

    final List<String> events = Lists.newArrayList();

    // ------------------------------------------------------------------------

    public UnitOfWorkEvent(final List<String> eventIds) {
        super();
        for (final String eventId : checkNotNull(eventIds)) {
            this.events.add(eventId);
        }
    }

    // ------------------------------------------------------------------------

    public List<String> getEventIds() {
        return this.events;
    }

}
