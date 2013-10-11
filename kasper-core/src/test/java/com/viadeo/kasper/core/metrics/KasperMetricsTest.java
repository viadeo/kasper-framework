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
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@Ignore
public class KasperMetricsTest {

    @XKasperUnregistered
    private static class TestCommand implements Command { }

    // ------------------------------------------------------------------------

    @Test
    public void testNameFromString() {
        // Given
        final KasperMetrics kasperMetrics = new KasperMetrics();
        kasperMetrics._setNamePrefix("dummy");

        // When
        final String name = kasperMetrics._name("a", "b");

        // Then
        assertEquals("dummy.a.b", name);
    }

    @Test
    public void testNameFromClass() {
        // Given
        final KasperMetrics kasperMetrics = new KasperMetrics();
        kasperMetrics._setNamePrefix("");

        // When
        final String name = kasperMetrics._name(this.getClass(), "b");

        // Then
        assertEquals(this.getClass().getName() + ".b", name);
    }

    @Test
    public void testKasperPathDefault() {
        // Given
        final KasperMetrics kasperMetrics = new KasperMetrics();
        kasperMetrics._unsetResolverFactory();

        // When
        kasperMetrics._clearCache();
        final String path = kasperMetrics._pathForKasperComponent(TestCommand.class);

        // Then
        assertEquals(TestCommand.class.getName(), path);
    }

    @Test
    public void testKasperPathWithDomainResolver() {
        // Given
        final KasperMetrics kasperMetrics = new KasperMetrics();
        final ResolverFactory resolverFactory = mock(ResolverFactory.class);
        final CommandResolver commandResolver = mock(CommandResolver.class);

        when( resolverFactory.getResolverFromClass(TestCommand.class) )
                .thenReturn( Optional.<Resolver>of(commandResolver) );

        when( commandResolver.getTypeName() )
                .thenReturn( "Command" );

        when(commandResolver.getDomainLabel(TestCommand.class))
                .thenReturn("Test");

        kasperMetrics._setResolverFactory(resolverFactory);

        // When
        kasperMetrics._clearCache();
        final String path = kasperMetrics._pathForKasperComponent(TestCommand.class);

        // Then
        assertEquals("Test.Command." + TestCommand.class.getSimpleName(), path);
        verify(commandResolver, times(1)).getDomainLabel(TestCommand.class);
        verify(commandResolver, times(1)).getTypeName();
        verifyNoMoreInteractions(commandResolver);
    }

}
