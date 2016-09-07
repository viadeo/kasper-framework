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
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.component.command.repository.BaseRepository;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.domain.EventRegistrationCallback;
import org.axonframework.eventhandling.EventBus;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RepositoryResolverTest {

    @XKasperUnregistered
    private static final class TestDomain implements Domain {}

    private static final class TestAggregateRoot extends AggregateRoot {
        @Override
        public KasperID getIdentifier() { return null; }
        @Override
        public void commitEvents() { }
        @Override
        public int getUncommittedEventCount() { return 0; }
        @Override
        public DomainEventStream getUncommittedEvents() { return null; }
        @Override
        public Long getVersion() { return null; }
        @Override
        public boolean isDeleted() { return false; }
        @Override
        public void addEventRegistrationCallback(EventRegistrationCallback eventRegistrationCallback) { }
        @Override
        public void initializeState(DomainEventStream domainEventStream) { }
        @Override
        public KasperID getEntityId() { return null; }
    }

    @XKasperUnregistered
    private static final class TestRepository extends BaseRepository<KasperID,TestAggregateRoot> {

        protected TestRepository(EventBus eventBus) {
            super(eventBus);
        }

        @Override
        protected Optional<TestAggregateRoot> doLoad(KasperID aggregateIdentifier, Long expectedVersion) {
            return null;
        }

        @Override
        protected void doSave(TestAggregateRoot aggregate) { }

        @Override
        protected void doDelete(TestAggregateRoot aggregate) { }
    }

    @XKasperUnregistered
    private static final class TestGenericRepository extends BaseRepository {

        protected TestGenericRepository(EventBus eventBus) {
            super(eventBus);
        }

        @Override
        protected Optional doLoad(KasperID aggregateIdentifier, Long expectedVersion) {
            return null;
        }

        @Override
        protected void doSave(AggregateRoot aggregate) { }

        @Override
        protected void doDelete(AggregateRoot aggregate) { }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomainFromRepository() {
        // Given
        final RepositoryResolver resolver = new RepositoryResolver();
        final EntityResolver entityResolver = mock(EntityResolver.class);

        when( entityResolver.getDomainClass(TestAggregateRoot.class) )
                .thenReturn(Optional.<Class<? extends Domain>>of(TestDomain.class));

        resolver.setEntityResolver(entityResolver);

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestRepository.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());

        verify(entityResolver, times(1)).getDomainClass(TestAggregateRoot.class);
        verifyNoMoreInteractions(entityResolver);
    }

    @Test
    public void testGetDomainFromGenericRepository() {
        // Given
        final RepositoryResolver resolver = new RepositoryResolver();

        // When
        try {
            resolver.getDomainClass(TestGenericRepository.class);
            fail();
        } catch (final KasperException e) {
            // Then should raise exception
        }
    }

}
