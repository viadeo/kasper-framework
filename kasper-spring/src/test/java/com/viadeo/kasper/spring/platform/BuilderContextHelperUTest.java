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
import com.viadeo.kasper.platform.builder.BuilderContext;
import com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration;
import org.axonframework.eventhandling.EventBus;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BuilderContextHelperUTest {

    @Test( expected = NullPointerException.class)
    public void createApplicationContextFrom_nullAsBuilderContext_shouldThrowException() {
        // Given
        final BuilderContext builderContext = null;

        // When
        BuilderContextHelper.createApplicationContextFrom(builderContext);

        // Then throws an exception
    }

    @Test
    public void createApplicationContextFrom_validBuilderContext_shouldCreateAnApplicationContext() {
        // Given
        final KasperPlatformConfiguration platformConfiguration = new KasperPlatformConfiguration();

        final BuilderContext builderContext = new BuilderContext(
            platformConfiguration.configuration(),
            platformConfiguration.eventBus(),
            platformConfiguration.commandGateway(),
            platformConfiguration.queryGateway(),
            platformConfiguration.metricRegistry(),
            Lists.<ExtraComponent>newArrayList()
        );

        // When
        final ApplicationContext applicationContext = BuilderContextHelper.createApplicationContextFrom(builderContext);

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

        final BuilderContext builderContext = new BuilderContext(
            platformConfiguration.configuration(),
            platformConfiguration.eventBus(),
            platformConfiguration.commandGateway(),
            platformConfiguration.queryGateway(),
            platformConfiguration.metricRegistry(),
            extraComponents
        );

        // When
        final ApplicationContext applicationContext = BuilderContextHelper.createApplicationContextFrom(builderContext);

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

        final BuilderContext builderContext = new BuilderContext(
            platformConfiguration.configuration(),
            platformConfiguration.eventBus(),
            platformConfiguration.commandGateway(),
            platformConfiguration.queryGateway(),
            platformConfiguration.metricRegistry(),
            extraComponents
        );

        // When
        ApplicationContext applicationContext = BuilderContextHelper.createApplicationContextFrom(builderContext);

        // Then
        assertNotNull(applicationContext);
        assertEquals(workers1, applicationContext.getBean(name1));
        assertEquals(workers2, applicationContext.getBean(name2));
    }
}
