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
import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.id.TestFormats;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.unitofwork.CurrentUnitOfWork;
import org.axonframework.unitofwork.DefaultUnitOfWork;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class AbstractRepositoryUTest {

    private AbstractRepository<ID,Concept> repository;

    @Before
    public void setUp() throws Exception {
        DefaultUnitOfWork unitOfWork = new DefaultUnitOfWork();
        unitOfWork.start();
        CurrentUnitOfWork.set(unitOfWork);

        MetricRegistry metricRegistry = new MetricRegistry();

        repository = spy(new BaseRepository<ID, Concept>(metricRegistry, mock(EventBus.class)) {
            @Override
            protected Optional<Concept> doLoad(ID aggregateIdentifier, Long expectedVersion) {
                return Optional.of(mock(Concept.class));
            }

            @Override protected void doSave(Concept aggregate) { }

            @Override protected void doDelete(Concept aggregate) { }

            @Override
            protected boolean doHas(ID aggregateIdentifier) { return Boolean.TRUE; }
        });

        repository.setAxonRepository(
                repository.createAxonRepository(metricRegistry, repository)
        );
    }

    @Test
    public void check_aggregate_identifier_during_a_load() {
        ID id = new ID("viadeo", "member", TestFormats.ID, 42);
        repository.load(id);
        verify(repository, times(1)).checkAggregateIdentifier(any(ID.class));

    }

    @Test
    public void check_aggregate_identifier_during_a_get() {
        ID id = new ID("viadeo", "member", TestFormats.ID, 42);
        repository.get(id);
        verify(repository, times(1)).checkAggregateIdentifier(id);
    }

    @Test
    public void check_aggregate_identifier_during_an_has() {
        ID id = new ID("viadeo", "member", TestFormats.ID, 42);
        repository.has(id);
        verify(repository, times(1)).checkAggregateIdentifier(id);
    }

}
