// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.Relation;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperRelation;
import com.viadeo.kasper.api.exception.KasperException;
import org.junit.Test;

import static org.junit.Assert.*;

public class RelationResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain { }

    @XKasperUnregistered
    @XKasperRelation( domain = TestDomain.class )
    private static class TestRelation extends Relation { }

    @XKasperUnregistered
    private static class TestRootConceptSource extends Concept { }

    @XKasperUnregistered
    private static class TestRootConceptTarget extends TestRootConceptSource { }

    @XKasperUnregistered
    private static class TestRelation2 extends Relation<TestRootConceptSource, TestRootConceptTarget> { }

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
        final Class<? extends Concept> conceptClass =
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
            final Class<? extends Concept> conceptClass =
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
        final Class<? extends Concept> conceptClass =
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
            final Class<? extends Concept> conceptClass =
                    resolver.getTargetEntityClass(TestRelation.class);
            fail();
        } catch (final KasperException e) {
            // Then should raise exception
        }
    }

}
