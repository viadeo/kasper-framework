// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.resolvers.CommandResolver;
import com.viadeo.kasper.core.resolvers.Resolver;
import com.viadeo.kasper.core.resolvers.ResolverFactory;
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
