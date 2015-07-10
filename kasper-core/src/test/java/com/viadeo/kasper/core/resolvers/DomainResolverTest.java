// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.api.domain.Domain;
import com.viadeo.kasper.api.documentation.XKasperDomain;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DomainResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain {}

    @XKasperUnregistered
    @XKasperDomain( prefix="tst2", label = "TestFoo")
    private static class TestDomain2 implements Domain {}

    @XKasperUnregistered
    @XKasperDomain( prefix="tst3")
    private static class TestDomain3 implements Domain {}

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomain() {
        // Given
        final DomainResolver domainResolver = new DomainResolver();

        // When
        final Optional<Class<? extends Domain>> domain = domainResolver.getDomainClass(TestDomain.class);

        // Then
        assertTrue(domain.isPresent());
        assertEquals(TestDomain.class, domain.get());
    }

    //-------------------------------------------------------------------------

    @Test
    public void testGetDomainLabelWithNonDecoratedDomain() {
        // Given
        final DomainResolver domainResolver = new DomainResolver();

        // When
        final String label = domainResolver.getLabel(TestDomain.class);
        final String labelIndirect = domainResolver.getDomainLabel(TestDomain.class);

        // Then
        assertEquals("Test", label);
        assertEquals(label, labelIndirect);
    }

    @Test
    public void testGetDomainLabelWitDecoratedAndSetDomain() {
        // Given
        final DomainResolver domainResolver = new DomainResolver();

        // When
        final String label = domainResolver.getLabel(TestDomain2.class);
        final String labelIndirect = domainResolver.getDomainLabel(TestDomain2.class);

        // Then
        assertEquals("TestFoo", label);
        assertEquals(label, labelIndirect);
    }

    @Test
    public void testGetDomainLabelWitDecoratedAndNotSetDomain() {
        // Given
        final DomainResolver domainResolver = new DomainResolver();

        // When
        final String label = domainResolver.getLabel(TestDomain3.class);
        final String labelIndirect = domainResolver.getDomainLabel(TestDomain3.class);

        // Then
        assertEquals("Test3", label);
        assertEquals(label, labelIndirect);
    }

}
