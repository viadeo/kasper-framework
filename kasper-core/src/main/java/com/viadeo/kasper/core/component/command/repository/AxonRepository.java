// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import org.axonframework.repository.AggregateNotFoundException;

import static com.google.common.base.Preconditions.checkNotNull;

public class AxonRepository<ID extends KasperID, AGR extends AggregateRoot>
    extends org.axonframework.repository.AbstractRepository<AGR>
    implements AxonRepositoryFacade<AGR>
{

    private final AbstractRepository<ID,AGR> repository;

    // ------------------------------------------------------------------------

    public AxonRepository(final AbstractRepository<ID, AGR> repository) {
        super(repository.getAggregateClass());
        this.repository = checkNotNull(repository);
    }

    // ------------------------------------------------------------------------

    @Override
    public void save(final AGR aggregate) {
        doSave(aggregate);
    }

    @Override
    public void update(final AGR aggregate) {
        doUpdate(aggregate);
    }

    @Override
    public void delete(final AGR aggregate) {
        doDelete(aggregate);
    }

    @Override
    public AGR load(final Object aggregateIdentifier) {
        return load(aggregateIdentifier, null);
    }

    @Override
    public AGR get(final Object aggregateIdentifier, final Long expectedVersion) {
        return doLoad(aggregateIdentifier, expectedVersion);
    }

    @Override
    public AGR get(final Object aggregateIdentifier) {
        return get(aggregateIdentifier, null);
    }

    @Override
    protected void doSave(final AGR aggregate) {
        checkNotNull(aggregate);

        if (null != aggregate.getVersion()) {
            doUpdate(aggregate);
        } else {
            repository.eventStore.appendEvents(aggregate.getClass().getSimpleName(), aggregate.getUncommittedEvents());
            aggregate.setVersion(1L);
            repository.doSave(aggregate);
        }
    }

    protected void doUpdate(final AGR aggregate) {
        checkNotNull(aggregate);
        repository.eventStore.appendEvents(aggregate.getClass().getSimpleName(), aggregate.getUncommittedEvents());

        if (null != aggregate.getVersion()) {
            aggregate.setVersion(aggregate.getVersion() + 1L);
        }

        repository.doUpdate(aggregate);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected AGR doLoad(final Object aggregateIdentifier, final Long expectedVersion) {
        repository.checkAggregateIdentifier((ID) aggregateIdentifier);

        final Optional<AGR> optionalAggregate = repository.doLoad((ID) aggregateIdentifier, expectedVersion);

        if (!optionalAggregate.isPresent()) {
            throw new AggregateNotFoundException(aggregateIdentifier, "Failed to load an aggregate");
        }

        if (optionalAggregate.isPresent() && optionalAggregate.get().getVersion() == null) {
            optionalAggregate.get().setVersion(0L);
        }

        return optionalAggregate.get();
    }

    @Override
    protected void doDelete(final AGR aggregate) {
        checkNotNull(aggregate);
        repository.eventStore.appendEvents(aggregate.getClass().getSimpleName(), aggregate.getUncommittedEvents());

        if (null != aggregate.getVersion()) {
            aggregate.setVersion(aggregate.getVersion() + 1L);
        }

        repository.doDelete(aggregate);
    }

}
