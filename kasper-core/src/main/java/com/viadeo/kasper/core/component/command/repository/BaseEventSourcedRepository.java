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

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventstore.EventStore;

/**
 * Base implementation for an event sourced repository.
 *
 * @param <ID> the aggregate id
 * @param <AGR> AggregateRoot
 */
public abstract class BaseEventSourcedRepository<ID extends KasperID,AGR extends AggregateRoot>
        extends AbstractRepository<ID,AGR>
{

    protected BaseEventSourcedRepository(final EventStore eventStore, final EventBus eventBus) {
        this(KasperMetrics.getMetricRegistry(), eventStore, eventBus);
    }

    protected BaseEventSourcedRepository(final MetricRegistry metricRegistry, final EventStore eventStore, final EventBus eventBus) {
        super(metricRegistry, eventStore, eventBus);
    }

    // ------------------------------------------------------------------------

    @Override
    protected AxonEventSourcedRepository<ID,AGR> createAxonRepository(final MetricRegistry metricRegistry, final AbstractRepository<ID,AGR> repository) {
        return new AxonEventSourcedRepository<>(
                new MeasuredRepository<>(metricRegistry, repository),
                eventStore
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public AxonEventSourcedRepository<ID,AGR> getAxonRepository() {
        return (AxonEventSourcedRepository<ID, AGR>) super.getAxonRepository();
    }

}
