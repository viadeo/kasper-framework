package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.repository.ClientRepository;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.exception.KasperException;
import org.junit.Assert;
import org.junit.Test;

public class DefaultRepositoryManagerUTest {

    private final DefaultRepositoryManager repositoryManager;

    public DefaultRepositoryManagerUTest(){
        repositoryManager = new DefaultRepositoryManager();
    }

    @Test(expected = NullPointerException.class)
    public void register_withNullAsRepository_shouldThrownException() {
        // Given
        Repository repository = null;

        // When
        repositoryManager.register(repository);

        // Then throws an exception
    }

    @Test(expected = KasperException.class)
    public void register_withUninitializedRepository_shouldBeRegistered() {
        // Given
        Repository repository = new DummyRepository();

        // When
        repositoryManager.register(repository);

        //Then throws an exception

    }

    @Test
    public void register_withRepository_shouldBeRegistered() {
        // Given
        Repository repository = new DummyRepository();
        repository.init();

        // When
        repositoryManager.register(repository);

        // Then
        Assert.assertTrue(repositoryManager.isRegistered(repository));
    }

    @Test
    public void getEntityRepository_withUnknownAggregate_shouldReturnAnOptionalAbsent() {
        // Given nothing

        // When
        Optional<ClientRepository<DummyConcept>> optional = repositoryManager.getEntityRepository(DummyConcept.class);

        // Then
        Assert.assertNotNull(optional);
        Assert.assertFalse(optional.isPresent());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getEntityRepository_withAggregateOfARegisteredRepository_shouldReturnWrappedRepository() {
        // Given
        Repository repository = new DummyRepository();
        repository.init();

        repositoryManager.register(repository);

        // When
        Optional<ClientRepository<DummyConcept>> optional = repositoryManager.getEntityRepository(repository.getAggregateClass());

        // Then
        Assert.assertNotNull(optional);
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(repository, optional.get().business());
    }

    private static class DummyConcept extends Concept { }

    private static class DummyRepository extends Repository<Concept> {
        @Override
        protected Optional<Concept> doLoad(KasperID aggregateIdentifier, Long expectedVersion) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void doSave(Concept aggregate) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void doDelete(Concept aggregate) {
            throw new UnsupportedOperationException();
        }
    }
}
