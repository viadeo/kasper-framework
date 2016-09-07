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
package com.viadeo.kasper.spring.core;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.component.command.RepositoryManager;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import com.viadeo.kasper.core.resolvers.DomainHelper;
import com.viadeo.kasper.platform.Build;
import com.viadeo.kasper.platform.ExtraComponent;
import com.viadeo.kasper.platform.Meta;
import com.viadeo.kasper.platform.PlatformWirer;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import com.viadeo.kasper.platform.bundle.descriptor.DescriptorRegistry;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptor;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptorFactory;
import com.viadeo.kasper.platform.plugin.Plugin;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.DefaultLifecycleProcessor;

import java.util.Collections;
import java.util.List;

@Configuration
public class KasperPlatformConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperPlatformConfiguration.class);

    @Autowired(required = false)
    List<ExtraComponent> extraComponents;

    @Autowired(required = false)
    List<DomainBundle> bundles;

    @Autowired(required = false)
    List<Plugin> plugins;

    /**
     * Register lifecycle processor (using spring smart lifecycle)
     * In order to get a proper start/shutdown sequence
     *
     * @return lifecycle processor
     */
    @Bean
    public LifecycleProcessor lifecycleProcessor() {
        return new DefaultLifecycleProcessor();
    }

    @Bean
    public PlatformWirer platformWirer(
            final Config config,
            final MetricRegistry metricRegistry,
            final KasperEventBus eventBus,
            final KasperCommandGateway commandGateway,
            final KasperQueryGateway queryGateway,
            final SagaManager sagaManager,
            final RepositoryManager repositoryManager,
            final Meta meta

    ) {
        final PlatformWirer platformWirer = new PlatformWirer(config, metricRegistry, eventBus, commandGateway, queryGateway, sagaManager, repositoryManager, meta);

        if (extraComponents != null) {
            for (final ExtraComponent extraComponent : extraComponents) {
                platformWirer.register(extraComponent);
            }
        }

        if (plugins != null) {
            List<Plugin> toWirer = Lists.newArrayList(plugins);
            Collections.sort(toWirer, Plugin.REVERSED_COMPARATOR);
            for (final Plugin plugin : toWirer) {
                platformWirer.wire(plugin);
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
     * @return Descriptor registry
     */
    @Bean
    public DescriptorRegistry descriptorRegistry(
            final PlatformWirer platformWirer,
            final DomainHelper domainHelper
    ) {
        final List<DomainDescriptor> descriptors = Lists.newArrayList();

        for (final DomainBundle bundle : Objects.firstNonNull(bundles, Lists.<DomainBundle>newArrayList())) {

            LOGGER.debug("Configuring bundle : {}", bundle.getName());

            final DomainDescriptor domainDescriptor = platformWirer.wire(bundle);
            domainHelper.add(DomainDescriptorFactory.mapToDomainClassByComponentClass(domainDescriptor));
            descriptors.add(domainDescriptor);
        }

        return new DescriptorRegistry(descriptors);
    }

    @Bean
    public Build.Info info(ObjectMapper objectMapper) {
        return Build.info(objectMapper);
    }

    @Bean
    public Meta meta(final Build.Info info) {
        return new Meta(
                info.getRevision(),
                info.getTime(),
                DateTime.now()
        );
    }
}