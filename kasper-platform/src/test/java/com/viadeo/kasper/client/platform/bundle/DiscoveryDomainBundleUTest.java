// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.bundle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.client.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.client.platform.bundle.sample.MyCustomDomainBox;
import com.viadeo.kasper.client.platform.bundle.sample2.SampleDomainBox2;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.viadeo.kasper.client.platform.Platform.BuilderContext;
import static com.viadeo.kasper.client.platform.Platform.ExtraComponentKey;
import static com.viadeo.kasper.client.platform.bundle.SpringDomainBundle.BeanDescriptor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DiscoveryDomainBundleUTest {

    @Configuration
    public static class InfraConfiguration {
        @Bean
        public SampleDomainBox2.Infra infra() {
            return new SampleDomainBox2.Infra();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void init_withInvalidBasePackage_throwException() {
        // Given
        final String basePackage = "foo.bar";

        // When
        new DiscoveryDomainBundle(basePackage);

        // Then throws an exception
    }

    @Test(expected = IllegalStateException.class)
    public void init_withValidBasePackage_containingTwoDomains_throwException() {
        // Given
        final String basePackage = DiscoveryDomainBundleUTest.class.getPackage().getName();

        // When
        new DiscoveryDomainBundle(basePackage);

        // Then throws an exception
    }

    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
    @Test
    public void init_withValidBasePackage_containingOnlyOneDomain_isOk() {
        // Given
        final String basePackage = MyCustomDomainBox.class.getPackage().getName();

        // When
        final DiscoveryDomainBundle domainBundle = new DiscoveryDomainBundle(basePackage);

        // Then
        assertNotNull(domainBundle);

        assertEquals(1, domainBundle.commandHandlerClasses.size());
        assertEquals(MyCustomDomainBox.MyCustomCommandHandler.class, domainBundle.commandHandlerClasses.iterator().next());
        assertEquals(1, domainBundle.queryHandlerClasses.size());
        assertEquals(MyCustomDomainBox.MyCustomQueryHandler.class, domainBundle.queryHandlerClasses.iterator().next());
        assertEquals(1, domainBundle.eventListenerClasses.size());
        assertEquals(MyCustomDomainBox.MyCustomEventListener.class, domainBundle.eventListenerClasses.iterator().next());
        assertEquals(1, domainBundle.repositoryClasses.size());
        assertEquals(MyCustomDomainBox.MyCustomRepository.class, domainBundle.repositoryClasses.iterator().next());
    }

    @Test
    public void configure_withMyCustomDomain_shouldBeOk() {
        // Given
        final String basePackage = MyCustomDomainBox.class.getPackage().getName();
        final DiscoveryDomainBundle domainBundle = new DiscoveryDomainBundle(basePackage);

        final BuilderContext context = new BuilderContext(new KasperPlatformConfiguration(), Maps.<ExtraComponentKey, Object>newHashMap());

        // When
        domainBundle.configure(context);

        // Then
        assertNotNull(domainBundle);
        assertEquals(1, domainBundle.getCommandHandlers().size());
        assertEquals(1, domainBundle.getQueryHandlers().size());
        assertEquals(1, domainBundle.getEventListeners().size());
        assertEquals(1, domainBundle.getRepositories().size());
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void configure_withSampleDomainBox_throwException() {
        // Given
        final String basePackage = SampleDomainBox2.class.getPackage().getName();
        final BuilderContext context = new BuilderContext(new KasperPlatformConfiguration(), Maps.<ExtraComponentKey, Object>newHashMap());
        final DiscoveryDomainBundle domainBundle = new DiscoveryDomainBundle(basePackage);

        // When
        domainBundle.configure(context);

        // Then throws an exception
    }

    @Test
    public void configure_withSampleDomainBox_withBeanDescriptor_shouldBeOk() {
        // Given
        final String basePackage = SampleDomainBox2.class.getPackage().getName();
        final BuilderContext context = new BuilderContext(new KasperPlatformConfiguration(), Maps.<ExtraComponentKey, Object>newHashMap());
        final BeanDescriptor beanDescriptor = new BeanDescriptor(SampleDomainBox2.Infra.class, new SampleDomainBox2.Infra());
        final DiscoveryDomainBundle domainBundle = new DiscoveryDomainBundle(basePackage, beanDescriptor);

        // When
        domainBundle.configure(context);

        // Then
        assertNotNull(domainBundle);
        assertEquals(1, domainBundle.getCommandHandlers().size());
    }

    @Test
    public void configure_withSampleDomainBox_withConfiguration_shouldBeOk() {
        // Given
        final String basePackage = SampleDomainBox2.class.getPackage().getName();
        final BuilderContext context = new BuilderContext(new KasperPlatformConfiguration(), Maps.<ExtraComponentKey, Object>newHashMap());
        final DiscoveryDomainBundle domainBundle = new DiscoveryDomainBundle(basePackage, Lists.<Class>newArrayList(InfraConfiguration.class));

        // When
        domainBundle.configure(context);

        // Then
        assertNotNull(domainBundle);
        assertEquals(1, domainBundle.getCommandHandlers().size());
    }

}
