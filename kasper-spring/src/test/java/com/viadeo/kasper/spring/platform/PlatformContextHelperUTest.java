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
package com.viadeo.kasper.spring.platform;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;
import com.viadeo.kasper.platform.ExtraComponent;
import com.viadeo.kasper.platform.Meta;
import com.viadeo.kasper.platform.builder.PlatformContext;
import com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration;
import org.axonframework.eventhandling.EventBus;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PlatformContextHelperUTest {

    @Test( expected = NullPointerException.class)
    public void createApplicationContextFrom_nullAsBuilderContext_shouldThrowException() {
        // Given
        final PlatformContext builderContext = null;

        // When
        PlatformContextHelper.createApplicationContextFrom(builderContext);

        // Then throws an exception
    }

    @Test
    public void createApplicationContextFrom_validBuilderContext_shouldCreateAnApplicationContext() {
        // Given
        final KasperPlatformConfiguration platformConfiguration = new KasperPlatformConfiguration();

        final PlatformContext builderContext = new PlatformContext(
                platformConfiguration.configuration(),
                platformConfiguration.eventBus(),
                platformConfiguration.commandGateway(),
                platformConfiguration.queryGateway(),
                platformConfiguration.metricRegistry(),
                Lists.<ExtraComponent>newArrayList(),
                Meta.UNKNOWN
        );

        // When
        final ApplicationContext applicationContext = PlatformContextHelper.createApplicationContextFrom(builderContext);

        // Then
        assertNotNull(applicationContext);
        assertEquals(platformConfiguration.configuration(), applicationContext.getBean(Config.class));
        assertEquals(platformConfiguration.eventBus(), applicationContext.getBean(EventBus.class));
        assertEquals(platformConfiguration.commandGateway(), applicationContext.getBean(CommandGateway.class));
        assertEquals(platformConfiguration.queryGateway(), applicationContext.getBean(QueryGateway.class));
        assertEquals(platformConfiguration.metricRegistry(), applicationContext.getBean(MetricRegistry.class));
    }

    @Test
    public void createApplicationContextFrom_validBuilderContext_containingExtraComponent_shouldCreateAnApplicationContext() {
        // Given
        final String name = "workers";
        final ExecutorService workers = Executors.newFixedThreadPool(2);

        final List<ExtraComponent> extraComponents = Lists.newArrayList();
        extraComponents.add(new ExtraComponent(name, ExecutorService.class, workers));

        final KasperPlatformConfiguration platformConfiguration = new KasperPlatformConfiguration();

        final PlatformContext builderContext = new PlatformContext(
                platformConfiguration.configuration(),
                platformConfiguration.eventBus(),
                platformConfiguration.commandGateway(),
                platformConfiguration.queryGateway(),
                platformConfiguration.metricRegistry(),
                extraComponents,
                Meta.UNKNOWN
        );

        // When
        final ApplicationContext applicationContext = PlatformContextHelper.createApplicationContextFrom(builderContext);

        // Then
        assertNotNull(applicationContext);
        assertEquals(workers, applicationContext.getBean(ExecutorService.class));
        assertEquals(workers, applicationContext.getBean(name));
    }

    @Test
    public void createApplicationContextFrom_validBuilderContext_containingTwoSameExtraComponent_shouldCreateAnApplicationContext() {
        // Given
        final String name1 = "workers1";
        final ExecutorService workers1 = Executors.newFixedThreadPool(2);

        final String name2 = "workers2";
        final ExecutorService workers2 = Executors.newFixedThreadPool(2);

        final List<ExtraComponent> extraComponents = Lists.newArrayList();
        extraComponents.add(new ExtraComponent(name1, ExecutorService.class, workers1));
        extraComponents.add(new ExtraComponent(name2, ExecutorService.class, workers2));

        final KasperPlatformConfiguration platformConfiguration = new KasperPlatformConfiguration();

        final PlatformContext builderContext = new PlatformContext(
                platformConfiguration.configuration(),
                platformConfiguration.eventBus(),
                platformConfiguration.commandGateway(),
                platformConfiguration.queryGateway(),
                platformConfiguration.metricRegistry(),
                extraComponents,
                Meta.UNKNOWN
        );

        // When
        ApplicationContext applicationContext = PlatformContextHelper.createApplicationContextFrom(builderContext);

        // Then
        assertNotNull(applicationContext);
        assertEquals(workers1, applicationContext.getBean(name1));
        assertEquals(workers2, applicationContext.getBean(name2));
    }
}