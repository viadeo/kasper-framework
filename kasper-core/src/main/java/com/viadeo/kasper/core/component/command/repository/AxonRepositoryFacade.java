// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import org.axonframework.domain.AggregateRoot;

public interface AxonRepositoryFacade<AGR extends AggregateRoot>
        extends org.axonframework.repository.Repository<AGR>
{
    void save(AGR aggregate);

    void update(AGR aggregate);

    void delete(AGR aggregate);

    AGR get(Object aggregateIdentifier, Long expectedVersion);

    AGR get(Object aggregateIdentifier);

}
