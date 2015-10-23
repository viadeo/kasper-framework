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
import com.viadeo.kasper.core.component.command.gateway.KasperCommandBus;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.command.interceptor.CommandValidationInterceptorFactory;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.event.interceptor.EventValidationInterceptorFactory;
import com.viadeo.kasper.core.component.event.saga.DefaultSagaManager;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.event.saga.spring.SagaConfiguration;
import com.viadeo.kasper.core.component.event.saga.step.StepProcessor;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.cache.CacheInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.filter.QueryFilterInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.validation.QueryValidationInterceptorFactory;
import com.viadeo.kasper.platform.ExtraComponent;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptorFactory;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
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

        final UnitOfWorkFactory uowFactory = new DefaultUnitOfWorkFactory();
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
