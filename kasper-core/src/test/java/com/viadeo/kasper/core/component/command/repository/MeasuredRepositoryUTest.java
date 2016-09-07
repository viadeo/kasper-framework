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
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.annotation.XKasperRepository;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperConcept;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.*;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.unitofwork.CurrentUnitOfWork;
import org.axonframework.unitofwork.DefaultUnitOfWork;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

public class MeasuredRepositoryUTest {

    private TestRepository repository;
    private MetricRegistry metricRegistry;

    @Before
    public void setUp() throws Exception {
        DomainResolver domainResolver = new DomainResolver();

        ConceptResolver conceptResolver = new ConceptResolver();
        RelationResolver relationResolver = new RelationResolver(conceptResolver);
        EntityResolver entityResolver = new EntityResolver(conceptResolver, relationResolver);

        RepositoryResolver repositoryResolver = new RepositoryResolver(entityResolver);
        repositoryResolver.setDomainResolver(domainResolver);

        ResolverFactory resolverFactory = new ResolverFactory();
        resolverFactory.setRepositoryResolver(repositoryResolver);
        resolverFactory.setDomainResolver(domainResolver);

        KasperMetrics.setResolverFactory(resolverFactory);

        CurrentUnitOfWork.set(new DefaultUnitOfWork());
        metricRegistry = spy(new MetricRegistry());

        // initialize our repository in two times
        repository = spy(new TestRepository(metricRegistry, mock(EventBus.class)));
        repository.setAxonRepository(
                repository.createAxonRepository(metricRegistry, repository)
        );
    }

    @Test
    public void the_save_of_an_aggregate_is_measured() {
        // Given
        TestAggregate aggregate = new TestAggregate(mock(KasperID.class));

        // When
        repository.save(aggregate);

        //Then
        verify(metricRegistry).timer(Matchers.matches("test\\.repository\\..*\\.save-time"));
        verify(metricRegistry).meter("com.viadeo.kasper.core.component.command.repository.repository.saves");
    }

    @Test
    public void an_unexpected_error_during_the_save_of_an_aggregate_is_measured() {
        // Given
        TestAggregate aggregate = new TestAggregate(mock(KasperID.class));
        RuntimeException expectedException = new RuntimeException("Fake");
        doThrow(expectedException).when(repository).doSave(aggregate);

        // When
        try {
            repository.save(aggregate);

            // Then
        } catch (Exception e) {
            assertEquals(expectedException, e);
        } finally {
            verify(metricRegistry).timer(Matchers.matches("test\\.repository\\..*\\.save-time"));
            verify(metricRegistry).meter(Matchers.matches("test\\.repository\\..*\\.save-errors"));
            verify(metricRegistry).meter("com.viadeo.kasper.core.component.command.repository.repository.saves");
            verify(metricRegistry).meter("com.viadeo.kasper.core.component.command.repository.repository.save-errors");
        }
    }

    @Test
    public void the_update_of_an_aggregate_is_measured() {
        // Given
        TestAggregate aggregate = new TestAggregate(mock(KasperID.class));
        aggregate.setVersion(5L);

        // When
        repository.save(aggregate);

        //Then
        verify(metricRegistry).timer(Matchers.matches("test\\.repository\\..*\\.save-time"));
        verify(metricRegistry).meter("com.viadeo.kasper.core.component.command.repository.repository.saves");
    }

    @Test
    public void the_load_of_an_aggregate_is_measured() {
        // Given
        KasperID aggregateIdentifier = mock(KasperID.class);
        repository.save(new TestAggregate(aggregateIdentifier));
        reset(metricRegistry);

        // When
        repository.load(aggregateIdentifier);

        //Then
        verify(metricRegistry).timer(Matchers.matches("test\\.repository\\..*\\.load-time"));
        verify(metricRegistry).meter("com.viadeo.kasper.core.component.command.repository.repository.loads");
    }

    @Test
    public void an_unexpected_error_during_the_load_of_an_aggregate_is_measured() {
        // Given
        KasperID aggregateIdentifier = mock(KasperID.class);
        reset(metricRegistry);
        RuntimeException expectedException = new RuntimeException("Fake");
        doThrow(expectedException).when(repository).doLoad(any(KasperID.class), anyLong());

        // When
        try {
            repository.load(aggregateIdentifier, 0L);

        // Then
        } catch (Exception e) {
            assertEquals(expectedException, e);
        } finally {
            verify(metricRegistry).timer(Matchers.matches("test\\.repository\\..*\\.load-time"));
            verify(metricRegistry).meter(Matchers.matches("test\\.repository\\..*\\.load-errors"));
            verify(metricRegistry).meter("com.viadeo.kasper.core.component.command.repository.repository.loads");
            verify(metricRegistry).meter("com.viadeo.kasper.core.component.command.repository.repository.load-errors");
        }
    }

    @Test
    public void the_deletion_of_an_aggregate_is_measured() {
        // Given
        TestAggregate aggregate = new TestAggregate(mock(KasperID.class));
        aggregate.setVersion(5L);

        // When
        repository.delete(aggregate);

        //Then
        verify(metricRegistry).timer(Matchers.matches("test\\.repository\\..*\\.delete-time"));
        verify(metricRegistry).meter("com.viadeo.kasper.core.component.command.repository.repository.deletes");
    }

    @Test
    public void an_unexpected_error_during_the_deletion_of_an_aggregate_is_measured() {
        // Given
        TestAggregate aggregate = new TestAggregate(mock(KasperID.class));
        aggregate.setVersion(5L);

        RuntimeException expectedException = new RuntimeException("Fake");
        doThrow(expectedException).when(repository).doDelete(aggregate);

        // When
        try {
            repository.delete(aggregate);

            // Then
        } catch (Exception e) {
            assertEquals(expectedException, e);
        } finally {
            verify(metricRegistry).timer(Matchers.matches("test\\.repository\\..*\\.delete-time"));
            verify(metricRegistry).meter(Matchers.matches("test\\.repository\\..*\\.delete-errors"));
            verify(metricRegistry).meter("com.viadeo.kasper.core.component.command.repository.repository.deletes");
            verify(metricRegistry).meter("com.viadeo.kasper.core.component.command.repository.repository.delete-errors");
        }
    }

    // ------------------------------------------------------------------------

    public static class TestDomain implements Domain {}

    @XKasperConcept(label = "test", domain = TestDomain.class)
    public static class TestAggregate extends Concept {
        public TestAggregate(KasperID id) {
            setId(id);
        }
    }

    @XKasperRepository()
    public static class TestRepository extends BaseRepository<KasperID,TestAggregate> {

        private final Map<KasperID,TestAggregate> store;

        protected TestRepository(MetricRegistry metricRegistry, EventBus eventBus) {
            super(metricRegistry, eventBus);
            this.store = Maps.newHashMap();
        }

        @Override
        protected Optional<TestAggregate> doLoad(KasperID aggregateIdentifier, Long expectedVersion) {
            return Optional.fromNullable(store.get(aggregateIdentifier));
        }

        @Override
        protected void doSave(TestAggregate aggregate) {
            store.put(aggregate.getEntityId(), aggregate);
        }

        @Override
        protected void doDelete(TestAggregate aggregate) {
            store.remove(aggregate.getEntityId());
        }
    }
}
