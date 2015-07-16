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
import com.viadeo.kasper.core.component.command.aggregate.LinkedConcept;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperConcept;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConceptResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain { }

    @XKasperUnregistered
    @XKasperConcept(domain = TestDomain.class, label = "TestConcept")
    private static class TestConcept extends Concept {
        LinkedConcept<TestConcept2> linkedTo;
    }

    @XKasperUnregistered
    private static class TestConcept2 extends Concept { }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomainWithDecoratedConcept() {
        // Given
        final ConceptResolver resolver = new ConceptResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestConcept.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());
    }

    @Test
    public void testGetDomainWithNonDecoratedConcept() {
        // Given
        final ConceptResolver resolver = new ConceptResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomainClass(TestConcept2.class);

        // Then
        assertFalse(domain.isPresent());
    }

}
