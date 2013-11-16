// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.viadeo.kasper.ddd.AggregateRoot;
import org.axonframework.eventstore.EventStore;

/**
 * A Kasper event sourced repository based on AxonEventSourcedRepository
 *
 * @param <AGR>
 */
public abstract class EventSourcedRepository<AGR extends AggregateRoot> extends Repository<AGR> {

    private final EventStore eventStore;

    // ------------------------------------------------------------------------

    protected EventSourcedRepository(final EventStore eventStore) {
        this.eventStore = eventStore;
    }

    // ------------------------------------------------------------------------

    @Override
    protected DecoratedAxonRepository<AGR> getDecoratedRepository(final Class<AGR> entityType) {
        return new AxonEventSourcedRepository<>(
                new ActionRepositoryFacade<>(this),
                entityType,
                eventStore
        );
    }

}
