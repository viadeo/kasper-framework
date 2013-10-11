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
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
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
        @Override
        public Domain getDomain() { return null; }
        @Override
        public void setDomainLocator(DomainLocator domainLocator) { }
        @Override
        public DateTime getCreationDate() { return null; }
        @Override
        public DateTime getModificationDate() { return null; }
        @Override
        public <I extends KasperID> I getEntityId() { return null; }
    }

    @XKasperUnregistered
    private static class TestRelation2 implements Relation {
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
    public void testGetDomainWithDecoratedRelation() {
        // Given
        final RelationResolver resolver = new RelationResolver();

        // When
        final Optional<Class<? extends Domain>> domain =
                resolver.getDomain(TestRelation.class);

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
                resolver.getDomain(TestRelation2.class);

        // Then
        assertFalse(domain.isPresent());
    }

}
