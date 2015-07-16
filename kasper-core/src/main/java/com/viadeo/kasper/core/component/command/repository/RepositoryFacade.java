// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;

import static com.google.common.base.Preconditions.checkNotNull;

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
        this.kasperRepository = checkNotNull(kasperRepository);
    }

    // ------------------------------------------------------------------------

    protected void doSave(final AGR aggregate) {

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
        this.kasperRepository.doDelete(aggregate);
    }

}
