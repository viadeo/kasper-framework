// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.viadeo.kasper.ddd.AggregateRoot;
import org.axonframework.eventhandling.EventBus;

/**
 * Used to limit the allowed axon repository decorated by Kasper repositories
 */
interface DecoratedAxonRepository<AGR extends AggregateRoot>  extends org.axonframework.repository.Repository<AGR> {
    // Used to limit the allowed axon repository decorated by Kasper repositories

    void setEventBus(EventBus eventBus);

    ActionRepositoryFacade<AGR> getActionRepositoryFacade();

}
