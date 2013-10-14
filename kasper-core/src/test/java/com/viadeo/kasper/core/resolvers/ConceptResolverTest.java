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
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConceptResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain { }

    @XKasperUnregistered
    @XKasperConcept(domain = TestDomain.class, label = "TestConcept")
    private static class TestConcept implements Concept {
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
    }

    @XKasperUnregistered
    private static class TestConcept2 implements Concept {
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
    }

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
