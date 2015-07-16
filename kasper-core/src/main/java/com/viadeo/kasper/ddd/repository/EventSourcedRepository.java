// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.repository;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.api.exception.KasperException;
import org.axonframework.eventstore.EventStore;
import org.axonframework.repository.AggregateNotFoundException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Kasper event sourced repository based on AxonEventSourcedRepository
 *
 * @param <AGR> the aggregate class
 */
public abstract class EventSourcedRepository<AGR extends AggregateRoot> extends Repository<AGR> {

    private AxonEventSourcedRepository<AGR> axonEventSourcedRepository;

    // ------------------------------------------------------------------------

    protected EventSourcedRepository() { }

    protected EventSourcedRepository(final EventStore eventStore) {
        super.setEventStore(checkNotNull(eventStore));
    }

    // ------------------------------------------------------------------------

    @Override
    public void setEventStore(final EventStore eventStore) {
        super.setEventStore(eventStore);

        /**
         * Force re-init since axon event-sourcing repositories deprecates
         * runtime setting of the event store
         */
        this.init(true);
    }

    @Override
    protected DecoratedAxonRepository<AGR> getDecoratedRepository(final Class<AGR> entityType) {

        if ( ! this.getEventStore().isPresent()) {
            throw new KasperException("EventSourcedRepository needs an EventStore before usage");
        }

        final AxonEventSourcedRepository<AGR> axonRepository = new AxonEventSourcedRepository<>(
            new MetricsRepositoryFacade<>(this),
            entityType,
            this.getEventStore().get()
        );

        /* keep a local copy of the axon repository for final delegation */
        this.axonEventSourcedRepository = axonRepository;

        return axonRepository;
    }

    // ------------------------------------------------------------------------

    @Override
    protected Optional<AGR> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
        try {
            return Optional.of(
                this.axonEventSourcedRepository.doRealLoad(aggregateIdentifier, expectedVersion)
            );
        } catch (final AggregateNotFoundException e) {
            return Optional.absent();
        }
    }

    @Override
    protected void doSave(final AGR aggregate) {
        this.axonEventSourcedRepository.doRealSaveWithLock(aggregate);
    }

    @Override
    protected void doDelete(final AGR aggregate) {
        this.axonEventSourcedRepository.doRealDeleteWithLock(aggregate);
    }

}
