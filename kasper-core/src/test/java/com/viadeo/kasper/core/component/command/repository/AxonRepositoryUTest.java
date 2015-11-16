// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventstore.EventStore;
import org.axonframework.unitofwork.CurrentUnitOfWork;
import org.axonframework.unitofwork.DefaultUnitOfWork;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class AxonRepositoryUTest {

    private TestRepository repository;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        CurrentUnitOfWork.set(new DefaultUnitOfWork());

        repository = spy(new TestRepository(mock(EventStore.class), mock(EventBus.class)));
        AxonRepository axonRepository = new AxonRepository(repository);
        doReturn(axonRepository).when(repository).createAxonRepository(any(MetricRegistry.class), any(AbstractRepository.class));
        doNothing().when(repository).doDelete(any(AggregateRoot.class));
        doNothing().when(repository).doSave(any(AggregateRoot.class));
    }

    @Test
    public void save_must_increment_an_existing_version_of_an_aggregate() {
        // Given
        AggregateRoot<KasperID> aggregate = new AggregateRoot<KasperID>() {};

        // When
        repository.save(aggregate);

        // Then
        assertEquals(1L, (long) aggregate.getVersion());
        verify(repository).doSave(aggregate);
    }

    @Test
    public void save_must_increment_an_existing_version_of_an_aggregate_2() {
        // Given
        AggregateRoot<KasperID> aggregate = new AggregateRoot<KasperID>() {};
        aggregate.setVersion(1L);

        // When
        repository.save(aggregate);

        // Then
        assertEquals(2L, (long) aggregate.getVersion());
        verify(repository).doUpdate(aggregate);
    }

    @Test
    public void delete_must_increment_an_existing_version_of_an_aggregate() {
        // Given
        AggregateRoot<KasperID> aggregate = new AggregateRoot<KasperID>() {};
        aggregate.setVersion(1L);

        // When
        repository.delete(aggregate);

        // Then
        assertEquals(2L, (long) aggregate.getVersion());
        verify(repository).doDelete(aggregate);
    }

    @Test
    public void load_must_initialize_to_zero_an_aggregate_without_version() {
        // Given
        KasperID aggregateIdentifier = mock(KasperID.class);
        AggregateRoot<KasperID> aggregate = new TestAggregate(aggregateIdentifier) {};
        doReturn(Optional.of(aggregate)).when(repository).doLoad(any(KasperID.class), anyLong());

        // When
        Optional<AggregateRoot<KasperID>> aggregateOptional = repository.load(aggregateIdentifier);

        // Then
        assertNotNull(aggregateOptional);
        assertTrue(aggregateOptional.isPresent());
        assertEquals(0L, (long) aggregateOptional.get().getVersion());
        verify(repository).doLoad(eq(aggregateIdentifier), anyLong());
    }

    @Test
    public void get_must_initialize_to_zero_an_aggregate_without_version() {
        // Given
        KasperID aggregateIdentifier = mock(KasperID.class);
        AggregateRoot<KasperID> aggregate = new TestAggregate(aggregateIdentifier) {};
        doReturn(Optional.of(aggregate)).when(repository).doLoad(eq(aggregateIdentifier), anyLong());

        // When
        Optional<AggregateRoot<KasperID>> aggregateOptional = repository.get(aggregateIdentifier);

        // Then
        assertNotNull(aggregateOptional);
        assertTrue(aggregateOptional.isPresent());
        assertEquals(0L, (long) aggregateOptional.get().getVersion());
        verify(repository).doLoad(eq(aggregateIdentifier), anyLong());
    }

    // ------------------------------------------------------------------------

    public static class TestAggregate extends AggregateRoot<KasperID> {
        public TestAggregate(KasperID id) {
            setId(id);
        }
    }

    public static class TestRepository extends AbstractRepository<KasperID,AggregateRoot<KasperID>> {

        public TestRepository(EventStore eventStore, EventBus eventBus) {
            super(mock(MetricRegistry.class), eventStore, eventBus);
        }

        @Override
        protected AxonRepositoryFacade<AggregateRoot<KasperID>> createAxonRepository(final MetricRegistry metricRegistry, final AbstractRepository<KasperID,AggregateRoot<KasperID>> repository) { return null; }

        @Override
        protected Optional<AggregateRoot<KasperID>> doLoad(KasperID aggregateIdentifier, Long expectedVersion) {
            return Optional.absent();
        }

        @Override
        protected void doSave(AggregateRoot<KasperID> aggregate) { }

        @Override
        protected void doDelete(AggregateRoot<KasperID> aggregate) { }
    }
}
