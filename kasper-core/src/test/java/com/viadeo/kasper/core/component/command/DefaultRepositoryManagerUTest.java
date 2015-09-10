// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.repository.ClientRepository;
import com.viadeo.kasper.core.component.command.repository.Repository;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultRepositoryManagerUTest {

    private final DefaultRepositoryManager repositoryManager;

    private static class DummyConcept extends Concept { }

    private static class DummyRepository extends Repository<Concept> {
        @Override
        protected Optional<Concept> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void doSave(final Concept aggregate) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void doDelete(final Concept aggregate) {
            throw new UnsupportedOperationException();
        }
    }

    // ------------------------------------------------------------------------

    public DefaultRepositoryManagerUTest() {
        repositoryManager = new DefaultRepositoryManager();
    }

    // ------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void register_withNullAsRepository_shouldThrownException() {
        // Given
        final Repository repository = null;

        // When
        repositoryManager.register(repository);

        // Then throws an exception
    }

    @Test(expected = KasperException.class)
    public void register_withUninitializedRepository_shouldBeRegistered() {
        // Given
        final Repository repository = new DummyRepository();

        // When
        repositoryManager.register(repository);

        // Then throws an exception

    }

    @Test
    public void register_withRepository_shouldBeRegistered() {
        // Given
        final Repository repository = new DummyRepository();
        repository.init();

        // When
        repositoryManager.register(repository);

        // Then
        assertTrue(repositoryManager.isRegistered(repository));
    }

    @Test
    public void getEntityRepository_withUnknownAggregate_shouldReturnAnOptionalAbsent() {
        // Given nothing

        // When
        final Optional<ClientRepository<DummyConcept>> optional =
                repositoryManager.getEntityRepository(DummyConcept.class);

        // Then
        assertNotNull(optional);
        assertFalse(optional.isPresent());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getEntityRepository_withAggregateOfARegisteredRepository_shouldReturnWrappedRepository() {
        // Given
        final Repository repository = new DummyRepository();
        repository.init();
        repositoryManager.register(repository);

        // When
        final Optional<ClientRepository<DummyConcept>> optional =
                repositoryManager.getEntityRepository(repository.getAggregateClass());

        // Then
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(repository, optional.get().business());
    }

}
