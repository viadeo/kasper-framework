// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.viadeo.kasper.ddd.AggregateRoot;
import org.axonframework.eventstore.EventStore;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Facade repository used for non event-sourced repositories
 *
 * - manage with version
 * - can warn an optional event store about saved aggregates events
 *
 */
class ActionRepositoryEntityStoreFacade<AGR extends AggregateRoot> extends ActionRepositoryFacade<AGR> {

    private EventStore eventStore;

    // ------------------------------------------------------------------------

    ActionRepositoryEntityStoreFacade(final Repository<AGR> kasperRepository) {
        super(kasperRepository);
    }

    public void setEventStore(final EventStore eventStore) {
        this.eventStore = checkNotNull(eventStore);
    }

    // ------------------------------------------------------------------------

    protected void doSave(final AGR aggregate) {

        /**
         * Increment non-null version
         */
        if (null != aggregate.getVersion()) {
            aggregate.setVersion(aggregate.getVersion() + 1L);
        }

        if (null != eventStore) {
            eventStore.appendEvents(
                    aggregate.getClass().getSimpleName(),
                    aggregate.getUncommittedEvents()
            );
        }

        super.doSave(aggregate);
    }

    // ------------------------------------------------------------------------

    protected AGR doLoad(final Object aggregateIdentifier, final Long expectedVersion) {

        final AGR agr = super.doLoad(aggregateIdentifier, expectedVersion);

        /**
         * Set null version to 0L
         */
        if (null == agr.getVersion()) {
            agr.setVersion(0L);
        }

        return agr;
    }

    // ------------------------------------------------------------------------

    protected void doDelete(final AGR aggregate) {

        /**
         * Increment non-null version
         */
        if (null != aggregate.getVersion()) {
            aggregate.setVersion(aggregate.getVersion() + 1L);
        }

        super.doDelete(aggregate);
    }

}
