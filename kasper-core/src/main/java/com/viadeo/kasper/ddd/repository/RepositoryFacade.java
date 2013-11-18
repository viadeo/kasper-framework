// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.repository;

import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.event.Event;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.domain.DomainEventStream;

/**
 * Facade repository used to :
 *
 * - add metrics before and after each action
 * - make some coherency validation on aggregates before and after each action
 *
 */
class RepositoryFacade<AGR extends AggregateRoot> {
    private final Repository<AGR> kasperRepository; /* The repository to proxy actions on */

    // ------------------------------------------------------------------------

    RepositoryFacade(final Repository<AGR> kasperRepository) {
        this.kasperRepository = kasperRepository;
    }

    // ------------------------------------------------------------------------

    private void enrichEvents(final AGR aggregate) {
        /**
         * Mark events persistency type
         */
        if (aggregate.getUncommittedEventCount() > 0) {
            final DomainEventStream eventStream = aggregate.getUncommittedEvents();
            while (eventStream.hasNext()) {
                final DomainEventMessage message = eventStream.next();
                if (Event.class.isAssignableFrom(message.getPayloadType())) {
                    final Event event = (Event) message.getPayload();
                    if (EventSourcedRepository.class.isAssignableFrom(this.kasperRepository.getClass())) {
                        event.setPersistencyType(Event.PersistencyType.EVENT_SOURCE);
                    } else {
                        event.setPersistencyType(Event.PersistencyType.EVENT_INFO);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------

    protected void doSave(final AGR aggregate) {

        this.enrichEvents(aggregate);

        /**
         * Manage with save/update differentiation for Kasper repositories
         */
        if (null == aggregate.getVersion()) {
            this.kasperRepository.doSave(aggregate);
        } else {
            this.kasperRepository.doUpdate(aggregate);
        }

    }

    // ------------------------------------------------------------------------

    protected AGR doLoad(final Object aggregateIdentifier, final Long expectedVersion) {
        return this.kasperRepository.doLoad(aggregateIdentifier, expectedVersion);
    }

    // ------------------------------------------------------------------------

    protected void doDelete(final AGR aggregate) {
        this.enrichEvents(aggregate);

        this.kasperRepository.doDelete(aggregate);
    }

}
