// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.repository;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.impl.DefaultKasperId;
import org.axonframework.repository.AggregateNotFoundException;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ClientRepositoryTest {

    @Test
    public void testBusiness_shouldReturnDecoratedRepository() {
        // Given
        final IRepository decoratedRepo = mock(IRepository.class);
        final ClientRepository repo = new ClientRepository(decoratedRepo);

        // When
        final IRepository business = repo.business();

        // Then
        assertSame(business, decoratedRepo);

    }

    // ------------------------------------------------------------------------

    @Test
    public void testLoadOnUnexistentAggregate_shouldReturnOptionalAbsent() {
        // Given
        final IRepository decoratedRepo = mock(IRepository.class);
        final ClientRepository repo = new ClientRepository(decoratedRepo);
        doThrow(new AggregateNotFoundException(null, ""))
                .when(decoratedRepo)
                .load(any(Object.class), any(Long.class));

        // When
        final Optional ret = repo.load(DefaultKasperId.random(),  0L);

        // Then
        assertFalse(ret.isPresent());
    }

    @Test
    public void testLoadExistentAggregate_shouldReturnOptionalPresent() {
        // Given
        final IRepository decoratedRepo = mock(IRepository.class);
        final ClientRepository repo = new ClientRepository(decoratedRepo);
        final AggregateRoot agr = mock(AggregateRoot.class);
        doReturn(agr)
                .when(decoratedRepo)
                .load(any(Object.class), any(Long.class));

        // When
        final Optional ret = repo.load(DefaultKasperId.random(),  0L);

        // Then
        assertTrue(ret.isPresent());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testLoadWithAbsentVersion_shouldCallLoadWithoutVersion() {
        // Given
        final IRepository decoratedRepo = mock(IRepository.class);
        final ClientRepository repo = new ClientRepository(decoratedRepo);
        final AggregateRoot agr = mock(AggregateRoot.class);
        doReturn(agr)
                .when(decoratedRepo)
                .load(any(Object.class));

        // When
        repo.load(DefaultKasperId.random(), Optional.absent());

        // Then
        verify(decoratedRepo).load(any(Object.class));
    }

    @Test
    public void testLoadWithVersion_shouldCallLoadWithThisVersion() {
        // Given
        final IRepository decoratedRepo = mock(IRepository.class);
        final ClientRepository repo = new ClientRepository(decoratedRepo);
        final AggregateRoot agr = mock(AggregateRoot.class);
        final Long version = 42L;
        doReturn(agr)
                .when(decoratedRepo)
                .load(any(Object.class), eq(version));

        // When
        repo.load(DefaultKasperId.random(), Optional.of(version));

        // Then
        verify(decoratedRepo).load(any(Object.class), eq(version));
    }

    // ------------------------------------------------------------------------

    @Test
    public void testLoadOnUnexistentAggregateWithoutVersion_shouldReturnOptionalAbsent() {
        // Given
        final IRepository decoratedRepo = mock(IRepository.class);
        final ClientRepository repo = new ClientRepository(decoratedRepo);
        doThrow(new AggregateNotFoundException(null, ""))
                .when(decoratedRepo)
                .load(any(Object.class));

        // When
        final Optional ret = repo.load(DefaultKasperId.random());

        // Then
        assertFalse(ret.isPresent());
    }

    @Test
    public void testLoadExistentAggregateWithoutVersion_shouldReturnOptionalPresent() {
        // Given
        final IRepository decoratedRepo = mock(IRepository.class);
        final ClientRepository repo = new ClientRepository(decoratedRepo);
        final AggregateRoot agr = mock(AggregateRoot.class);
        doReturn(agr)
                .when(decoratedRepo)
                .load(any(Object.class));

        // When
        final Optional ret = repo.load(DefaultKasperId.random());

        // Then
        assertTrue(ret.isPresent());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetOnUnexistentAggregate_shouldReturnOptionalAbsent() {
        // Given
        final IRepository decoratedRepo = mock(IRepository.class);
        final ClientRepository repo = new ClientRepository(decoratedRepo);
        doThrow(new AggregateNotFoundException(null, ""))
                .when(decoratedRepo)
                .get(any(KasperID.class), any(Long.class));

        // When
        final Optional ret = repo.get(DefaultKasperId.random(), 0L);

        // Then
        assertFalse(ret.isPresent());
    }

    @Test
    public void testGetExistentAggregate_shouldReturnOptionalPresent() {
        // Given
        final IRepository decoratedRepo = mock(IRepository.class);
        final ClientRepository repo = new ClientRepository(decoratedRepo);
        final AggregateRoot agr = mock(AggregateRoot.class);
        doReturn(agr)
                .when(decoratedRepo)
                .get(any(KasperID.class), any(Long.class));

        // When
        final Optional ret = repo.get(DefaultKasperId.random(),  0L);

        // Then
        assertTrue(ret.isPresent());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetWithAbsentVersion_shouldCallGetWithoutVersion() {
        // Given
        final IRepository decoratedRepo = mock(IRepository.class);
        final ClientRepository repo = new ClientRepository(decoratedRepo);
        final AggregateRoot agr = mock(AggregateRoot.class);
        doReturn(agr)
                .when(decoratedRepo)
                .get(any(KasperID.class));

        // When
        repo.get(DefaultKasperId.random(), Optional.absent());

        // Then
        verify(decoratedRepo).get(any(KasperID.class));
    }

    @Test
    public void testGetWithVersion_shouldCallGetWithThisVersion() {
        // Given
        final IRepository decoratedRepo = mock(IRepository.class);
        final ClientRepository repo = new ClientRepository(decoratedRepo);
        final AggregateRoot agr = mock(AggregateRoot.class);
        final Long version = 42L;
        doReturn(agr)
                .when(decoratedRepo)
                .get(any(KasperID.class), eq(version));

        // When
        repo.get(DefaultKasperId.random(), Optional.of(version));

        // Then
        verify(decoratedRepo).get(any(KasperID.class), eq(version));
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetOnUnexistentAggregateWithoutVersion_shouldReturnOptionalAbsent() {
        // Given
        final IRepository decoratedRepo = mock(IRepository.class);
        final ClientRepository repo = new ClientRepository(decoratedRepo);
        doThrow(new AggregateNotFoundException(null, ""))
                .when(decoratedRepo)
                .get(any(KasperID.class));

        // When
        final Optional ret = repo.get(DefaultKasperId.random());

        // Then
        assertFalse(ret.isPresent());
    }

    @Test
    public void testGetExistentAggregateWithoutVersion_shouldReturnOptionalPresent() {
        // Given
        final IRepository decoratedRepo = mock(IRepository.class);
        final ClientRepository repo = new ClientRepository(decoratedRepo);
        final AggregateRoot agr = mock(AggregateRoot.class);
        doReturn(agr)
                .when(decoratedRepo)
                .get(any(KasperID.class));

        // When
        final Optional ret = repo.get(DefaultKasperId.random());

        // Then
        assertTrue(ret.isPresent());
    }

}
