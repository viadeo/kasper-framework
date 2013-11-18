// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.repository;

import com.viadeo.kasper.ddd.AggregateRoot;
import org.axonframework.eventstore.EventStore;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Facade repository used for non event-sourced repositories
 *
 * - warn an optional event store about saved aggregates events
 *
 */
class EntityStoreFacade<AGR extends AggregateRoot> extends MetricsRepositoryFacade<AGR> {

    private EventStore eventStore;

    // ------------------------------------------------------------------------

    EntityStoreFacade(final Repository<AGR> kasperRepository) {
        super(kasperRepository);
    }

    public void setEventStore(final EventStore eventStore) {
        this.eventStore = checkNotNull(eventStore);
    }

    // ------------------------------------------------------------------------

    protected void doSave(final AGR aggregate) {

        if (null != eventStore) {
            eventStore.appendEvents(
                    aggregate.getClass().getSimpleName(),
                    aggregate.getUncommittedEvents()
            );
        }

        super.doSave(aggregate);
    }

}
