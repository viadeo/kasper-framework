// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.repository;

import com.viadeo.kasper.ddd.AggregateRoot;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Decored axon event sourced repository
 *
 * Delegates actions to a Kasper repository through an action facade
 */
class AxonEventSourcedRepository<AGR extends AggregateRoot>
        extends EventSourcingRepository<AGR>
        implements DecoratedAxonRepository<AGR> {

    private final RepositoryFacade<AGR> repositoryFacade;

    // --------------------------------------------------------------------

    AxonEventSourcedRepository(final RepositoryFacade<AGR> repositoryFacade,
                               final Class<AGR> aggregateType,
                               final EventStore eventStore) {
        super(checkNotNull(aggregateType), checkNotNull(eventStore));
        this.repositoryFacade = checkNotNull(repositoryFacade);
    }

    @Override
    public RepositoryFacade<AGR> getRepositoryFacade() {
        return this.repositoryFacade;
    }

    // --------------------------------------------------------------------

    @Override
    protected void doSaveWithLock(final AGR aggregate) {
        this.repositoryFacade.doSave(aggregate);
    }

    @Override
    protected AGR doLoad(final Object aggregateIdentifier, final Long expectedVersion) {
        return this.repositoryFacade.doLoad(aggregateIdentifier, expectedVersion);
    }

    @Override
    protected void doDeleteWithLock(final AGR aggregate) {
        this.repositoryFacade.doDelete(aggregate);
    }

    // ------------------------------------------------------------------------

    public void doRealSaveWithLock(final AGR aggregate) {
        super.doSaveWithLock(aggregate);
    }

    public AGR doRealLoad(final Object aggregateIdentifier, final Long expectedVersion) {
        return super.doLoad(aggregateIdentifier, expectedVersion);
    }

    public void doRealDeleteWithLock(final AGR aggregate) {
        super.doDeleteWithLock(aggregate);
    }

}
