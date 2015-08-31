// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.resolvers.CommandResolver;
import com.viadeo.kasper.core.resolvers.Resolver;
import com.viadeo.kasper.core.resolvers.ResolverFactory;
import com.viadeo.kasper.api.component.command.Command;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class KasperMetricsTest {

    @XKasperUnregistered
    private static class TestCommand implements Command {
        private static final long serialVersionUID = -6923557995485418425L;
    }

    // ------------------------------------------------------------------------

    public KasperMetricsTest(){
        KasperMetrics.setMetricRegistry(new MetricRegistry());
    }

    @Test
    public void name_fromString_shouldBeOk() {
        // Given
        final KasperMetrics kasperMetrics = new KasperMetrics();
        kasperMetrics._setNamePrefix("dummy");

        // When
        final String name = kasperMetrics._name("a", "b");

        // Then
        assertEquals("dummy.a.b", name);
    }

    @Test
    public void name_fromClass_shouldBeOk() {
        // Given
        final KasperMetrics kasperMetrics = new KasperMetrics();
        kasperMetrics._setNamePrefix("");

        // When
        final String name = kasperMetrics._name(this.getClass(), "b");

        // Then
        assertEquals((this.getClass().getName() + ".b").toLowerCase(), name);
    }

    @Test
    public void name_fromCommandHandler_shouldBeOk() {
        // Given
        final KasperMetrics kasperMetrics = new KasperMetrics();
        kasperMetrics._unsetResolverFactory();

        // When
        kasperMetrics._clearCache();
        final String path = kasperMetrics.pathForKasperComponent(MetricNameStyle.DOMAIN_TYPE_COMPONENT, Contexts.empty(), TestCommand.class);

        // Then
        assertEquals(TestCommand.class.getName().toLowerCase(), path);
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
        final String path = kasperMetrics.pathForKasperComponent(MetricNameStyle.DOMAIN_TYPE_COMPONENT, Contexts.empty(), TestCommand.class);

        // Then
        assertEquals(("Test.Command." + TestCommand.class.getSimpleName()).toLowerCase(), path);
        verify(commandResolver, times(1)).getDomainLabel(TestCommand.class);
        verify(commandResolver, times(1)).getTypeName();
        verifyNoMoreInteractions(commandResolver);
    }

}
