// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.cqrs.command.exceptions.KasperCommandException;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.impl.StringKasperId;
import org.axonframework.unitofwork.CurrentUnitOfWork;
import org.axonframework.unitofwork.DefaultUnitOfWork;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class RepositoryTest {

    @XKasperUnregistered
    private static class TestAggregate extends AbstractAggregateRoot { }

    @XKasperUnregistered
    private static class UnidentifiedAggregate extends TestAggregate { }

    @XKasperUnregistered
    private static class NoCreationDateAggregate extends TestAggregate {
        NoCreationDateAggregate() {
            setId(new DefaultKasperId(UUID.randomUUID()));
        }
    }

    @XKasperUnregistered
    private static class NoModificationDateAggregate extends TestAggregate {
        NoModificationDateAggregate() {
            setId(new DefaultKasperId(UUID.randomUUID()));
            setCreationDate(DateTime.now());
        }
        public void change() {
        }
    }

    @XKasperUnregistered
    private static class TestRepository extends Repository<TestAggregate> {

        @Override
        protected Optional<TestAggregate> doLoad(KasperID aggregateIdentifier, Long expectedVersion) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void doSave(TestAggregate aggregate) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void doDelete(TestAggregate aggregate) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

    }

    // ------------------------------------------------------------------------

    @Test
    public void testNonIdentifiedRepository_shouldRaiseExceptionwhenSaving() {
        // Given
        final UnidentifiedAggregate agr = new UnidentifiedAggregate();
        final TestRepository repo = new TestRepository();
        repo.init();
        CurrentUnitOfWork.set(DefaultUnitOfWork.startAndGet());

        try {
            // When
            repo.add(agr);
            // Should not be valid
            fail();
        } catch (final KasperCommandException e) {
            // Then should raise exception
            CurrentUnitOfWork.commit();;
        }

    }

    // ------------------------------------------------------------------------

    @Test
    public void testRepositoryWithNoCreationDate_shouldGenerateItsOwnCreationDate() {
        // Given
        final NoCreationDateAggregate agr = new NoCreationDateAggregate();
        final TestRepository repo = new TestRepository();
        repo.init();
        CurrentUnitOfWork.set(DefaultUnitOfWork.startAndGet());
        assertNull(agr.getCreationDate());

        // When
        repo.add(agr);
        CurrentUnitOfWork.commit();

        // Then
        assertNotNull(agr.getCreationDate());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testRepositoryWithNoModificationDate_shouldGenerateItsOwnModificationDate() {
        // Given
        final TestRepository repo = spy(new TestRepository());
        repo.init();
        CurrentUnitOfWork.set(DefaultUnitOfWork.startAndGet());

        final NoModificationDateAggregate retAgr = new NoModificationDateAggregate();
        final DateTime originalModificationDate = retAgr.getModificationDate();
        doReturn(Optional.of(retAgr)).when(repo).doLoad(any(KasperID.class), any(Long.class));

        // When
        final TestAggregate agr = repo.load(new StringKasperId("foo"), null);
        CurrentUnitOfWork.commit();

        // Then
        assertNotNull(agr.getCreationDate());
        assertNotNull(agr.getModificationDate());
        assertFalse(agr.getModificationDate().equals(originalModificationDate));
    }

}
