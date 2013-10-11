// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.metrics;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.resolvers.CommandResolver;
import com.viadeo.kasper.core.resolvers.Resolver;
import com.viadeo.kasper.core.resolvers.ResolverFactory;
import com.viadeo.kasper.cqrs.command.Command;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class KasperMetricsTest {

    @XKasperUnregistered
    private static class TestCommand implements Command { }

    // ------------------------------------------------------------------------

    @Test
    public void testNameFromString() {
        // Given
        KasperMetrics.setNamePrefix("dummy");

        // When
        final String name = KasperMetrics.name("a", "b");

        // Then
        assertEquals("dummy.a.b", name);
    }

    @Test
    public void testNameFromClass() {
        // Given
        KasperMetrics.setNamePrefix("");

        // When
        final String name = KasperMetrics.name(this.getClass(), "b");

        // Then
        assertEquals(this.getClass().getName() + ".b", name);
    }

    @Test
    public void testKasperPathDefault() {
        // Given
        KasperMetrics.unsetResolverFactory();

        // When
        KasperMetrics.clearCache();
        final String path = KasperMetrics.pathForKasperComponent(TestCommand.class);

        // Then
        assertEquals(TestCommand.class.getName(), path);
    }

    @Test
    public void testKasperPathWithDomainResolver() {
        // Given
        final ResolverFactory resolverFactory = mock(ResolverFactory.class);
        final CommandResolver commandResolver = mock(CommandResolver.class);

        when( resolverFactory.getResolverFromClass(TestCommand.class) )
                .thenReturn( Optional.<Resolver>of(commandResolver) );

        when( commandResolver.getTypeName() )
                .thenReturn( "Command" );

        when( commandResolver.getDomainLabel(TestCommand.class) )
                .thenReturn( Optional.of("Test") );

        KasperMetrics.setResolverFactory(resolverFactory);

        // When
        KasperMetrics.clearCache();
        final String path = KasperMetrics.pathForKasperComponent(TestCommand.class);

        // Then
        assertEquals("Test.Command." + TestCommand.class.getSimpleName(), path);
        verify(commandResolver, times(1)).getDomainLabel(TestCommand.class);
        verify(commandResolver, times(1)).getTypeName();
        verifyNoMoreInteractions(commandResolver);
    }

}
