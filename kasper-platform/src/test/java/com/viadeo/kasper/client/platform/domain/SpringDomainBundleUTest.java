// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.domain;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.client.platform.domain.sample.MyCustomDomainBox;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.impl.KasperQueryGateway;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class SpringDomainBundleUTest {

    private static Platform.BuilderContext platformBuilderContext;

    @Configuration
    public static class FakeConfiguration {
        @Bean
        public MyCustomDomainBox.MyCustomCommandHandler myCustomCommandHandler(){
            return new MyCustomDomainBox.MyCustomCommandHandler();
        }
    }

    // ------------------------------------------------------------------------

    @BeforeClass
    public static void setup() {
        platformBuilderContext = new Platform.BuilderContext(
                mock(Config.class),
                mock(KasperEventBus.class),
                mock(CommandGateway.class),
                mock(QueryGateway.class),
                mock(MetricRegistry.class),
                Maps.<Platform.ExtraComponentKey, Object>newHashMap()
        );
    }

    @Test
    public void configure_shouldBeOk() {
        // Given
        final SpringDomainBundle springDomainBundle = new SpringDomainBundle(
                new MyCustomDomainBox.MyCustomDomain(),
                Lists.<Class>newArrayList()
        );

        // When
        springDomainBundle.configure(platformBuilderContext);

        // Then throws no exception
    }

    @Test
    public void configure_withConfiguration_shouldBeAccessibleThroughDomainContext() {
        // Given
        final SpringDomainBundle springDomainBundle = new SpringDomainBundle(
                new MyCustomDomainBox.MyCustomDomain(),
                Lists.<Class>newArrayList(FakeConfiguration.class)
        );

        // When
        springDomainBundle.configure(platformBuilderContext);

        // Then
        final Optional<MyCustomDomainBox.MyCustomCommandHandler> commandHandlerOptional =
                springDomainBundle.get(MyCustomDomainBox.MyCustomCommandHandler.class);
        assertNotNull(commandHandlerOptional);
        assertTrue(commandHandlerOptional.isPresent());
    }

    @Test
    public void configure_withNamedBean_shouldBeAccessibleThroughDomainContext() {
        // Given
        final String beanName = "hihihi";
        final MyCustomDomainBox.MyCustomEventListener expectedEventListener = new MyCustomDomainBox.MyCustomEventListener();
        final SpringDomainBundle springDomainBundle = new SpringDomainBundle(
                new MyCustomDomainBox.MyCustomDomain(),
                Lists.<Class>newArrayList(),
                new SpringDomainBundle.BeanDescriptor(beanName, expectedEventListener)
        );

        // When
        springDomainBundle.configure(platformBuilderContext);

        // Then

        final Optional<MyCustomDomainBox.MyCustomEventListener> eventListenerOptional =
                springDomainBundle.get(MyCustomDomainBox.MyCustomEventListener.class);
        assertNotNull(eventListenerOptional);
        assertTrue(eventListenerOptional.isPresent());
        assertEquals(expectedEventListener, eventListenerOptional.get());
    }

    @Test
    public void configure_withBean_shouldBeAccessibleThroughDomainContext() {
        // Given
        final DateFormatter dateFormatter = new DateFormatter();
        final SpringDomainBundle springDomainBundle = new SpringDomainBundle(
                new MyCustomDomainBox.MyCustomDomain(),
                Lists.<Class>newArrayList(),
                new SpringDomainBundle.BeanDescriptor(DefaultFormatter.class, dateFormatter)
        );

        // When
        springDomainBundle.configure(platformBuilderContext);

        // Then
        final Optional<DefaultFormatter> formatterOptional = springDomainBundle.get(DefaultFormatter.class);
        assertNotNull(formatterOptional);
        assertTrue(formatterOptional.isPresent());
        assertEquals(dateFormatter, formatterOptional.get());

        final Optional<DateFormatter> formatterOptional2 = springDomainBundle.get(DateFormatter.class);
        assertNotNull(formatterOptional2);
        assertTrue(formatterOptional2.isPresent());
        assertEquals(dateFormatter, formatterOptional2.get());
    }

    @Test
    public void configure_withComponentsDefinedInTheBuilderContext_shouldBeAccessibleThroughDomainContext() {
        // Given
        final SpringDomainBundle springDomainBundle = new SpringDomainBundle(
                new MyCustomDomainBox.MyCustomDomain(),
                Lists.<Class>newArrayList()
        );

        final ExecutorService workers = Executors.newFixedThreadPool(2);

        final Map<Platform.ExtraComponentKey, Object> extraComponents = Maps.newHashMap();
        extraComponents.put(new Platform.ExtraComponentKey("workers", ExecutorService.class), workers);

        final KasperPlatformConfiguration platformConfiguration = new KasperPlatformConfiguration();

        final Platform.BuilderContext builderContext = new Platform.BuilderContext(
                platformConfiguration,
                extraComponents
        );

        // When
        springDomainBundle.configure(builderContext);

        // Then
        assertEquals(workers, springDomainBundle.get(ExecutorService.class).get());
        assertEquals(platformConfiguration.eventBus(), springDomainBundle.get(KasperEventBus.class).get());
        assertEquals(platformConfiguration.commandGateway(), springDomainBundle.get(KasperCommandGateway.class).get());
        assertEquals(platformConfiguration.queryGateway(), springDomainBundle.get(KasperQueryGateway.class).get());
        assertEquals(platformConfiguration.configuration(), springDomainBundle.get(Config.class).get());
        assertEquals(platformConfiguration.metricRegistry(), springDomainBundle.get(MetricRegistry.class).get());
    }

}
