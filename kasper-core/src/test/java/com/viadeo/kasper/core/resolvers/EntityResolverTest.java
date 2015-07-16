// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.LinkedConcept;
import com.viadeo.kasper.er.Relation;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class EntityResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain { }

    @XKasperUnregistered
    private static class TestConcept extends Concept {
        LinkedConcept<TestConcept2> linkedTo;
    }

    @XKasperUnregistered
    private static class TestConcept2 extends Concept { }

    @XKasperUnregistered
    private static class TestRelation extends Relation { }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomainWithConcept() {
        // Given
        final EntityResolver resolver = new EntityResolver();
        final ConceptResolver conceptResolver = mock(ConceptResolver.class);

        resolver.setConceptResolver(conceptResolver);

        when(conceptResolver.getDomainClass(TestConcept.class))
                .thenReturn(Optional.<Class<? extends Domain>>of(TestDomain.class));

        // When
        final Optional<Class<? extends Domain>> domain = resolver.getDomainClass(TestConcept.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());

        verify(conceptResolver, times(1)).getDomainClass(TestConcept.class);
        verifyNoMoreInteractions(conceptResolver);
    }

    @Test
    public void testGetDomainWithRelation() {
        // Given
        final EntityResolver resolver = new EntityResolver();
        final RelationResolver relationResolver = mock(RelationResolver.class);

        resolver.setRelationResolver(relationResolver);

        when( relationResolver.getDomainClass(TestRelation.class) )
                .thenReturn(Optional.<Class<? extends Domain>>of(TestDomain.class));

        // When
        final Optional<Class<? extends Domain>> domain = resolver.getDomainClass(TestRelation.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());

        verify(relationResolver, times(1)).getDomainClass(TestRelation.class);
        verifyNoMoreInteractions(relationResolver);
    }

    @Test
    public void testGetComponentConcepts() {
        // Given
        final EntityResolver resolver = new EntityResolver();

        // When
        final List<Class<? extends Concept>> links =
                resolver.getComponentConcepts(TestConcept.class);

        // Then
        assertEquals(1, links.size());
        assertEquals(TestConcept2.class, links.get(0));
    }

}
