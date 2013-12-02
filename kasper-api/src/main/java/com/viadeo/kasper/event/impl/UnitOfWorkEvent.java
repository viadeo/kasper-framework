// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.impl;

import com.google.common.collect.Lists;
import com.viadeo.kasper.event.Event;

import java.util.List;

public class UnitOfWorkEvent extends Event {

    final List<String> events = Lists.newArrayList();

    // ------------------------------------------------------------------------

    public UnitOfWorkEvent(final List<String> eventIds) {
        super();
        for (final String eventId : eventIds) {
            this.events.add(eventId);
        }
    }

    // ------------------------------------------------------------------------

    public List<String> getEventIds() {
        return this.events;
    }

}
