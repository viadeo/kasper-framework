// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.viadeo.kasper.ddd.AggregateRoot;

/**
 * Facade repository used to :
 *
 * - add metrics before and after each action
 * - make some coherency validation on aggregates before and after each action
 *
 */
class ActionRepositoryVersionFacade<AGR extends AggregateRoot> extends ActionRepositoryFacade<AGR> {

    ActionRepositoryVersionFacade(final Repository<AGR> kasperRepository) {
        super(kasperRepository);
    }

    // ------------------------------------------------------------------------

    protected void doSave(final AGR aggregate) {

        /**
         * Increment non-null version
         */
        if (null != aggregate.getVersion()) {
            aggregate.setVersion(aggregate.getVersion() + 1L);
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
