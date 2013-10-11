// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.domain.EventRegistrationCallback;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RepositoryResolverTest {

    @XKasperUnregistered
    private static final class TestDomain implements Domain {}

    private static final class TestAggregateRoot implements AggregateRoot {
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
        public Domain getDomain() { return null; }
        @Override
        public void setDomainLocator(DomainLocator domainLocator) { }
        @Override
        public <I extends KasperID> I getEntityId() { return null; }
        @Override
        public DateTime getCreationDate() { return null; }
        @Override
        public DateTime getModificationDate() { return null; }
        @Override
        public void initializeState(DomainEventStream domainEventStream) { }
    }

    @XKasperUnregistered
    private static final class TestRepository implements IRepository<TestAggregateRoot> {
        @Override
        public void init() { }
        @Override
        public TestAggregateRoot load(Object aggregateIdentifier, Long expectedVersion) { return null; }
        @Override
        public TestAggregateRoot load(Object aggregateIdentifier) { return null; }
        @Override
        public void add(TestAggregateRoot aggregate) { }
    }

    @XKasperUnregistered
    private static final class TestGenericRepository implements IRepository {
        @Override
        public void init() { }
        @Override
        public Object load(Object aggregateIdentifier, Long expectedVersion) { return null; }
        @Override
        public Object load(Object aggregateIdentifier) { return null; }
        @Override
        public void add(Object aggregate) { }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomainFromRepository() {
        // Given
        final RepositoryResolver resolver = new RepositoryResolver();
        final EntityResolver entityResolver = mock(EntityResolver.class);

        when( entityResolver.getDomain(TestAggregateRoot.class) )
                .thenReturn(Optional.<Class<? extends Domain>>of(TestDomain.class));

        resolver.setEntityResolver(entityResolver);

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomain(TestRepository.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());

        verify(entityResolver, times(1)).getDomain(TestAggregateRoot.class);
        verifyNoMoreInteractions(entityResolver);
    }

    @Test
    public void testGetDomainFromGenericRepository() {
        // Given
        final RepositoryResolver resolver = new RepositoryResolver();
        final EntityResolver entityResolver = mock(EntityResolver.class);

        // When
        try {
            final Optional<Class<? extends Domain>> domain =
                    resolver.getDomain(TestGenericRepository.class);
            fail();
        } catch (final KasperException e) {
            // Then should raise exception
        }
    }

}
