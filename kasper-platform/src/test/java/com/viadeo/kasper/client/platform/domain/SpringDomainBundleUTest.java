package com.viadeo.kasper.client.platform.domain;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class SpringDomainBundleUTest {

    private static Platform.BuilderContext platformBuilderContext;

    @BeforeClass
    public static void setup(){
        platformBuilderContext = new Platform.BuilderContext(
                mock(Config.class)
                , mock(KasperEventBus.class)
                , mock(CommandGateway.class)
                , mock(QueryGateway.class)
                , mock(MetricRegistry.class)
                , Maps.<Platform.ExtraComponentKey, Object>newHashMap()
        );
    }

    @Test
    public void configure_shouldBeOk() {
        // Given
        SpringDomainBundle springDomainBundle = new SpringDomainBundle(
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
        SpringDomainBundle springDomainBundle = new SpringDomainBundle(
                new MyCustomDomainBox.MyCustomDomain(),
                Lists.<Class>newArrayList(FakeConfiguration.class)
        );

        // When
        springDomainBundle.configure(platformBuilderContext);

        // Then
        Optional<MyCustomDomainBox.MyCustomCommandHandler> commandHandlerOptional = springDomainBundle.get(MyCustomDomainBox.MyCustomCommandHandler.class);
        assertNotNull(commandHandlerOptional);
        assertTrue(commandHandlerOptional.isPresent());
    }

    @Test
    public void configure_withNamedBean_shouldBeAccessibleThroughDomainContext() {
        // Given
        String beanName = "hihihi";
        MyCustomDomainBox.MyCustomEventListener expectedEventListener = new MyCustomDomainBox.MyCustomEventListener();
        SpringDomainBundle springDomainBundle = new SpringDomainBundle(
                new MyCustomDomainBox.MyCustomDomain(),
                Lists.<Class>newArrayList(),
                new SpringDomainBundle.BeanDescriptor(beanName, expectedEventListener)
        );

        // When
        springDomainBundle.configure(platformBuilderContext);

        // Then

        Optional<MyCustomDomainBox.MyCustomEventListener> eventListenerOptional = springDomainBundle.get(MyCustomDomainBox.MyCustomEventListener.class);
        assertNotNull(eventListenerOptional);
        assertTrue(eventListenerOptional.isPresent());
        assertEquals(expectedEventListener, eventListenerOptional.get());
    }

    @Test
    public void configure_withBean_shouldBeAccessibleThroughDomainContext() {
        // Given
        DateFormatter dateFormatter = new DateFormatter();
        SpringDomainBundle springDomainBundle = new SpringDomainBundle(
                new MyCustomDomainBox.MyCustomDomain(),
                Lists.<Class>newArrayList(),
                new SpringDomainBundle.BeanDescriptor(DefaultFormatter.class, dateFormatter)
        );

        // When
        springDomainBundle.configure(platformBuilderContext);

        // Then
        Optional<DefaultFormatter> formatterOptional = springDomainBundle.get(DefaultFormatter.class);
        assertNotNull(formatterOptional);
        assertTrue(formatterOptional.isPresent());
        assertEquals(dateFormatter, formatterOptional.get());

        Optional<DateFormatter> formatterOptional2 = springDomainBundle.get(DateFormatter.class);
        assertNotNull(formatterOptional2);
        assertTrue(formatterOptional2.isPresent());
        assertEquals(dateFormatter, formatterOptional2.get());
    }

    @Configuration
    public static class FakeConfiguration {
        @Bean
        public MyCustomDomainBox.MyCustomCommandHandler myCustomCommandHandler(){
            return new MyCustomDomainBox.MyCustomCommandHandler();
        }
    }
}
