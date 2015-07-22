// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.RepositoryManager;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.event.CommandEventListener;
import com.viadeo.kasper.core.component.event.EventListener;
import com.viadeo.kasper.core.component.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import com.viadeo.kasper.core.component.saga.Saga;
import com.viadeo.kasper.core.component.saga.SagaExecutor;
import com.viadeo.kasper.core.component.saga.SagaManager;
import com.viadeo.kasper.core.component.saga.SagaWrapper;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptor;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class PlatformWirer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformWirer.class);

    private final KasperEventBus eventBus;
    private final KasperCommandGateway commandGateway;
    private final KasperQueryGateway queryGateway;
    private final SagaManager sagaManager;
    private final RepositoryManager repositoryManager;
    private final DomainDescriptorFactory domainDescriptorFactory;
    private final List<ExtraComponent> extraComponents;
    private final Config config;
    private final MetricRegistry metricRegistry;

    public PlatformWirer(
            final Config config,
            final MetricRegistry metricRegistry,
            final KasperEventBus eventBus,
            final KasperCommandGateway commandGateway,
            final KasperQueryGateway queryGateway,
            final SagaManager sagaManager,
            final RepositoryManager repositoryManager
    ) {
        this.config = checkNotNull(config);
        this.metricRegistry = checkNotNull(metricRegistry);
        this.eventBus = checkNotNull(eventBus);
        this.commandGateway = checkNotNull(commandGateway);
        this.queryGateway = checkNotNull(queryGateway);
        this.sagaManager = checkNotNull(sagaManager);
        this.repositoryManager = checkNotNull(repositoryManager);
        this.domainDescriptorFactory = new DomainDescriptorFactory();
        this.extraComponents = Lists.newArrayList();
    }

    public DomainDescriptor wire(DomainBundle bundle) {
        bundle.configure(
                new Platform.BuilderContext(
                        config,
                        eventBus,
                        commandGateway,
                        queryGateway,
                        metricRegistry,
                        extraComponents
                )
        );

        for (final Repository repository : bundle.getRepositories()) {
            repository.init();
            repository.setEventBus(eventBus);
            repositoryManager.register(repository);
        }

        for (final CommandHandler commandHandler : bundle.getCommandHandlers()) {
            commandHandler.setEventBus(eventBus);
            commandHandler.setRepositoryManager(repositoryManager);
            commandGateway.register(commandHandler);
        }

        for (final QueryHandler queryHandler : bundle.getQueryHandlers()) {
            queryHandler.setEventBus(eventBus);
            queryGateway.register(queryHandler);
        }

        for (final EventListener eventListener : bundle.getEventListeners()) {
            eventListener.setEventBus(eventBus);

            if (CommandEventListener.class.isAssignableFrom(eventListener.getClass())) {
                final CommandEventListener commandEventListener = (CommandEventListener) eventListener;
                commandEventListener.setCommandGateway(commandGateway);
            }
            eventBus.subscribe(eventListener);
        }

        for (final Saga saga : bundle.getSagas()) {
            final SagaExecutor executor = sagaManager.register(saga);
            eventBus.subscribe(new SagaWrapper(executor));
        }

        for (final CommandInterceptorFactory factory : bundle.getCommandInterceptorFactories()) {
            wire(factory);
        }
        for (final QueryInterceptorFactory factory : bundle.getQueryInterceptorFactories()) {
            wire(factory);
        }

        for (final EventInterceptorFactory factory : bundle.getEventInterceptorFactories()) {
            wire(factory);
        }

        return domainDescriptorFactory.createFrom(bundle);
    }

    public void wire(CommandInterceptorFactory factory) {
        commandGateway.register(factory);
    }
    public void wire(QueryInterceptorFactory factory) {
        queryGateway.register(factory);
    }

    public void wire(EventInterceptorFactory factory) {
        eventBus.register(factory);
    }

    public void register(ExtraComponent extraComponent) {
        checkNotNull(extraComponent);
        extraComponents.add(extraComponent);
    }
}
