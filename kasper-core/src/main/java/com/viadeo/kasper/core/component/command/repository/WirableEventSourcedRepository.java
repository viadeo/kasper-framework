// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import org.axonframework.eventstore.EventStore;

/**
 * A class implements this interface in order to have the capability to be auto wired with the platform components.
 *
 * @see Repository
 */
public interface WirableEventSourcedRepository extends WirableRepository {

    /**
     * Wires an event store on this <code>Repository</code> instance.
     * @param eventStore an event bus
     */
    void setEventStore(EventStore eventStore);
}
