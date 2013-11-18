// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.viadeo.kasper.ddd.AggregateRoot;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;

/**
 * Decored axon event sourced repository
 *
 * Delegates actions to a Kasper repository through an action facade
 */
class AxonEventSourcedRepository<AGR extends AggregateRoot>
        extends EventSourcingRepository<AGR>
        implements DecoratedAxonRepository<AGR> {

    private final ActionRepositoryFacade<AGR> actionRepositoryFacade;

    // --------------------------------------------------------------------

    AxonEventSourcedRepository(final ActionRepositoryFacade<AGR> actionRepositoryFacade,
                               final Class<AGR> aggregateType,
                               final EventStore eventStore) {
        super(aggregateType, eventStore);
        this.actionRepositoryFacade = actionRepositoryFacade;
    }

    @Override
    public ActionRepositoryFacade<AGR> getActionRepositoryFacade() {
        return this.actionRepositoryFacade;
    }

    // --------------------------------------------------------------------

    @Override
    protected void doSaveWithLock(final AGR aggregate) {
        this.actionRepositoryFacade.doSave(aggregate);
    }

    @Override
    protected AGR doLoad(final Object aggregateIdentifier, final Long expectedVersion) {
        return this.actionRepositoryFacade.doLoad(aggregateIdentifier, expectedVersion);
    }

    @Override
    protected void doDeleteWithLock(final AGR aggregate) {
        this.actionRepositoryFacade.doDelete(aggregate);
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
