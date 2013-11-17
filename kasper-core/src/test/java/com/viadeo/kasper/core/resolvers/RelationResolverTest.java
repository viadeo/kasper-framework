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
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.er.RootConcept;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.domain.EventRegistrationCallback;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class RelationResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain { }

    @XKasperUnregistered
    @XKasperRelation(domain = TestDomain.class, label = "TestRelation")
    private static class TestRelation implements Relation {
        @Override
        public KasperID getSourceIdentifier() { return null; }
        @Override
        public KasperID getTargetIdentifier() { return null; }
        @Override
        public boolean isBidirectional() { return false; }
    }

    @XKasperUnregistered
    private static class TestRootConceptSource implements RootConcept {
        @Override
        public void initializeState(DomainEventStream domainEventStream) { }
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
        public DateTime getCreationDate() { return null; }
        @Override
        public DateTime getModificationDate() { return null; }
        @Override
        public void setVersion(Long version) { }
        @Override
        public KasperID getEntityId() { return null; }
    }

    @XKasperUnregistered
    private static class TestRootConceptTarget extends TestRootConceptSource { }

    @XKasperUnregistered
    private static class TestRelation2 implements Relation<TestRootConceptSource, TestRootConceptTarget> {
        @Override
        public KasperID getSourceIdentifier() { return null; }
        @Override
        public KasperID getTargetIdentifier() { return null; }
        @Override
        public boolean isBidirectional() { return false; }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomainWithDecoratedRelation() {
        // Given
        final RelationResolver resolver = new RelationResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestRelation.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());
    }

    @Test
    public void testGetDomainWithNonDecoratedRelation() {
        // Given
        final RelationResolver resolver = new RelationResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestRelation2.class);

        // Then
        assertFalse(domain.isPresent());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetSourceWithValidRelation() {
        // Given
        final RelationResolver resolver = new RelationResolver();

        // When
        final Class<? extends RootConcept> conceptClass =
                resolver.getSourceEntityClass(TestRelation2.class);

        // Then
        assertEquals(TestRootConceptSource.class, conceptClass);
    }

    @Test
    public void testGetSourceWithInvalidRelation() {
        // Given
        final RelationResolver resolver = new RelationResolver();

        // When
        try {
            final Class<? extends RootConcept> conceptClass =
                    resolver.getSourceEntityClass(TestRelation.class);
            fail();
        } catch (final KasperException e) {
            // Then should raise exception
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetTargetWithValidRelation() {
        // Given
        final RelationResolver resolver = new RelationResolver();

        // When
        final Class<? extends RootConcept> conceptClass =
                resolver.getTargetEntityClass(TestRelation2.class);

        // Then
        assertEquals(TestRootConceptTarget.class, conceptClass);
    }

    @Test
    public void testGetTargetWithInvalidRelation() {
        // Given
        final RelationResolver resolver = new RelationResolver();

        // When
        try {
            final Class<? extends RootConcept> conceptClass =
                    resolver.getTargetEntityClass(TestRelation.class);
            fail();
        } catch (final KasperException e) {
            // Then should raise exception
        }
    }

}
