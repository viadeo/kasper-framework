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
package com.viadeo.kasper.platform.configuration;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;
import com.viadeo.kasper.core.component.command.gateway.ContextualizedUnitOfWork;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandBus;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.command.interceptor.CommandValidationInterceptorFactory;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.event.interceptor.EventValidationInterceptorFactory;
import com.viadeo.kasper.core.component.event.saga.DefaultSagaManager;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.cache.CacheInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.filter.QueryFilterInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.validation.QueryValidationInterceptorFactory;
import com.viadeo.kasper.platform.ExtraComponent;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptorFactory;
import org.axonframework.unitofwork.UnitOfWorkFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static com.viadeo.kasper.core.component.event.eventbus.KasperEventBus.Policy;

/**
 * The KasperPlatformSpringConfiguration class provides default implementation of the components required by the  {@link com.viadeo.kasper.platform.Platform}.
 *
 * @see com.viadeo.kasper.platform.Platform.Builder
 */
public class KasperPlatformConfiguration implements PlatformConfiguration {

    private final KasperEventBus eventBus;
    private final KasperQueryGateway queryGateway;
    private final MetricRegistry metricRegistry;
    private final Config configuration;
    private final KasperCommandGateway commandGateway;
    private final SagaManager sagaManager;
    private final List<ExtraComponent> extraComponents;
    private final List<CommandInterceptorFactory> commandInterceptorFactories;
    private final List<QueryInterceptorFactory> queryInterceptorFactories;
    private final List<EventInterceptorFactory> eventInterceptorFactories;
    private final DomainDescriptorFactory domainDescriptorFactory;

    // ------------------------------------------------------------------------

    public KasperPlatformConfiguration() {
        this(new MetricRegistry());
    }

    public KasperPlatformConfiguration(final MetricRegistry metricRegistry) {
        this.eventBus = new KasperEventBus(new MetricRegistry(), Policy.ASYNCHRONOUS);
        this.queryGateway = new KasperQueryGateway(metricRegistry);
        this.metricRegistry = metricRegistry;
        this.extraComponents = Lists.newArrayList();
        this.configuration = ConfigFactory.empty();

        final UnitOfWorkFactory uowFactory = new ContextualizedUnitOfWork.Factory();
        final KasperCommandBus commandBus = new KasperCommandBus(metricRegistry);
        commandBus.setUnitOfWorkFactory(uowFactory);

        this.commandGateway = new KasperCommandGateway(commandBus);

        this.commandInterceptorFactories = Lists.<CommandInterceptorFactory>newArrayList(
            new CommandValidationInterceptorFactory()
        );

        this.queryInterceptorFactories =  Lists.newArrayList(
            new CacheInterceptorFactory(),
            new QueryValidationInterceptorFactory(),
            new QueryFilterInterceptorFactory()
        );

        this.eventInterceptorFactories = Lists.<EventInterceptorFactory>newArrayList(
            new EventValidationInterceptorFactory()
        );

        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

        this.sagaManager = DefaultSagaManager.build();

        final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.registerSingleton("eventBus", eventBus);
        beanFactory.registerSingleton("queryGateway", queryGateway);
        beanFactory.registerSingleton("commandGateway", commandGateway);
        beanFactory.registerSingleton("configuration", configuration);
        beanFactory.registerSingleton("metricRegistry", metricRegistry);
        beanFactory.registerSingleton("objectMapper", ObjectMapperProvider.INSTANCE.mapper());
        applicationContext.refresh();

        this.domainDescriptorFactory = new DomainDescriptorFactory(this.sagaManager.getStepProcessor());
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperEventBus eventBus() {
        return eventBus;
    }

    @Override
    public KasperCommandGateway commandGateway() {
        return commandGateway;
    }

    @Override
    public KasperQueryGateway queryGateway() {
        return queryGateway;
    }

    @Override
    public MetricRegistry metricRegistry() {
        return metricRegistry;
    }

    @Override
    public SagaManager sagaManager() {
        return sagaManager;
    }

    @Override
    public Config configuration() {
        return configuration;
    }

    @Override
    public List<ExtraComponent> extraComponents() {
        return extraComponents;
    }

    @Override
    public List<CommandInterceptorFactory> commandInterceptorFactories() {
        return commandInterceptorFactories;
    }

    @Override
    public List<QueryInterceptorFactory> queryInterceptorFactories() {
        return queryInterceptorFactories;
    }

    @Override
    public List<EventInterceptorFactory> eventInterceptorFactories() {
        return eventInterceptorFactories;
    }

    @Override
    public DomainDescriptorFactory domainDescriptorFactory() {
        return domainDescriptorFactory;
    }

}
