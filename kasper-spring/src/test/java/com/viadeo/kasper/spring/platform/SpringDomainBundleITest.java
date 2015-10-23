// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.platform;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptor;
import com.viadeo.kasper.platform.bundle.sample.MyCustomDomainBox;
import com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.platform.plugin.Plugin;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SpringDomainBundleITest {

    private static class SpyPlugin implements Plugin {

        final List<DomainDescriptor> domainDescriptors = Lists.newArrayList();

        @Override
        public void initialize(final Platform platform,
                               final MetricRegistry metricRegistry,
                               final DomainDescriptor... domainDescriptors) {
            this.domainDescriptors.addAll(Lists.newArrayList(domainDescriptors));
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void build_withSpringDomainBundle_usingMyCustomDomainSpringConfiguration_shouldBeOk() {
        // Given
        final SpyPlugin spy = new SpyPlugin();

        final SpringDomainBundle domainBundle = new SpringDomainBundle(
            new MyCustomDomainBox.MyCustomDomain(),
            MyCustomDomainBox.MyCustomDomainSpringConfiguration.class
        );

        final Platform.Builder builder = Platforms.newDefaultBuilder(new KasperPlatformConfiguration())
                .addDomainBundle(domainBundle)
                .addPlugin(spy);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        assertEquals(1, spy.domainDescriptors.size());

        final DomainDescriptor domainDescriptor = spy.domainDescriptors.get(0);

        assertEquals(MyCustomDomainBox.MyCustomDomain.class, domainDescriptor.getDomainClass());
        assertEquals(1, domainDescriptor.getCommandHandlerDescriptors().size());
        assertEquals(1, domainDescriptor.getQueryHandlerDescriptors().size());
        assertEquals(1, domainDescriptor.getEventListenerDescriptors().size());
        assertEquals(1, domainDescriptor.getRepositoryDescriptors().size());
    }

}
