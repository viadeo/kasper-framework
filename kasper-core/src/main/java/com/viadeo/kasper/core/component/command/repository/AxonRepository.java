// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import org.axonframework.repository.AbstractRepository;

/**
 * Decored axon repository
 *
 * Delegates actions to a Kasper repository through an action facade
 */
class AxonRepository<AGR extends AggregateRoot>
        extends AbstractRepository<AGR>
        implements DecoratedAxonRepository<AGR> {

    private final RepositoryFacade<AGR> repositoryFacade;

    // --------------------------------------------------------------------

    AxonRepository(final RepositoryFacade<AGR> repositoryFacade, final Class<AGR> aggregateType) {
        super(aggregateType);
        this.repositoryFacade = repositoryFacade;
    }

    @Override
    public RepositoryFacade<AGR> getRepositoryFacade() {
        return this.repositoryFacade;
    }

    // --------------------------------------------------------------------

    @Override
    protected void doSave(final AGR aggregate) {
        this.repositoryFacade.doSave(aggregate);
    }

    @Override
    protected AGR doLoad(final Object aggregateIdentifier, final Long expectedVersion) {
        return this.repositoryFacade.doLoad(aggregateIdentifier, expectedVersion);
    }

    @Override
    protected void doDelete(final AGR aggregate) {
        this.repositoryFacade.doDelete(aggregate);
    }

}
