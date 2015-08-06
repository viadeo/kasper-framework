// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.configuration;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.core.component.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptorFactory;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.component.command.KasperCommandBus;
import com.viadeo.kasper.core.component.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.command.interceptor.CommandValidationInterceptorFactory;
import com.viadeo.kasper.core.component.gateway.KasperQueryGateway;
import com.viadeo.kasper.core.component.query.interceptor.CacheInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.QueryFilterInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.QueryValidationInterceptorFactory;
import com.viadeo.kasper.core.component.event.interceptor.EventValidationInterceptorFactory;
import com.viadeo.kasper.core.component.saga.SagaManager;
import com.viadeo.kasper.core.component.saga.spring.SagaConfiguration;
import com.viadeo.kasper.core.component.saga.step.StepProcessor;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.UnitOfWorkFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;

import static com.viadeo.kasper.core.component.eventbus.KasperEventBus.Policy;

/**
 * The KasperPlatformConfiguration class provides default implementation of the components required by the  {@link com.viadeo.kasper.client.platform.Platform}.
 *
 * @see com.viadeo.kasper.client.platform.Platform.Builder
 */
public class KasperPlatformConfiguration implements PlatformConfiguration {

    private final KasperEventBus eventBus;
    private final KasperQueryGateway queryGateway;
    private final MetricRegistry metricRegistry;
    private final Config configuration;
    private final KasperCommandGateway commandGateway;
    private final SagaManager sagaManager;
    private final Map<Platform.ExtraComponentKey, Object> extraComponents;
    private final List<CommandInterceptorFactory> commandInterceptorFactories;
    private final List<QueryInterceptorFactory> queryInterceptorFactories;
    private final List<EventInterceptorFactory> eventInterceptorFactories;
    private final DomainDescriptorFactory domainDescriptorFactory;

    // ------------------------------------------------------------------------

    public KasperPlatformConfiguration() {
        this.eventBus = new KasperEventBus(Policy.ASYNCHRONOUS);
        this.queryGateway = new KasperQueryGateway();
        this.metricRegistry = new MetricRegistry();
        this.extraComponents = Maps.newHashMap();
        this.configuration = ConfigFactory.empty();

        final UnitOfWorkFactory uowFactory = new DefaultUnitOfWorkFactory();
        final KasperCommandBus commandBus = new KasperCommandBus();
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
        applicationContext.register(SagaConfiguration.class);

        final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.registerSingleton("eventBus", eventBus);
        beanFactory.registerSingleton("queryGateway", queryGateway);
        beanFactory.registerSingleton("commandGateway", commandGateway);
        beanFactory.registerSingleton("configuration", configuration);
        beanFactory.registerSingleton("metricRegistry", metricRegistry);
        beanFactory.registerSingleton("objectMapper", ObjectMapperProvider.INSTANCE.mapper());
        applicationContext.refresh();

        this.sagaManager = applicationContext.getBean(SagaManager.class);
        this.domainDescriptorFactory = new DomainDescriptorFactory(applicationContext.getBean(StepProcessor.class));
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
    public Map<Platform.ExtraComponentKey, Object> extraComponents() {
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
