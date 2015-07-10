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
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractResolverTest {

    @XKasperUnregistered
    private static class TestDomain implements Domain {}

    private static class ImplementedAbstractResolver extends AbstractResolver<Object> {
        @Override
        public String getTypeName() { return "Object"; }
        @Override
        public String getLabel(Class<?> clazz) { return null; }
        @Override
        public String getDescription(Class<?> clazz) { return null; }
        @Override
        public Optional<Class<? extends Domain>> getDomainClass(final Class clazz) {
            return Optional.<Class<? extends Domain>>of(TestDomain.class);
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testGetDomain() {
        // Given
        final ImplementedAbstractResolver resolver = new ImplementedAbstractResolver();
        final DomainResolver domainResolver = mock(DomainResolver.class);

        resolver.setDomainResolver(domainResolver);

        when( domainResolver.getLabel(TestDomain.class) )
                .thenReturn( "Test" );

        // When
        final String label = resolver.getDomainLabel(TestDomain.class);

        // Then
        assertEquals("Test", label);
    }

}
