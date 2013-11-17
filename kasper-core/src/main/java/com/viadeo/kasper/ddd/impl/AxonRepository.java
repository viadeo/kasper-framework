// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.viadeo.kasper.ddd.AggregateRoot;
import org.axonframework.repository.AbstractRepository;

/**
 * Decored axon repository
 *
 * Delegates actions to a Kasper repository through an action facade
 */
class AxonRepository<AGR extends AggregateRoot>
        extends AbstractRepository<AGR>
        implements DecoratedAxonRepository<AGR> {

    private final ActionRepositoryFacade<AGR> actionRepositoryFacade;

    // --------------------------------------------------------------------

    AxonRepository(final ActionRepositoryFacade<AGR> actionRepositoryFacade, final Class<AGR> aggregateType) {
        super(aggregateType);
        this.actionRepositoryFacade = actionRepositoryFacade;
    }

    @Override
    public ActionRepositoryFacade<AGR> getActionRepositoryFacade() {
        return this.actionRepositoryFacade;
    }

    // --------------------------------------------------------------------

    @Override
    protected void doSave(final AGR aggregate) {
        this.actionRepositoryFacade.doSave(aggregate);
    }

    @Override
    protected AGR doLoad(final Object aggregateIdentifier, final Long expectedVersion) {
        return this.actionRepositoryFacade.doLoad(aggregateIdentifier, expectedVersion);
    }

    @Override
    protected void doDelete(final AGR aggregate) {
        this.actionRepositoryFacade.doDelete(aggregate);
    }

}
