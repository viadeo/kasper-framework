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
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EntityResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain { }

    @XKasperUnregistered
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
    private static class TestRelation implements Relation {
        @Override
        public KasperID getSourceIdentifier() { return null; }
        @Override
        public KasperID getTargetIdentifier() { return null; }
        @Override
        public boolean isBidirectional() { return false; }
        @Override
        public Domain getDomain() { return null; }
        @Override
        public void setDomainLocator(DomainLocator domainLocator) { }
        @Override
        public DateTime getCreationDate() { return null; }
        @Override
        public DateTime getModificationDate() { return null; }
        @Override
        public <I extends KasperID> I  getEntityId() { return null; }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomainWithConcept() {
        // Given
        final EntityResolver resolver = new EntityResolver();
        final ConceptResolver conceptResolver = mock(ConceptResolver.class);

        resolver.setConceptResolver(conceptResolver);

        when( conceptResolver.getDomain(TestConcept.class) )
                .thenReturn(Optional.<Class<? extends Domain>>of(TestDomain.class));

        // When
        final Optional<Class<? extends Domain>> domain = resolver.getDomain(TestConcept.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());
    }

    @Test
    public void testGetDomainWithRelation() {
        // Given
        final EntityResolver resolver = new EntityResolver();
        final RelationResolver relationResolver = mock(RelationResolver.class);

        resolver.setRelationResolver(relationResolver);

        when( relationResolver.getDomain(TestRelation.class) )
                .thenReturn(Optional.<Class<? extends Domain>>of(TestDomain.class));

        // When
        final Optional<Class<? extends Domain>> domain = resolver.getDomain(TestRelation.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());
    }

}
