// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.impl;

import com.google.common.collect.Lists;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.event.Event;

import java.util.List;

public class UnitOfWorkEvent extends AbstractEvent {

    final List<KasperID> events = Lists.newArrayList();

    // ------------------------------------------------------------------------

    public UnitOfWorkEvent(final List<Event> events) {
        super();
        for (final Event event : events) {
            this.events.add(event.getId());
            event.setUOWEventId(this.getId());
        }
    }

    // ------------------------------------------------------------------------

    public List<KasperID> getEvents() {
        return this.events;
    }

}
