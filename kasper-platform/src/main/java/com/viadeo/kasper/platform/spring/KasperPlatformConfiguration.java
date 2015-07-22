// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.spring;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.component.command.RepositoryManager;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import com.viadeo.kasper.core.component.saga.SagaManager;
import com.viadeo.kasper.core.resolvers.DomainHelper;
import com.viadeo.kasper.platform.ExtraComponent;
import com.viadeo.kasper.platform.PlatformWirer;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import com.viadeo.kasper.platform.bundle.descriptor.DescriptorRegistry;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptor;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class KasperPlatformConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperPlatformConfiguration.class);

    @Autowired(required = false)
    List<ExtraComponent> extraComponents;

    @Bean
    public PlatformWirer platformWirer(
            final Config config,
            final MetricRegistry metricRegistry,
            final KasperEventBus eventBus,
            final KasperCommandGateway commandGateway,
            final KasperQueryGateway queryGateway,
            final SagaManager sagaManager,
            final RepositoryManager repositoryManager
    ) {
        final PlatformWirer platformWirer = new PlatformWirer(config, metricRegistry, eventBus, commandGateway, queryGateway, sagaManager, repositoryManager);

        if (extraComponents != null) {
            for (final ExtraComponent extraComponent : extraComponents) {
                platformWirer.register(extraComponent);
            }
        }

        return platformWirer;
    }

    /** KasperDescriptorRegistry*
     * Initialize domain descriptors, with an additional nice side effect on domain helper.
     * This is the main glue code between the framework mess and the platform.
     * **Touch with caution**.
     *
     * @param platformWirer the platform wirer
     * @param domainHelper something strange
     * @param bundles the domains (with isolated command / query contexts depending on this context)
     * @return Descriptor registry
     */
    @Bean
    public DescriptorRegistry descriptorRegistry(
            final PlatformWirer platformWirer,
            final DomainHelper domainHelper,
            final List<DomainBundle> bundles
    ) {
        final List<DomainDescriptor> descriptors = Lists.newArrayList();

        for (final DomainBundle bundle : bundles) {

            LOGGER.debug("Configuring bundle : {}", bundle.getName());

            final DomainDescriptor domainDescriptor = platformWirer.wire(bundle);
            domainHelper.add(DomainDescriptorFactory.mapToDomainClassByComponentClass(domainDescriptor));
            descriptors.add(domainDescriptor);
        }

        return new DescriptorRegistry(descriptors);
    }
}
